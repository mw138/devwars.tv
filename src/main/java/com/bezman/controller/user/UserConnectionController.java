package com.bezman.controller.user;

import com.bezman.Reference.DevBits;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.AuthedUser;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
import com.bezman.init.DatabaseManager;
import com.bezman.model.*;
import com.bezman.oauth.*;
import com.bezman.service.UserService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;

/**
 * Created by root on 4/25/15.
 */
@Controller
@RequestMapping("/v1/connect")
public class UserConnectionController {
    @Autowired
    UserService userService;

    @PreAuthorization(minRole = com.bezman.model.User.Role.USER)
    @RequestMapping("/testtwitch")
    @Transactional
    public ResponseEntity testConnectTwitchUser(SessionImpl session, @RequestParam("username") String username, @AuthedUser User currentUser) {
        TwitchPointStorage twitchPointStorage = (TwitchPointStorage) session.createQuery("from TwitchPointStorage s where s.username = :username")
                .setString("username", username)
                .setMaxResults(1)
                .uniqueResult();

        if (twitchPointStorage != null) {
            currentUser.getRanking().addXP(twitchPointStorage.getXp());
            currentUser.getRanking().addPoints(twitchPointStorage.getPoints());

            session.delete(twitchPointStorage);
            session.merge(currentUser);
        }
        return new ResponseEntity(twitchPointStorage, HttpStatus.OK);
    }

    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    @RequestMapping("/{provider}/disconnect")
    public ResponseEntity disconnectAccount(@AuthedUser User user,
                                            @PathVariable("provider") String provider) {

        if (user.getPassword() == null) {
            return new ResponseEntity("You must set a password first!", HttpStatus.CONFLICT);
        }

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("update ConnectedAccount c set c.disconnected = true, c.username = '' where c.user.id = :id AND c.provider = :provider");
        query.setInteger("id", user.getId());
        query.setString("provider", provider);

        query.executeUpdate();

        session.close();

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping("/reddit")
    public ResponseEntity redditAuth(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity("We removed Reddit", HttpStatus.OK);

//        try
//        {
//            response.sendRedirect("https://www.reddit.com/api/v1/authorize?client_id=" + Security.redditAppID2 + "&response_type=code&" +
//                    "state=" + Util.randomText(32) + "&redirect_uri=" + Reference.rootURL + "/v1/connect/reddit_callback&duration=permanent&scope=identity");
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return null;
    }

    @RequestMapping("/reddit_callback")
    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    public ResponseEntity redditCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) throws UnirestException, IOException {
        com.bezman.model.User user = RedditProvider.userForCode2(code);
        com.bezman.model.User currentUser = (com.bezman.model.User) request.getAttribute("user");

        boolean connectedAccountExists = BaseModel.rowExists(ConnectedAccount.class, "provider = ? and user = ?", "REDDIT", currentUser);

        if (user != null && !connectedAccountExists) {
            Activity activity = new Activity(currentUser, currentUser, "Connected your Reddit account", DevBits.ACCOUNT_CONNECTION, 0);

            currentUser.getRanking().addPoints(DevBits.ACCOUNT_CONNECTION);
            DatabaseUtil.saveOrUpdateObjects(false, currentUser.getRanking());
            DatabaseUtil.saveObjects(false, connectedAccountForUser(user, currentUser, "REDDIT"), activity);
        } else {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("update ConnectedAccount c set disconnected = false, username = :username where c.provider = :provider AND c.user = :user");
            query.setString("provider", "REDDIT");
            query.setParameter("user", currentUser);
            query.setString("username", user.getUsername().substring(0, user.getUsername().length() - 4));

            query.executeUpdate();

            session.close();

            DatabaseUtil.saveObjects(false, new Activity(currentUser, currentUser, "Connected your Reddit account", 0, 0));
        }

        response.sendRedirect("/settings/connections");
        return null;
    }

    @RequestMapping("/google")
    public ResponseEntity googleAuth(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://accounts.google.com/o/oauth2/auth?scope=" +
                    "profile email openid&" +
                    "state=generate_a_unique_state_value&" +
                    "redirect_uri=" + Reference.rootURL + "/v1/connect/google_callback&" +
                    "response_type=code&" +
                    "client_id=" + Reference.getEnvironmentProperty("googleClientID") + "&" +
                    "access_type=offline");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/google_callback")
    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    public ResponseEntity googleCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) throws UnirestException, IOException {
        com.bezman.model.User user = GoogleProvider.userForCode2(code);
        com.bezman.model.User currentUser = (com.bezman.model.User) request.getAttribute("user");

        boolean connectedAccountExists = BaseModel.rowExists(ConnectedAccount.class, "provider = ? and user = ?", "GOOGLE", currentUser);

        if (user != null && !connectedAccountExists) {
            Activity activity = new Activity(currentUser, currentUser, "Connected your Google account", DevBits.ACCOUNT_CONNECTION, 0);

            currentUser.getRanking().addPoints(DevBits.ACCOUNT_CONNECTION);
            DatabaseUtil.saveOrUpdateObjects(false, currentUser.getRanking());
            DatabaseUtil.saveObjects(false, connectedAccountForUser(user, currentUser, "GOOGLE"), activity);
        } else {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("update ConnectedAccount c set disconnected = false, username = :username where c.provider = :provider AND c.user = :user");
            query.setString("provider", "GOOGLE");
            query.setParameter("user", currentUser);
            query.setString("username", user.getUsername().substring(0, user.getUsername().length() - 4));

            query.executeUpdate();

            session.close();

            DatabaseUtil.saveObjects(false, new Activity(currentUser, currentUser, "Connected your Google account", 0, 0));
        }

        response.sendRedirect("/settings/connections");
        return null;
    }

    @RequestMapping("/twitter")
    public ResponseEntity twitterAuth(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "email", required = false) String email) {
        boolean allowed = isRequestAllowed(request);


        if (allowed) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(Reference.getEnvironmentProperty("twitterConsumerKey"));
            builder.setOAuthConsumerSecret(Reference.getEnvironmentProperty("twitterConsumerSecret"));
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            Twitter twitter = factory.getInstance();

            try {
                RequestToken token = twitter.getOAuthRequestToken(Reference.rootURL + "/v1/connect/twitter_callback");
                request.getSession().setAttribute("requestToken", token);
                request.getSession().setAttribute("twitter", twitter);
                request.getSession().setAttribute("email", email);
                response.sendRedirect(token.getAuthenticationURL());

                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else System.out.println("Not allowed");

        return new ResponseEntity("Not a valid host", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping("/twitter_callback")
    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    public ResponseEntity twitterCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("oauth_token") String token, @RequestParam("oauth_verifier") String verifier) throws TwitterException, IOException {
        Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
        twitter.getOAuthAccessToken((RequestToken) request.getSession().getAttribute("requestToken"), verifier);

        twitter4j.User twitterUser = twitter.showUser(twitter.getId());
        com.bezman.model.User user = TwitterProvider.userForTwitterUser(twitterUser);

        com.bezman.model.User currentUser = (com.bezman.model.User) request.getAttribute("user");

        boolean connectedAccountExists = BaseModel.rowExists(ConnectedAccount.class, "provider = ? and user = ?", "TWITTER", currentUser);

        twitter.setOAuthAccessToken(null);

        if (user != null && !connectedAccountExists) {
            Activity activity = new Activity(currentUser, currentUser, "Connected your Twitter account", DevBits.ACCOUNT_CONNECTION, 0);

            currentUser.getRanking().addPoints(DevBits.ACCOUNT_CONNECTION);
            DatabaseUtil.saveOrUpdateObjects(false, currentUser.getRanking());
            DatabaseUtil.saveObjects(false, connectedAccountForUser(user, currentUser, "TWITTER"), activity);
        } else {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("update ConnectedAccount c set disconnected = false, username = :username where c.provider = :provider AND c.user = :user");
            query.setString("provider", "TWITTER");
            query.setParameter("user", currentUser);
            query.setString("username", user.getUsername().substring(0, user.getUsername().length() - 4));

            query.executeUpdate();

            session.close();

            DatabaseUtil.saveObjects(false, new Activity(currentUser, currentUser, "Connected your Twitter account", 0, 0));
        }

        response.sendRedirect("/settings/connections");
        return null;
    }

    @RequestMapping("/twitch")
    public ResponseEntity twitchAuth(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://api.twitch.tv/kraken/oauth2/authorize" +
                    "?response_type=code" +
                    "&client_id=" + Reference.getEnvironmentProperty("twitchClientID2") +
                    "&redirect_uri=" + Reference.rootURL + "/v1/connect/twitch_callback" +
                    "&scope=user_read");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Transactional
    @RequestMapping("/twitch_callback")
    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    public ResponseEntity twitchCallback(SessionImpl session, @AuthedUser User user, HttpServletResponse response, @RequestParam("code") String code) throws IOException {
        User twitchUser = TwitchProvider.userForCode2(code);
        User veteranUser = userService.userForUsername(user.getUsername().substring(0, user.getUsername().length() - 4));

        boolean alreadyConnected = user.getConnectedAccounts().stream()
                .anyMatch(account -> !account.getDisconnected() && "TWITCH".equals(account.getProvider()));

        if (alreadyConnected) {
            return new ResponseEntity("Already Connected", HttpStatus.CONFLICT);
        }

        Optional<ConnectedAccount> connectedAccount = user.getConnectedAccounts().stream()
                .filter(account ->
                        "TWITCH".equals(account.getProvider()) && account.getDisconnected())
                .findFirst();

        if (connectedAccount.isPresent()) {
            connectedAccount.get().setUsername(twitchUser.getUsername().substring(0, twitchUser.getUsername().length() - 4));
            connectedAccount.get().setDisconnected(false);

            session.merge(connectedAccount.get());
        } else {
            ConnectedAccount newConnection = new ConnectedAccount(user, "TWITCH", twitchUser.getUsername().substring(0, twitchUser.getUsername().length() - 4));
            user.getConnectedAccounts().add(newConnection);

            session.save(newConnection);
        }

        userService.addTwitchPointsToUser(user);

        session.merge(user);

        if (veteranUser != null && veteranUser.getVeteran()) {
            response.sendRedirect("/settings/connections?veteran=true");
            return null;
        }

        response.sendRedirect("/settings/connections");
        return null;
    }

    @RequestMapping("/facebook")
    public static ResponseEntity facebookAuth(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://www.facebook.com/dialog/oauth?" +
                    "client_id=" + Reference.getEnvironmentProperty("facebookAppID") +
                    "&redirect_uri=" + Reference.rootURL + "/v1/connect/facebook_callback" +
                    "&response_type=code" +
                    "&scope=email");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/facebook_callback")
    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    public ResponseEntity facebookCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) throws UnirestException, IOException {
        com.bezman.model.User currentUser = (com.bezman.model.User) request.getAttribute("user");

        com.bezman.model.User user = FacebookProvider.userForCode2(code);
        boolean connectedAccountExists = BaseModel.rowExists(ConnectedAccount.class, "provider = ? and user = ?", "FACEBOOK", currentUser);

        if (user != null && !connectedAccountExists) {
            Activity activity = new Activity(currentUser, currentUser, "Connected your Facebook account", DevBits.ACCOUNT_CONNECTION, 0);

            currentUser.getRanking().addPoints(DevBits.ACCOUNT_CONNECTION);
            DatabaseUtil.saveOrUpdateObjects(false, currentUser.getRanking());
            DatabaseUtil.saveObjects(false, connectedAccountForUser(user, currentUser, "FACEBOOK"), activity);
        } else {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("update ConnectedAccount c set disconnected = false, username = :username where c.provider = :provider AND c.user = :user");
            query.setString("provider", "FACEBOOK");
            query.setParameter("user", currentUser);
            query.setString("username", user.getUsername().substring(0, user.getUsername().length() - 4));

            query.executeUpdate();

            session.close();

            DatabaseUtil.saveObjects(false, new Activity(currentUser, currentUser, "Connected your Facebook account", 0, 0));
        }

        response.sendRedirect("/settings/connections");
        return null;
    }

    @RequestMapping("/github")
    public ResponseEntity githubAuth(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://github.com/login/oauth/authorize?" +
                    "client_id=" + Reference.getEnvironmentProperty("githubClientID2") +
                    "&redirect_uri=" + Reference.rootURL + "/v1/connect/github_callback" +
                    "&scope=user,user:email" +
                    "&state=" + Util.randomText(32));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    @RequestMapping("/github_callback")
    public ResponseEntity githubCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) throws IOException {
        com.bezman.model.User currentUser = (com.bezman.model.User) request.getAttribute("user");

        com.bezman.model.User user = GithubProvider.userForCode2(code);
        boolean connectedAccountExists = BaseModel.rowExists(ConnectedAccount.class, "provider = ? and user = ?", "GITHUB", currentUser);

        if (user != null && !connectedAccountExists) {
            Activity activity = new Activity(currentUser, currentUser, "Connected your GitHub account", DevBits.ACCOUNT_CONNECTION, 0);

            currentUser.getRanking().addPoints(DevBits.ACCOUNT_CONNECTION);
            DatabaseUtil.saveOrUpdateObjects(false, currentUser.getRanking());
            DatabaseUtil.saveObjects(false, connectedAccountForUser(user, currentUser, "GITHUB"), activity);
        } else {
            Session session = DatabaseManager.getSession();
            Query query = session.createQuery("update ConnectedAccount c set disconnected = false, username = :username where c.provider = :provider AND c.user = :user");
            query.setString("provider", "GITHUB");
            query.setParameter("user", currentUser);
            query.setString("username", user.getUsername().substring(0, user.getUsername().length() - 4));

            query.executeUpdate();

            session.close();

            DatabaseUtil.saveObjects(false, new Activity(currentUser, currentUser, "Connected your GitHub account", 0, 0));
        }

        response.sendRedirect("/settings/connections");
        return null;
    }

    public static boolean isRequestAllowed(HttpServletRequest request) {
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Enumeration values = request.getHeaders(name); // support multiple values
            if (values != null) {
                while (values.hasMoreElements()) {
                    String value = (String) values.nextElement();

                    if (name.equals("host")) {
                        for (String host : Reference.allowedHosts) {
                            if (host.equals(value)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private ConnectedAccount connectedAccountForUser(com.bezman.model.User user, com.bezman.model.User signedInUser, String provider) {
        ConnectedAccount connectedAccount = new ConnectedAccount();
        connectedAccount.setUser(signedInUser);
        connectedAccount.setProvider(provider);
        connectedAccount.setUsername(user.getUsername().substring(0, user.getUsername().length() - 4));

        return connectedAccount;
    }

}
