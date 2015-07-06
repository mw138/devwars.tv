package com.bezman.controller;

import com.bezman.Reference.*;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.*;
import com.bezman.model.*;
import com.bezman.service.Security;
import com.bezman.service.UserService;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.internal.SessionImpl;
import org.hibernate.metamodel.relational.Database;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.URISyntaxException;
import java.sql.Ref;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Terence on 1/26/2015.
 */
@Controller
@RequestMapping(value = "/v1/user")
public class UserController extends BaseController
{
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/")
    public ResponseEntity user(HttpServletRequest request, HttpServletResponse response)
    {
        User currentUser = (User) request.getAttribute("user");

        if(currentUser != null)
        {
            if(currentUser.getRanking() == null)
            {
                Ranking ranking = new Ranking();
                ranking.setId(currentUser.getId());
                currentUser.setRanking(ranking);

                DatabaseUtil.saveObjects(true, ranking);

                Session session = DatabaseManager.getSession();
                session.beginTransaction();

                session.createQuery("update ConnectedAccount set disconnected = true where username = ''");

                session.getTransaction().commit();
                session.close();
            }

            Session session = DatabaseManager.getSession();
            session.beginTransaction();

            currentUser = (User) session.get(User.class, currentUser.getId());

            currentUser.tryAllBadges();

            session.getTransaction().commit();
            session.close();
        } else {
            return new ResponseEntity(HttpMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity(Reference.gson.toJson(currentUser), HttpStatus.OK);
    }

    @RequestMapping("/activity")
    @PreAuthorization(minRole = User.Role.PENDING)
    public ResponseEntity getActivities(HttpServletRequest request, HttpServletResponse response)
    {
        User currentUser = (User) request.getAttribute("user");

        Session session = DatabaseManager.getSession();

        List<Activity> results = session.createCriteria(Activity.class)
                .add(Restrictions.eq("affectedUser", currentUser))
                .addOrder(Order.desc("timestamp"))
                .setMaxResults(200)
                .list();

        session.close();

        return new ResponseEntity(Reference.gson.toJson(results), HttpStatus.OK);
    }


    @RequestMapping("/create")
    public ResponseEntity createUser(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam("username") String username,
                                     @RequestParam("email") String email,
                                     @RequestParam("password") String password,
                                     @RequestParam("captchaResponse") String rcResponse,
                                     @RequestParam(value = "referral", required = false, defaultValue = "-1") int referral)
    {
        ResponseEntity responseEntity = null;
        String uid = Util.randomText(32);

        System.out.println("Referral : " + referral);

        JSONArray conflictJSONArray = new JSONArray();

        boolean emailTaken = UserService.userForEmail(email) != null;
        boolean usernameTaken = UserService.userForUsername(username) != null;
        boolean usernameValid = username.matches("^([A-Za-z0-9\\-_]+)$");
        boolean captchaValid = Reference.recaptchaValid(rcResponse, request.getRemoteAddr());
        boolean usernameLengthGood = username.length() <= 25 && username.length() >= 4;
        boolean passwordLengthGood = password.length() >= 6;

        boolean emailValid = false;

        if(emailTaken)
        {
            conflictJSONArray.put("Email already taken");
        }

        if(usernameTaken)
        {
            conflictJSONArray.put("Username already taken");
        }

        if(!emailTaken)
        {
            emailValid = EmailValidator.getInstance().isValid(email);

            if (!emailValid)
            {
                conflictJSONArray.put("Invalid Email");
            }
        }

        if(!captchaValid)
        {
            conflictJSONArray.put("Invalid Captcha Response");
        }

        if(!usernameLengthGood)
        {
            conflictJSONArray.put("Username must be at least 4 characters and at most 25 characters");
        }

        if(!passwordLengthGood)
        {
            conflictJSONArray.put("Password must be at least 6 characters");
        }

        if(!usernameValid)
        {
            conflictJSONArray.put("Username can contain characters, numbers and underscores");
        }

        if(!emailTaken && !usernameTaken && emailValid && captchaValid && usernameLengthGood && passwordLengthGood && usernameValid)
        {
            Session session = DatabaseManager.getSession();
            session.beginTransaction();

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setEncryptedPassword(password);
            user.setRole(User.Role.PENDING.toString());
            user.setAvatarChanges(1);

            session.save(user);
            session.flush();

            EmailConfirmation confirmation = new EmailConfirmation();
            confirmation.setId(user.getId());
            confirmation.setUid(uid);

            session.save(confirmation);

            session.getTransaction().commit();

            String subject = "DevWars account confirmation";
            String message = "Click here to confirm your account : " + Reference.rootURL + "/v1/user/validate?uid=" + uid;
            Util.sendEmail(Security.emailUsername, Security.emailPassword, subject, message, email);

            responseEntity = new ResponseEntity("We have sent an email to " + email, HttpStatus.OK);

            session.close();

            if(referral != -1)
            {
                User referrer = UserService.getUser(referral);

                if (referrer != null)
                {
                    referrer.setReferredUsers(referrer.getReferredUsers() + 1);

                    Integer referrerDevBits = -1;
                    switch(referrer.getReferredUsers())
                    {
                        case 1: referrerDevBits = 285; break;
                        case 5: referrerDevBits = 570; break;
                        case 10: referrerDevBits = 855; break;
                        case 25: referrerDevBits = 1710; break;
                        case 50: referrerDevBits = 2850; break;
                    }

                    if (referrerDevBits != -1)
                    {
                        referrer.getRanking().addPoints(referrerDevBits);
                        Activity activity = new Activity(referrer, user, "You earned " + referrerDevBits + " DevBits by referring " + referrer.getReferredUsers() + " users", referrerDevBits, 0);
                        DatabaseUtil.saveObjects(false, activity);
                    }

                    DatabaseUtil.saveOrUpdateObjects(false, referrer, referrer.getRanking());
                }
            }

            Activity activity = new Activity(user, user, "Created a DevWars account", 0, 0);
            DatabaseUtil.saveObjects(false, activity);
        }else
        {
            responseEntity = new ResponseEntity(conflictJSONArray.toString(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping(value = "/{id}")
    public ResponseEntity getUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id)
    {
        ResponseEntity responseEntity = null;

        User user = UserService.getUser(id);

        if(user.getRanking() == null)
        {
            Ranking ranking = new Ranking();
            ranking.setId(user.getId());
            user.setRanking(ranking);

            DatabaseUtil.saveObjects(true, ranking);
        }

        if (user != null)
        {
            responseEntity = new ResponseEntity(user.toString(), HttpStatus.OK);
        } else
        {
            responseEntity = new ResponseEntity(HttpMessages.NO_USER_FOUND, HttpStatus.NOT_FOUND);
        }


        return responseEntity;
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/delete")
    public ResponseEntity deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id)
    {
        ResponseEntity responseEntity = null;

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        User user = UserService.getUser(id);
        if (user != null)
        {
            session.delete(user);
            session.getTransaction().commit();
            responseEntity = new ResponseEntity(user.toString(), HttpStatus.OK);
        } else
        {
            session.getTransaction().rollback();
            responseEntity = new ResponseEntity(HttpMessages.NO_USER_FOUND, HttpStatus.NOT_FOUND);
        }

        session.close();

        return responseEntity;
    }

    @PreAuthorization(minRole = User.Role.NONE)
    @RequestMapping("/validate")
    public ResponseEntity validateUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("uid") String uid) throws IOException
    {
        ResponseEntity responseEntity = null;
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        Query query = session.createQuery("from EmailConfirmation where uid = :uid");
        query.setString("uid", uid);

        EmailConfirmation confirmation = (EmailConfirmation) DatabaseUtil.getFirstFromQuery(query);

        if (request.getAttribute("user") != null)
        {
            response.sendRedirect("/");
            return null;
        }

        if (confirmation != null)
        {
            int user_id = confirmation.getId();

            Query userQuery = session.createQuery("from User where id = :id");
            userQuery.setInteger("id", user_id);

            User user = (User) DatabaseUtil.getFirstFromQuery(userQuery);

            user.setRole(User.Role.USER.toString());

            session.delete(confirmation);
            session.saveOrUpdate(user);

            Activity activity = new Activity(user, user, "You validated your email", 0, 0);
            session.save(activity);

            session.getTransaction().commit();

            response.sendRedirect("/#/verify?username=" + user.getUsername());

            responseEntity = null;
        } else responseEntity = new ResponseEntity("You were not validated", HttpStatus.CONFLICT);

        session.close();

        return responseEntity;
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/addpoints")
    public ResponseEntity addPoints(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id, @RequestParam(value = "points", required = false, defaultValue = "0") double points, @RequestParam(value = "xp", required = false, defaultValue = "0") double xp)
    {
        ResponseEntity responseEntity = null;

        User user = UserService.getUser(id);

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        if (user != null)
        {
            if (user.getRanking() == null)
            {
                Ranking ranking = new Ranking();
                ranking.setId(user.getId());
                user.setRanking(ranking);
            }

            user.getRanking().setPoints(user.getRanking().getPoints() + points);
            user.getRanking().setXp(user.getRanking().getXp() + xp);
            session.saveOrUpdate(user.getRanking());

            Activity activity = new Activity(user, (User) request.getAttribute("user"), "You received points/xp", (int) points, (int) xp);
            session.save(activity);

            Query query = session.createQuery("from Player p where p.user.id = :id");
            query.setInteger("id", id);

            Player player = (Player) DatabaseUtil.getFirstFromQuery(query);

            if (player != null)
            {
                player.setPointsChanged((int) (player.getPointsChanged() + points));
                session.saveOrUpdate(player);
            }

            responseEntity = new ResponseEntity(user.toString(), HttpStatus.OK);
        }

        session.getTransaction().commit();
        session.close();

        return responseEntity;
    }

    @RequestMapping(value = "/login")
    public Object login(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username, @RequestParam("password") String password) throws URISyntaxException
    {
        Object responseObject = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where lower(username) = :username");
        query.setString("username", username.toLowerCase());

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        if (user != null && user.getUnencryptedPassword().equals(password))
        {
            if (user.role != User.Role.PENDING)
            {
                String newToken = user.newSession();
                Cookie cookie = new Cookie("token", newToken);
                cookie.setPath("/");

                response.addCookie(cookie);
                responseObject = new ResponseEntity(Reference.gson.toJson(user), HttpStatus.OK);
            } else
            {
                responseObject = new ResponseEntity("You must validate your email before signing in", HttpStatus.CONFLICT);
            }
        } else
        {
            responseObject = new ResponseEntity(HttpMessages.INCORRECT_USERNAME_OR_PASSWORD, HttpStatus.UNAUTHORIZED);
        }

        return responseObject;
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response)
    {
        User currentUser = (User) request.getAttribute("user");

        currentUser.logout();

        return new ResponseEntity("Logged out successfully", HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/appliedgames")
    public ResponseEntity appliedGames(HttpServletRequest request, HttpServletResponse response)
    {
        List<Game> games = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("select g.game from GameSignup g where g.user.id = :id order by g.game.timestamp desc");
        query.setParameter("id", ((User) request.getAttribute("user")).getId());
        query.setMaxResults(50);

        games = query.list();

        session.close();

        return new ResponseEntity(Reference.gson.toJson(games), HttpStatus.OK);
    }

//    @RequestMapping("/initreset")
//    public ResponseEntity initReset(HttpServletRequest request, HttpServletResponse response,
//                                    @RequestParam("email") String email)
//    {
//
//        Session session = DatabaseManager.getSession();
//
//        Query query = session.createQuery("from User where email = :email and provider = null");
//        query.setString("email", email);
//
//        User user = (User) DatabaseUtil.getFirstFromQuery(query);
//
//        session.close();
//
//        if (user != null)
//        {
//            String token = Util.randomText(64);
//
//            UserReset userReset = new UserReset();
//            userReset.setUid(token);
//            userReset.setId(user.getId());
//
//            DatabaseUtil.saveOrUpdateObjects(false, userReset);
//
//            Util.sendEmail(Security.emailUsername, Security.emailPassword, "DevWars password reset", token, user.getEmail());
//        } else
//        {
//            return new ResponseEntity("Can't find user for given email", HttpStatus.BAD_REQUEST);
//        }
//
//        return null;
//    }
//
//    @RequestMapping("/resetpassword")
//    public ResponseEntity resetPassword(HttpServletRequest request, HttpServletResponse response,
//                                        @RequestParam("newpassword") String password,
//                                        @RequestParam("uid") String uid)
//    {
//
//        Session session = DatabaseManager.getSession();
//
//        Query query = session.createQuery("from User u where u.passwordReset.uid = :uid");
//        query.setString("uid", uid);
//
//        User user = (User) DatabaseUtil.getFirstFromQuery(query);
//
//        session.close();
//
//        if (user != null)
//        {
//            user.setEncryptedPassword(password);
//
//            DatabaseUtil.saveOrUpdateObjects(false, user);
//            DatabaseUtil.deleteObjects(user.getPasswordReset());
//
//            return new ResponseEntity("Successfully changed password", HttpStatus.OK);
//        } else
//        {
//            return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
//        }
//    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/changepassword")
    public ResponseEntity changePassword(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("currentPassword") String currentPassword,
                                         @RequestParam("newPassword") String newPassword)
    {
        User user = (User) request.getAttribute("user");

        if(!user.isNative())
        {
            return new ResponseEntity("You can't have a password, you're not native", HttpStatus.CONFLICT);
        }

        if (user.getUnencryptedPassword().equals(currentPassword))
        {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("update User set password = :password where id = :id");
            query.setString("password", Security.encrypt(newPassword));
            query.setInteger("id", user.getId());

            query.executeUpdate();

            session.close();

            return new ResponseEntity("Success", HttpStatus.OK);
        } else
        {
            return new ResponseEntity("Incorrect Password", HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/changeemail")
    public ResponseEntity changeEmail(HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam("currentPassword") String currentPassword,
                                      @RequestParam("newEmail") String newEmail)
    {
        User user = (User) request.getAttribute("user");

        if (!user.isNative() || user.getUnencryptedPassword().equals(currentPassword))
        {
            if(EmailValidator.getInstance().isValid(newEmail))
            {
                Session session = DatabaseManager.getSession();
                Query query = session.createQuery("update User set email = :email where id = :id");
                query.setString("email", newEmail);
                query.setInteger("id", user.getId());

                query.executeUpdate();

                session.close();

                return new ResponseEntity("Success", HttpStatus.OK);
            } else
            {
                return new ResponseEntity("Invalid Email", HttpStatus.BAD_REQUEST);
            }
        } else
        {
            return new ResponseEntity("Incorrect Password", HttpStatus.FORBIDDEN);
        }
    }


    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/changeavatar")
    public ResponseEntity changeAvatar(HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam("file") MultipartFile image) throws IOException
    {
        if (image.isEmpty())
        {
            return new ResponseEntity("No image was uploaded", HttpStatus.BAD_REQUEST);
        } else
        {
            User currentUser = (User) request.getAttribute("user");

            if(currentUser.getAvatarChanges() > 0)
            {
                File file = new File(Reference.PROFILE_PICTURE_PATH + currentUser.getId() + File.separator + "avatar.jpeg");

                if (!file.exists())
                {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                bufferedOutputStream.write(image.getBytes());
                bufferedOutputStream.close();

                currentUser.setAvatarChanges(currentUser.getAvatarChanges() - 1);

                DatabaseUtil.mergeObjects(false, currentUser);

                return new ResponseEntity("Successfully uploaded image", HttpStatus.OK);
            } else
            {
                return new ResponseEntity("You need to purchase more [Avatar Changes] from the BitShop", HttpStatus.CONFLICT);
            }
        }
    }

    @RequestMapping("/{username}/public")
    public ResponseEntity getPublicUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("username") String username) throws IOException, ServletException
    {
        User currentUser = UserService.userForUsername(username);

        if (currentUser != null)
        {
            JSONObject userJSON = (JSONObject) JSONValue.parse(Reference.gson.toJson(currentUser));
            userJSON.remove("warrior");
            userJSON.remove("connectedAccounts");
            userJSON.remove("email");
            return new ResponseEntity(userJSON, HttpStatus.OK);
        } else
        {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/{username}/avatar")
    public ResponseEntity getUserAvatar(HttpServletRequest request, HttpServletResponse response, @PathVariable("username") String username) throws IOException, ServletException
    {
        User currentUser = UserService.userForUsername(username);

        if (currentUser != null)
        {
            File file = new File(Reference.PROFILE_PICTURE_PATH + File.separator + currentUser.getId() + File.separator + "avatar.jpeg");

            System.out.println(file.getAbsolutePath());

            if(file.exists())
            {
                FileInputStream inputStream = new FileInputStream(file);
                IOUtils.copy(inputStream, response.getOutputStream());
                inputStream.close();
            } else
            {
                System.out.println("NA");
                request.getRequestDispatcher("/assets/img/default-avatar.png").forward(request, response);
            }

            return null;
        } else
        {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/avatar")
    public ResponseEntity getAvatar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        User currentUser = (User) request.getAttribute("user");

        File file = new File(Reference.PROFILE_PICTURE_PATH + File.separator + currentUser.getId() + File.separator + "avatar.jpeg");

        System.out.println(file.getAbsolutePath());

        if(file.exists())
        {
            FileInputStream inputStream = new FileInputStream(file);
            IOUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();
        } else
        {
            System.out.println("NA");
            request.getRequestDispatcher("/assets/img/default-avatar.png").forward(request, response);
        }

        return null;
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/updateinfo")
    public ResponseEntity updateInfo(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "username", required = false) String username,
                                     @RequestParam(value = "url", required = false) String url,
                                     @RequestParam(value = "company", required = false) String company,
                                     @RequestParam(value = "location", required = false) String location)
    {
        User user = (User) request.getAttribute("user");

        if (username != null && !user.getUsername().equalsIgnoreCase(username))
        {
            if(user.getUsernameChanges() < 1)
            {
                return new ResponseEntity("Not enough username changes", HttpStatus.CONFLICT);
            } else
            {
                user.setUsername(username);
                user.setUsernameChanges(user.getUsernameChanges() - 1);
            }
        }

        if (location != null)
        {
            user.setLocation(location);
        }

        if (url != null)
        {
            user.setUrl(url);
        }

        if (company != null)
        {
            user.setCompany(company);
        }

        DatabaseUtil.mergeObjects(false, user);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/claimtwitch")
    public ResponseEntity claimTwitch(HttpServletRequest request, HttpServletResponse response)
    {
        User user = (User) request.getAttribute("user");

        ConnectedAccount connectedAccount = null;

        for(ConnectedAccount account : user.getConnectedAccounts())
        {
            if (account.getProvider().equals("TWITCH")) connectedAccount = account;
        }

        if (connectedAccount != null)
        {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("from User where username = :username and veteran = true");
            query.setString("username", connectedAccount.getUsername());
            System.out.println(connectedAccount.getUsername());

            User veteranUser = (User) DatabaseUtil.getFirstFromQuery(query);

            session.close();

            //Veteran to new
            if (veteranUser != null)
            {
                session = DatabaseManager.getSession();
                Query allPlayersQuery = session.createQuery("from Player p where p.user.id = :id");
                allPlayersQuery.setInteger("id", veteranUser.getId());

                List<Player> players = allPlayersQuery.list();

                session.beginTransaction();

                for(Player player : players)
                {
                    player.setUser(user);
                    session.update(player);
                }

                session.getTransaction().commit();

                if (veteranUser.getRanking() != null && user.getRanking() != null)
                {
                    Query updatePoints = session.createQuery("update Ranking set points = :points, xp = :xp where id = :id");
                    updatePoints.setDouble("points", user.getRanking().getPoints() + veteranUser.getRanking().getPoints());
                    updatePoints.setDouble("xp", user.getRanking().getXp() + veteranUser.getRanking().getXp());
                    updatePoints.setInteger("id", user.getId());
                    updatePoints.executeUpdate();
                }

                System.out.println("Username update");
                Query updateUsernameQuery = session.createQuery("update User set username = :username where id = :id");
                updateUsernameQuery.setString("username", veteranUser.getUsername());
                updateUsernameQuery.setInteger("id", user.getId());
                updateUsernameQuery.executeUpdate();

                session.close();

                session = DatabaseManager.getSession();
                session.beginTransaction();

                session.delete(veteranUser);

                if (veteranUser.getRanking() != null) session.delete(veteranUser.getRanking());

                session.getTransaction().commit();
                session.close();

                return new ResponseEntity("Successfully transferred accounts", HttpStatus.OK);
            } else
            {
                return new ResponseEntity("Veteran not found", HttpStatus.NOT_FOUND);
            }
        } else
        {
            return new ResponseEntity("Twitch account not found", HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/releasetwitch")
    public ResponseEntity releaseUsername(HttpServletRequest request, HttpServletResponse response)
    {
        User user = (User) request.getAttribute("user");

        ConnectedAccount connectedAccount = null;

        for(ConnectedAccount account : user.getConnectedAccounts())
        {
            if (account.getProvider().equals("TWITCH")) connectedAccount = account;
        }

        if (connectedAccount != null)
        {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("from User where username = :username and veteran = true");
            query.setString("username", connectedAccount.getUsername());
            System.out.println(connectedAccount.getUsername());

            User veteranUser = (User) DatabaseUtil.getFirstFromQuery(query);

            session.close();

            //Veteran to new
            if (veteranUser != null)
            {
                session = DatabaseManager.getSession();
                Query allPlayersQuery = session.createQuery("from Player p where p.user.id = :id");
                allPlayersQuery.setInteger("id", veteranUser.getId());

                List<Player> players = allPlayersQuery.list();

                session.beginTransaction();

                for(Player player : players)
                {
                    player.setUser(user);
                    session.update(player);
                }

                session.getTransaction().commit();

                session.refresh(user);

                System.out.println("updated username : " + user.getUsername());

                if (veteranUser.getRanking() != null && user.getRanking() != null)
                {
                    Query updatePoints = session.createQuery("update Ranking set points = :points, xp = :xp where id = :id");
                    updatePoints.setDouble("points", user.getRanking().getPoints() + veteranUser.getRanking().getPoints());
                    updatePoints.setDouble("xp", user.getRanking().getXp() + veteranUser.getRanking().getXp());
                    updatePoints.setInteger("id", user.getId());
                    updatePoints.executeUpdate();
                }

                session.close();

                session = DatabaseManager.getSession();
                session.beginTransaction();

                session.delete(veteranUser);

                if (veteranUser.getRanking() != null) session.delete(veteranUser.getRanking());

                session.getTransaction().commit();
                session.close();

                return new ResponseEntity("Successfully transferred accounts", HttpStatus.OK);
            } else
            {
                return new ResponseEntity("Veteran not found", HttpStatus.NOT_FOUND);
            }
        } else
        {
            return new ResponseEntity("Twitch account not found", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/testemail")
    public ResponseEntity testEmail(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("email") String email,
                                    @RequestParam("uid") String uid) throws UnirestException
    {
        String html = Unirest.get(Reference.rootURL + "/assets/email/verification.html")
                .asString()
                .getBody();
        String message = html.replace("{{verifyLink}}", Reference.rootURL + "/v1/user/validate?uid=" + uid);


        Util.sendEmail(Security.emailUsername, Security.emailPassword, "test", message, email);

        return null;
    }

    @UnitOfWork
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/notifications")
    public ResponseEntity getUnreadNotifications(@AuthedUser User user, SessionImpl session)
    {
        List results = session.createCriteria(Notification.class)
                .add(Restrictions.eq("user.id", user.getId()))
                .add(Restrictions.eq("hasRead", false))
                .list();

        return new ResponseEntity(Reference.gson.toJson(results), HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @Transactional
    @RequestMapping(value = "/notifications/read", method = RequestMethod.POST)
    public ResponseEntity markNotificationsAsRead(@JSONParam("notifications") Notification[] notificationList, @AuthedUser User user, SessionImpl session)
    {
        List notificationIDs = Arrays.asList(notificationList).stream()
                .map(a -> a.getId())
                .collect(Collectors.toList());

        if(notificationList.length > 0)
        {
            List<Notification> notifications = session.createCriteria(Notification.class)
                    .add(Restrictions.eq("user.id", user.getId()))
                    .add(Restrictions.in("id", notificationIDs))
                    .list();

            notifications.stream()
                    .forEach(a -> a.setHasRead(true));
        }
//
        return new ResponseEntity(Reference.gson.toJson(notificationList), HttpStatus.OK);
    }



    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/badges")
    public ResponseEntity getBadges(HttpServletRequest request, HttpServletResponse response, @AuthedUser User user)
    {
        return new ResponseEntity(Reference.gson.toJson(user.getBadges()), HttpStatus.OK);
    }


}
