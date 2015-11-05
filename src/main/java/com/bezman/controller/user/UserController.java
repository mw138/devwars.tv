package com.bezman.controller.user;

import com.bezman.Reference.HttpMessages;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.*;
import com.bezman.controller.BaseController;
import com.bezman.exception.NonDevWarsUserException;
import com.bezman.exception.UserNotFoundException;
import com.bezman.init.DatabaseManager;
import com.bezman.model.*;
import com.bezman.service.Security;
import com.bezman.service.UserService;
import com.bezman.service.UserTeamService;
import com.dropbox.core.DbxException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by Terence on 1/26/2015.
 */
@Controller
@RequestMapping(value = "/v1/user")
public class UserController extends BaseController {
    @Autowired
    UserService userService;

    @Autowired
    Security security;

    @Autowired
    UserTeamService userTeamService;

    /**
     * Gets the signed in user
     *
     * @param session
     * @param user
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/")
    public ResponseEntity user(SessionImpl session, @AuthedUser User user) {
        User currentUser = (User) session.merge(user);

        if (currentUser.getRanking() == null) {
            Ranking ranking = new Ranking();
            ranking.setId(currentUser.getId());

            currentUser.setRanking(ranking);

            session.save(ranking);
        }

        List<Badge> badgesToAward = currentUser.tryAllBadges();

        badgesToAward.stream()
                .filter(a -> !currentUser.hasBadge(a))
                .forEach(badge -> {
                    currentUser.awardBadge(badge);
                    session.save(new Notification(currentUser, "Badge Get : " + badge.getName(), false));
                    session.save(new Activity(currentUser, currentUser, "You earned a badge : " + badge.getName(), badge.getBitsAwarded(), badge.getXpAwarded()));
                });

        return new ResponseEntity(user, HttpStatus.OK);
    }

    /**
     * Retrieves the signed in user's activity
     *
     * @param request
     * @param response
     * @return Signed in User's Activity
     */
    @RequestMapping("/activity")
    @PreAuthorization(minRole = User.Role.PENDING)
    public ResponseEntity getActivities(HttpServletRequest request, HttpServletResponse response) {
        User currentUser = (User) request.getAttribute("user");

        Session session = DatabaseManager.getSession();

        List<Activity> results = session.createCriteria(Activity.class)
                .add(Restrictions.eq("affectedUser", currentUser))
                .addOrder(Order.desc("timestamp"))
                .setMaxResults(200)
                .list();

        session.close();

        return new ResponseEntity(results, HttpStatus.OK);
    }


    /**
     * Sign up method for the user
     *
     * @param request
     * @param response
     * @param username   Username for new user
     * @param email      Email for new user
     * @param password   Password for new user
     * @param rcResponse The Google Recaptcha response sent from the client
     * @param referral   (Optional) The person which referred them
     * @return
     */
    @RequestMapping("/create")
    public ResponseEntity createUser(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam("username") String username,
                                     @RequestParam("email") String email,
                                     @RequestParam("password") String password,
                                     @RequestParam("captchaResponse") String rcResponse,
                                     @RequestParam(value = "referral", required = false) String referral) {
        ResponseEntity responseEntity = null;
        String uid = Util.randomText(32);

        System.out.println("Referral : " + referral);

        JSONArray conflictJSONArray = new JSONArray();

        boolean emailTaken = userService.userForEmail(email) != null;
        boolean usernameTaken = userService.userForUsername(username) != null;
        boolean usernameValid = username.matches("^([A-Za-z0-9\\-_]+)$");
        boolean captchaValid = Reference.recaptchaValid(rcResponse, request.getRemoteAddr());
        boolean usernameLengthGood = username.length() <= 25 && username.length() >= 4;
        boolean passwordLengthGood = password.length() >= 6;

        boolean emailValid = false;

        if (emailTaken) {
            conflictJSONArray.put("Email already taken");
        }

        if (usernameTaken) {
            conflictJSONArray.put("Username already taken");
        }

        if (!emailTaken) {
            emailValid = EmailValidator.getInstance().isValid(email);

            if (!emailValid) {
                conflictJSONArray.put("Invalid Email");
            }
        }

        if (!captchaValid) {
            conflictJSONArray.put("Invalid Captcha Response");
        }

        if (!usernameLengthGood) {
            conflictJSONArray.put("Username must be at least 4 characters and at most 25 characters");
        }

        if (!passwordLengthGood) {
            conflictJSONArray.put("Password must be at least 6 characters");
        }

        if (!usernameValid) {
            conflictJSONArray.put("Username can contain characters, numbers and underscores");
        }

        if (!emailTaken && !usernameTaken && emailValid && captchaValid && usernameLengthGood && passwordLengthGood && usernameValid) {
            Session session = DatabaseManager.getSession();
            session.beginTransaction();

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(security.hash(password));
            user.setRole(User.Role.PENDING);
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
            Util.sendEmail(Reference.getEnvironmentProperty("emailUsername"), Reference.getEnvironmentProperty("emailPassword"), subject, message, email);

            responseEntity = new ResponseEntity("We have sent an email to " + email, HttpStatus.OK);

            session.close();

            if (referral != null) {
                User referrer = userService.userForUsername(referral);

                if (referrer != null) {
                    referrer.setReferredUsers(referrer.getReferredUsers() + 1);

                    Activity activity = new Activity(referrer, user, "You referred " + username, 0, 0);
                    DatabaseUtil.saveObjects(false, activity);

                    DatabaseUtil.saveOrUpdateObjects(false, referrer, referrer.getRanking());
                }
            }

            Activity activity = new Activity(user, user, "Created a DevWars account", 0, 0);
            DatabaseUtil.saveObjects(false, activity);
        } else {
            responseEntity = new ResponseEntity(conflictJSONArray.toString(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    /**
     * Get a user
     *
     * @param request
     * @param response
     * @param id
     * @return Requested User
     */
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping(value = "/{id}")
    public ResponseEntity getUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id) {
        ResponseEntity responseEntity = null;

        User user = userService.getUser(id);

        if (user.getRanking() == null) {
            Ranking ranking = new Ranking();
            ranking.setId(user.getId());
            user.setRanking(ranking);

            DatabaseUtil.saveObjects(true, ranking);
        }

        if (user != null) {
            responseEntity = new ResponseEntity(user, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity(HttpMessages.NO_USER_FOUND, HttpStatus.NOT_FOUND);
        }


        return responseEntity;
    }

    /**
     * Remove a user from the system
     *
     * @param request
     * @param response
     * @param id
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/delete")
    public ResponseEntity deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id) {
        ResponseEntity responseEntity = null;

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        User user = userService.getUser(id);
        if (user != null) {
            session.delete(user);
            session.getTransaction().commit();
            responseEntity = new ResponseEntity(user.toString(), HttpStatus.OK);
        } else {
            session.getTransaction().rollback();
            responseEntity = new ResponseEntity(HttpMessages.NO_USER_FOUND, HttpStatus.NOT_FOUND);
        }

        session.close();

        return responseEntity;
    }

    @PreAuthorization(minRole = User.Role.USER)
    @UnitOfWork
    @RequestMapping("/search")
    public ResponseEntity searchUsers(SessionImpl session, @RequestParam("username") String username) {
        if (username.isEmpty()) {
            return new ResponseEntity("Query cannot be empty", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(userService.searchUsers(username), HttpStatus.OK);
    }

    /**
     * Validate the user's email
     *
     * @param request
     * @param response
     * @param uid      The UID sent in the email to confirm that they are who they say they are
     * @return
     * @throws IOException
     */
    @PreAuthorization(minRole = User.Role.NONE)
    @RequestMapping("/validate")
    public ResponseEntity validateUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("uid") String uid) throws IOException {
        ResponseEntity responseEntity;
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        Query query = session.createQuery("from EmailConfirmation where uid = :uid");
        query.setString("uid", uid);

        EmailConfirmation confirmation = (EmailConfirmation) DatabaseUtil.getFirstFromQuery(query);

        if (request.getAttribute("user") != null) {
            response.sendRedirect("/");
            return null;
        }

        if (confirmation != null) {
            int user_id = confirmation.getId();

            Query userQuery = session.createQuery("from User where id = :id");
            userQuery.setInteger("id", user_id);

            User user = (User) DatabaseUtil.getFirstFromQuery(userQuery);

            user.setRole(User.Role.USER);

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

    /**
     * Adds xp / devbits to the specified user
     *
     * @param request
     * @param response
     * @param id
     * @param points
     * @param xp
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/addpoints")
    public ResponseEntity addPoints(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id, @RequestParam(value = "points", required = false, defaultValue = "0") double points, @RequestParam(value = "xp", required = false, defaultValue = "0") double xp) {
        ResponseEntity responseEntity = null;

        User user = userService.getUser(id);

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        if (user != null) {
            if (user.getRanking() == null) {
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

            if (player != null) {
                player.setPointsChanged((int) (player.getPointsChanged() + points));
                session.saveOrUpdate(player);
            }

            responseEntity = new ResponseEntity(user.toString(), HttpStatus.OK);
        }

        session.getTransaction().commit();
        session.close();

        return responseEntity;
    }

    /**
     * Login the user
     *
     * @param request
     * @param response
     * @param username
     * @param password
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/login")
    public Object login(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username, @RequestParam("password") String password) throws URISyntaxException {
        Object responseObject = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where lower(username) = :username");
        query.setString("username", username.toLowerCase());

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        if (user != null && user.getPassword().equals(security.hash(password))) {
            if (user.getRole() != User.Role.PENDING) {
                String newToken = user.newSession();
                Cookie cookie = new Cookie("token", newToken);
                cookie.setPath("/");

                response.addCookie(cookie);
                responseObject = new ResponseEntity(user, HttpStatus.OK);
            } else {
                responseObject = new ResponseEntity("You must validate your email before signing in", HttpStatus.CONFLICT);
            }
        } else {
            responseObject = new ResponseEntity(HttpMessages.INCORRECT_USERNAME_OR_PASSWORD, HttpStatus.UNAUTHORIZED);
        }

        return responseObject;
    }

    /**
     * Logs out the current user
     *
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/logout")
    public ResponseEntity logout(@AuthedUser User user) {
        userService.logoutUser(user);

        return new ResponseEntity("Logged out successfully", HttpStatus.OK);
    }

    /**
     * Gets the applied games for the signed in user
     *
     * @param request
     * @param response
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/appliedgames")
    public ResponseEntity appliedGames(HttpServletRequest request, HttpServletResponse response) {
        List<Game> games = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("select g.game from GameSignup g where g.user.id = :id order by g.game.timestamp desc");
        query.setParameter("id", ((User) request.getAttribute("user")).getId());
        query.setMaxResults(50);

        games = query.list();

        session.close();

        return new ResponseEntity(games, HttpStatus.OK);
    }

    /**
     * Method to change password for the signed in user
     *
     * @param request
     * @param response
     * @param currentPassword
     * @param newPassword
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/changepassword")
    public ResponseEntity changePassword(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("currentPassword") String currentPassword,
                                         @RequestParam("newPassword") String newPassword) {
        User user = (User) request.getAttribute("user");

        if (!user.isNative()) {
            return new ResponseEntity("You can't have a password, you're not native", HttpStatus.CONFLICT);
        }

        if (user.getPassword().equals(security.hash(currentPassword))) {
            userService.changePasswordForUser(user, newPassword);

            return new ResponseEntity("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity("Incorrect Password", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Starts the reset password process
     *
     * @param response (Resolved)
     * @param email    Email of the user for the password to be reset
     * @return Appropriate response
     */
    @RequestMapping(value = "/initreset", method = RequestMethod.GET)
    public ResponseEntity initResetPassword(HttpServletResponse response, @RequestParam("email") String email) {
        try {
            userService.beginResetPasswordForEmail(email);

            return new ResponseEntity("An email has been sent to your email with a link to reset your password", HttpStatus.OK);
        } catch (NonDevWarsUserException e) {
            return new ResponseEntity("You must be a native DevWars User", HttpStatus.CONFLICT);
        } catch (UserNotFoundException e) {
            return new ResponseEntity("User was not found for " + e.param, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
    public ResponseEntity resetPassword(@RequestParam("user") int id,
                                        @RequestParam("key") String key,
                                        @RequestParam("password") String password,
                                        @RequestParam("password_confirmation") String passwordConfirmation) throws IOException {
        if (!password.equals(passwordConfirmation))
            return new ResponseEntity("Passwords must be the same", HttpStatus.BAD_REQUEST);

        if (password.length() < 6)
            return new ResponseEntity("Password must be at least six chars", HttpStatus.BAD_REQUEST);

        User user = userService.getUser(id);

        if (!userService.isResetKeyValidForUser(user, key))
            return new ResponseEntity("Invalid Reset Key", HttpStatus.BAD_REQUEST);

        userService.changePasswordForUser(user, password);
        userService.removeResetKeyFromUser(user);

        return new ResponseEntity("Success", HttpStatus.OK);
    }

    /**
     * Method to change the email for the signed in user
     *
     * @param request
     * @param response
     * @param currentPassword
     * @param newEmail
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/changeemail")
    public ResponseEntity changeEmail(HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam("currentPassword") String currentPassword,
                                      @RequestParam("newEmail") String newEmail) {
        User user = (User) request.getAttribute("user");

        if (!user.isNative() || user.getPassword().equals(security.hash(currentPassword))) {
            if (EmailValidator.getInstance().isValid(newEmail)) {
                Session session = DatabaseManager.getSession();
                Query query = session.createQuery("update User set email = :email where id = :id");
                query.setString("email", newEmail);
                query.setInteger("id", user.getId());

                query.executeUpdate();

                session.close();

                return new ResponseEntity("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity("Invalid Email", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity("Incorrect Password", HttpStatus.FORBIDDEN);
        }
    }


    /**
     * Method to change the avatar picture for the signed in user
     *
     * @param image
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/changeavatar")
    public ResponseEntity changeAvatar(@AuthedUser User user,
                                       @RequestParam("file") MultipartFile image) throws IOException, DbxException {
        userService.changeProfilePictureForUser(user, image.getInputStream());

        return new ResponseEntity("Successfully change profile picture", HttpStatus.OK);
    }

    /**
     * Returns public information for the user
     * Deprecated due to new json processing
     *
     * @param request
     * @param response
     * @param username
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("/{username}/public")
    public ResponseEntity getPublicUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("username") String username) throws IOException, ServletException {
        User currentUser = userService.userForUsername(username);

        if (currentUser != null) {
            return new ResponseEntity(currentUser, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets the Avatar for the given user
     *
     * @param request
     * @param response
     * @param username
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("/{username}/avatar")
    public ResponseEntity getUserAvatar(HttpServletRequest request, HttpServletResponse response, @PathVariable("username") String username) throws IOException, ServletException {
        User currentUser = userService.userForUsername(username);

        if (currentUser != null) {
            return new ResponseEntity(userService.pathForProfilePictureForUser(currentUser), HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets the signed in user's avatar
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/avatar")
    public ResponseEntity getAvatar(HttpServletRequest request, HttpServletResponse response, @AuthedUser User user) throws IOException, ServletException {
        return getUserAvatar(request, response, user.getUsername());
    }

    /**
     * Method to update the user's personal information
     *
     * @param request
     * @param response
     * @param username
     * @param url
     * @param company
     * @param location
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/updateinfo")
    public ResponseEntity updateInfo(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "username", required = false) String username,
                                     @RequestParam(value = "url", required = false) String url,
                                     @RequestParam(value = "company", required = false) String company,
                                     @RequestParam(value = "location", required = false) String location) {
        User user = (User) request.getAttribute("user");

        if (username != null && !user.getUsername().equalsIgnoreCase(username)) {
            user.setUsername(username);
        }

        if (location != null) {
            user.setLocation(location);
        }

        if (url != null) {
            user.setUrl(url);
        }

        if (company != null) {
            user.setCompany(company);
        }

        DatabaseUtil.mergeObjects(false, user);

        return new ResponseEntity("", HttpStatus.OK);
    }

    /**
     * Method for a veteran to claim his/her old twitch account username
     *
     * @param request
     * @param response
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/claimtwitch")
    public ResponseEntity claimTwitch(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getAttribute("user");

        ConnectedAccount connectedAccount = null;

        for (ConnectedAccount account : user.getConnectedAccounts()) {
            if (account.getProvider().equals("TWITCH")) connectedAccount = account;
        }

        if (connectedAccount != null) {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("from User where username = :username and veteran = true");
            query.setString("username", connectedAccount.getUsername());
            System.out.println(connectedAccount.getUsername());

            User veteranUser = (User) DatabaseUtil.getFirstFromQuery(query);

            session.close();

            //Veteran to new
            if (veteranUser != null) {
                session = DatabaseManager.getSession();
                Query allPlayersQuery = session.createQuery("from Player p where p.user.id = :id");
                allPlayersQuery.setInteger("id", veteranUser.getId());

                List<Player> players = allPlayersQuery.list();

                session.beginTransaction();

                for (Player player : players) {
                    player.setUser(user);
                    session.update(player);
                }

                session.getTransaction().commit();

                if (veteranUser.getRanking() != null && user.getRanking() != null) {
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
            } else {
                return new ResponseEntity("Veteran not found", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity("Twitch account not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method for the user to release their twitch account and keep their new username
     *
     * @param request
     * @param response
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/releasetwitch")
    public ResponseEntity releaseUsername(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getAttribute("user");

        ConnectedAccount connectedAccount = null;

        for (ConnectedAccount account : user.getConnectedAccounts()) {
            if (account.getProvider().equals("TWITCH")) connectedAccount = account;
        }

        if (connectedAccount != null) {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("from User where username = :username and veteran = true");
            query.setString("username", connectedAccount.getUsername());
            System.out.println(connectedAccount.getUsername());

            User veteranUser = (User) DatabaseUtil.getFirstFromQuery(query);

            session.close();

            //Veteran to new
            if (veteranUser != null) {
                session = DatabaseManager.getSession();
                Query allPlayersQuery = session.createQuery("from Player p where p.user.id = :id");
                allPlayersQuery.setInteger("id", veteranUser.getId());

                List<Player> players = allPlayersQuery.list();

                session.beginTransaction();

                for (Player player : players) {
                    player.setUser(user);
                    session.update(player);
                }

                session.getTransaction().commit();

                session.refresh(user);

                System.out.println("updated username : " + user.getUsername());

                if (veteranUser.getRanking() != null && user.getRanking() != null) {
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
            } else {
                return new ResponseEntity("Veteran not found", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity("Twitch account not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Made to test sending html emails
     *
     * @param request
     * @param response
     * @param email
     * @param uid
     * @return
     * @throws UnirestException
     * @throws MessagingException
     */
    @RequestMapping("/testemail")
    public ResponseEntity testEmail(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("email") String email,
                                    @RequestParam("uid") String uid) throws UnirestException, MessagingException {
        String html = Unirest.get(Reference.rootURL + "/assets/email/verification.html")
                .asString()
                .getBody();
        String message = html.replace("{{verifyLink}}", Reference.rootURL + "/v1/user/validate?uid=" + uid);


        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.user", Reference.getEnvironmentProperty("emailUsername"));
        properties.put("mail.smtp.password", Reference.getEnvironmentProperty("emailPassword"));
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);

        javax.mail.Session session = javax.mail.Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Reference.getEnvironmentProperty("emailUsername"), Reference.getEnvironmentProperty("emailPassword"));
            }
        });

        MimeMessage emailMessage = new MimeMessage(session);

        emailMessage.setFrom(new InternetAddress(Reference.getEnvironmentProperty("emailUsername")));
        emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        emailMessage.setSubject("test");
        emailMessage.setText(message);

        Transport.send(emailMessage);

        return null;
    }

    /**
     * Gets the signed in user's unread notifications
     *
     * @param user
     * @param session
     * @return
     */
    @UnitOfWork
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/notifications")
    public ResponseEntity getUnreadNotifications(@AuthedUser User user, SessionImpl session) {
        List results = session.createCriteria(Notification.class)
                .add(Restrictions.eq("user.id", user.getId()))
                .add(Restrictions.eq("hasRead", false))
                .list();

        return new ResponseEntity(results, HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.PENDING)
    @Transactional
    @RequestMapping(value = "/notifications/read", method = RequestMethod.POST)
    public ResponseEntity markNotificationsAsRead(@JSONParam("notifications") Notification[] notificationList, @AuthedUser User user, SessionImpl session) {
        List notificationIDs = Arrays.asList(notificationList).stream()
                .map(a -> a.getId())
                .collect(Collectors.toList());

        if (notificationList.length > 0) {
            List<Notification> notifications = session.createCriteria(Notification.class)
                    .add(Restrictions.eq("user.id", user.getId()))
                    .add(Restrictions.in("id", notificationIDs))
                    .list();

            notifications.stream()
                    .forEach(a -> a.setHasRead(true));
        }

        return new ResponseEntity(notificationList, HttpStatus.OK);
    }


    /**
     * Gets the signed in user's badges
     *
     * @param request
     * @param response
     * @param user
     * @return
     */
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/badges")
    public ResponseEntity getBadges(HttpServletRequest request, HttpServletResponse response, @AuthedUser User user) {
        return new ResponseEntity(user.getBadges(), HttpStatus.OK);
    }

    /**
     * Returns the team that the current user owns
     *
     * @param user (Resolved) The user requesting the data
     * @return Response Entity for the request
     */
    @UnitOfWork
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/ownedteam")
    public ResponseEntity getOwnedTeam(SessionImpl session, @AuthedUser User user) {
        UserTeam userTeam = user.getOwnedTeam();

        if (userTeam != null) {
            userTeam = (UserTeam) session.merge(userTeam);

            return new ResponseEntity(userTeam, HttpStatus.OK);
        }

        return new ResponseEntity("You don't own a team", HttpStatus.NOT_FOUND);
    }

    /**
     * Returns the team that the current user belongs to
     *
     * @param user (Resolved) The user requesting the data
     * @return Response Entity for the request
     */
    @UnitOfWork
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/myteam")
    public ResponseEntity getMyTeam(SessionImpl session, @AuthedUser User user) {
        UserTeam userTeam = user.getTeam();

        if (userTeam != null && userTeam.getOwner() != null) {
            userTeam = (UserTeam) session.merge(userTeam);

            return new ResponseEntity(userTeam, HttpStatus.OK);
        }

        return new ResponseEntity("You don't belong to a team", HttpStatus.NOT_FOUND);
    }

    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/teaminvites")
    public ResponseEntity getMyTeamInvites(@AuthedUser User user) {
        List<UserTeamInvite> invites = userTeamService.teamsInvitedTo(user);

        return new ResponseEntity(invites, HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/permission")
    @ResponseBody
    public String hello() {
        return "Hello";
    }
}
