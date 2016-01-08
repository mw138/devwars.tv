package com.bezman.controller.user;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.init.DatabaseManager;
import com.bezman.model.Ranking;
import com.bezman.model.TwitchPointStorage;
import com.bezman.oauth.*;
import com.bezman.service.AuthService;
import com.bezman.service.HttpService;
import com.bezman.service.UserService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by Terence on 3/23/2015.
 */
@Controller
@RequestMapping("/v1/oauth")
public class OAuthController {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    HttpService httpService;

    @RequestMapping("/google")
    public ResponseEntity googleAuth(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://accounts.google.com/o/oauth2/auth?scope=" +
                    "profile email openid&" +
                    "state=generate_a_unique_state_value&" +
                    "redirect_uri=" + Reference.rootURL + "/v1/oauth/google_callback&" +
                    "response_type=code&" +
                    "client_id=" + Reference.getEnvironmentProperty("googleClientID") + "&" +
                    "access_type=offline");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/google_callback")
    public ResponseEntity googleCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) {
        com.bezman.model.User user = GoogleProvider.userForCode(code);
        com.bezman.model.User queryUser = userService.userForProviderAndProviderID(user.getProvider(), user.getProviderID());

        if (queryUser == null) {
            userService.createConnectedAccountFromPrimaryAccount(user);
        } else {
            user.setId(queryUser.getId());
        }

        this.authService.loginUser(user);
        this.httpService.sendRedirect("/");

        return null;
    }

    @RequestMapping("/twitter")
    public ResponseEntity twitterAuth(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "email", required = false) String email) {
        Twitter twitter = TwitterFactory.getSingleton();

        try {
            RequestToken token = twitter.getOAuthRequestToken(Reference.rootURL + "/v1/oauth/twitter_callback");
            request.getSession().setAttribute("requestToken", token);
            request.getSession().setAttribute("twitter", twitter);
            request.getSession().setAttribute("email", email);
            response.sendRedirect("https://api.twitter.com/oauth/authenticate?oauth_token=" + token.getToken());

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/twitter_callback")
    public ResponseEntity twitterCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("oauth_token") String token, @RequestParam("oauth_verifier") String verifier) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();

        twitter.getOAuthAccessToken((RequestToken) request.getSession().getAttribute("requestToken"), verifier);

        User twitterUser = twitter.showUser(twitter.getId());
        com.bezman.model.User user = TwitterProvider.userForTwitterUser(twitterUser);
        twitter.setOAuthAccessToken(null);
        com.bezman.model.User queryUser = userService.userForProviderAndProviderID(user.getProvider(), user.getProviderID());

        if (queryUser == null) {
            userService.createConnectedAccountFromPrimaryAccount(user);
        } else {
            user.setId(queryUser.getId());
        }

        this.authService.loginUser(user);
        this.httpService.sendRedirect("/");

        return null;
    }

    @RequestMapping("/twitch")
    public ResponseEntity twitchAuth(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://api.twitch.tv/kraken/oauth2/authorize" +
                    "?response_type=code" +
                    "&client_id=" + Reference.getEnvironmentProperty("twitchClientID") +
                    "&redirect_uri=" + Reference.rootURL + "/v1/oauth/twitch_callback" +
                    "&scope=user_read");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/twitch_callback")
    public ResponseEntity twitchCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) {
        try {
            com.bezman.model.User user = TwitchProvider.userForCode(code);

            Session session = DatabaseManager.getSession();

            com.bezman.model.User queryUser = userService.userForProviderAndProviderID(user.getProvider(), user.getProviderID());

            if (queryUser == null) {
                Query pointsQuery = session.createQuery("from TwitchPointStorage s where lower(s.username) = :username");
                pointsQuery.setString("username", (user.getUsername().substring(0, user.getUsername().length() - 4)).toLowerCase());

                TwitchPointStorage twitchPointStorage = (TwitchPointStorage) DatabaseUtil.getFirstFromQuery(pointsQuery);

                session.close();

                DatabaseUtil.saveObjects(true, user);

                if (twitchPointStorage != null) {
                    Ranking ranking = new Ranking();
                    ranking.setXp((double) twitchPointStorage.getXp());
                    ranking.setPoints((double) twitchPointStorage.getPoints());
                    ranking.setId(user.getId());

                    DatabaseUtil.saveOrUpdateObjects(false, ranking);
                    DatabaseUtil.deleteObjects(twitchPointStorage);
                }
            } else {
                user.setId(queryUser.getId());
            }

            Cookie cookie = new Cookie("token", user.newSession());
            cookie.setPath("/");

            response.addCookie(cookie);
            response.sendRedirect(Reference.rootURL + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/facebook")
    public static ResponseEntity facebookAuth(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://www.facebook.com/dialog/oauth?" +
                    "client_id=" + Reference.getEnvironmentProperty("facebookAppID") +
                    "&redirect_uri=" + Reference.rootURL + "/v1/oauth/facebook_callback" +
                    "&response_type=code" +
                    "&scope=email");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/facebook_callback")
    public ResponseEntity facebookCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) {
        com.bezman.model.User user = FacebookProvider.userForCode(code);
        com.bezman.model.User queryUser = userService.userForProviderAndProviderID(user.getProvider(), user.getProviderID());

        if (queryUser == null) {
            userService.createConnectedAccountFromPrimaryAccount(user);
        } else {
            user.setId(queryUser.getId());
        }

        this.authService.loginUser(user);
        this.httpService.sendRedirect("/");

        return null;
    }

    @RequestMapping("/github")
    public ResponseEntity githubAuth(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://github.com/login/oauth/authorize?" +
                    "client_id=" + Reference.getEnvironmentProperty("githubClientID") +
                    "&redirect_uri=" + Reference.rootURL + "/v1/oauth/github_callback" +
                    "&scope=user,user:email" +
                    "&state=" + Util.randomText(32));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/github_callback")
    public ResponseEntity githubCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) {
        com.bezman.model.User user = GithubProvider.userForCode(code);
        com.bezman.model.User queryUser = userService.userForProviderAndProviderID(user.getProvider(), user.getProviderID());

        if (queryUser == null) {
            userService.createConnectedAccountFromPrimaryAccount(user);
        } else {
            user.setId(queryUser.getId());

        }
        this.authService.loginUser(user);
        this.httpService.sendRedirect("/");

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

}
