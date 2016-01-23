package com.bezman.controller.user;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.AuthedUser;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
import com.bezman.model.User;
import com.bezman.oauth.*;
import com.bezman.service.ConnectedAccountService;
import com.bezman.service.HttpService;
import com.bezman.service.UserService;
import com.mashape.unirest.http.exceptions.UnirestException;
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

/**
 * Created by root on 4/25/15.
 */
@Controller
@RequestMapping("/v1/connect")
public class UserConnectionController {

    private UserService userService;
    private ConnectedAccountService connectedAccountService;
    private HttpService httpService;

    @Autowired
    public UserConnectionController(UserService userService, HttpService httpService, ConnectedAccountService connectedAccountService) {
        this.userService = userService;
        this.httpService = httpService;
        this.connectedAccountService = connectedAccountService;
    }

    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    @RequestMapping("/{provider}/disconnect")
    public ResponseEntity disconnectAccount(@AuthedUser User user,
                                            @PathVariable("provider") String provider) {

        if (user.getPassword() == null) {
            return new ResponseEntity("You need to set a password before you can disconnect this account.", HttpStatus.CONFLICT);
        }

        this.connectedAccountService.disconnectProviderFromUser(provider, user);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping("/google")
    public void googleAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("https://accounts.google.com/o/oauth2/auth?scope=" +
                "profile email openid&" +
                "state=generate_a_unique_state_value&" +
                "redirect_uri=" + Reference.rootURL + "/v1/connect/google_callback&" +
                "response_type=code&" +
                "client_id=" + Reference.getEnvironmentProperty("googleClientID") + "&" +
                "access_type=offline");
    }

    @RequestMapping("/google_callback")
    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    public void googleCallback(@AuthedUser User currentUser, @RequestParam("code") String code) throws UnirestException, IOException {
        User connectedUser = GoogleProvider.userForCode2(code);
        String cutUsername = connectedUser.getUsername().substring(0, connectedUser.getUsername().length() - 4);

        this.connectedAccountService.connectProviderToUser(currentUser, cutUsername, "GOOGLE", connectedUser.getProviderID());

        this.httpService.sendRedirect("/settings/connectoins");
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
    public ResponseEntity twitterCallback(HttpServletRequest request, @AuthedUser User currentUser, @RequestParam("oauth_token") String token, @RequestParam("oauth_verifier") String verifier) throws TwitterException, IOException {
        Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
        twitter.getOAuthAccessToken((RequestToken) request.getSession().getAttribute("requestToken"), verifier);

        twitter4j.User twitterUser = twitter.showUser(twitter.getId());
        twitter.setOAuthAccessToken(null);

        com.bezman.model.User connectedUser = TwitterProvider.userForTwitterUser(twitterUser);
        String cutUsername = connectedUser.getUsername().substring(0, connectedUser.getUsername().length() - 4);

        this.connectedAccountService.connectProviderToUser(currentUser, cutUsername, connectedUser.getProvider(), connectedUser.getProviderID());

        this.httpService.sendRedirect("/settings/connections/");

        return null;
    }

    @RequestMapping("/twitch")
    public ResponseEntity twitchAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.sendRedirect("https://api.twitch.tv/kraken/oauth2/authorize" +
                    "?response_type=code" +
                    "&client_id=" + Reference.getEnvironmentProperty("twitchClientID2") +
                    "&redirect_uri=" + Reference.rootURL + "/v1/connect/twitch_callback" +
                    "&scope=user_read");

        return null;
    }

    @Transactional
    @RequestMapping("/twitch_callback")
    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    public ResponseEntity twitchCallback(@AuthedUser User currentUser, HttpServletResponse response, @RequestParam("code") String code) throws IOException {
        User connectedUser = TwitchProvider.userForCode2(code);
        String cutUsername = connectedUser.getUsername().substring(0, connectedUser.getUsername().length() - 4);

        this.connectedAccountService.connectProviderToUser(currentUser, cutUsername, connectedUser.getProvider(), connectedUser.getProviderID());

        response.sendRedirect("/settings/connections");

        return null;
    }

    @RequestMapping("/facebook")
    public static ResponseEntity facebookAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("https://www.facebook.com/dialog/oauth?" +
                "client_id=" + Reference.getEnvironmentProperty("facebookAppID") +
                "&redirect_uri=" + Reference.rootURL + "/v1/connect/facebook_callback" +
                "&response_type=code" +
                "&scope=email");

        return null;
    }

    @RequestMapping("/facebook_callback")
    @PreAuthorization(minRole = com.bezman.model.User.Role.PENDING)
    public ResponseEntity facebookCallback(@AuthedUser User currentUser, @RequestParam("code") String code) throws UnirestException, IOException {
        com.bezman.model.User connectedUser = FacebookProvider.userForCode2(code);
        String cutUsername = connectedUser.getUsername().substring(0, connectedUser.getUsername().length() - 4);

        this.connectedAccountService.connectProviderToUser(currentUser, cutUsername, connectedUser.getProvider(), connectedUser.getProviderID());

        this.httpService.sendRedirect("/settings/connections/");

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
    public ResponseEntity githubCallback(@AuthedUser User currentUser, @RequestParam("code") String code) throws IOException {
        com.bezman.model.User connectedUser = GithubProvider.userForCode2(code);
        String cutUsername = connectedUser.getUsername().substring(0, connectedUser.getUsername().length() - 4);

        this.connectedAccountService.connectProviderToUser(currentUser, cutUsername, connectedUser.getProvider(), connectedUser.getProviderID());

        this.httpService.sendRedirect("/settings/connections");

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
