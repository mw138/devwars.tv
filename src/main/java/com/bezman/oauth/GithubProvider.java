package com.bezman.oauth;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.model.User;
import com.mashape.unirest.http.Unirest;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GithubProvider implements IProvider {

    public static User userForCodeWithRedirect(String code, String redirect, String client, String secret) {
        try {
            String accessTokenResponse = Unirest.post("https://github.com/login/oauth/access_token")
                .queryString("code", code)
                .queryString("client_id", client)
                .queryString("client_secret", secret)
                .queryString("redirect_uri", redirect)
                .header("Accept", "application/json")
                .asString()
                .getBody();

            JSONObject accessTokenJSON = (JSONObject) JSONValue.parse(accessTokenResponse);
            String accessToken = (String) accessTokenJSON.get("access_token");

            String userInfoResponse = Unirest.get("https://api.github.com/user")
                .queryString("access_token", accessToken)
                .asString()
                .getBody();

            JSONObject userInfoJSON = (JSONObject) JSONValue.parse(userInfoResponse);

            User user = new User();
            user.setUsername(userInfoJSON.get("login") + Util.randomNumbers(4));
            user.setRole(User.Role.USER);
            user.setEmail((String) userInfoJSON.get("email"));
            user.setProviderID(String.valueOf(userInfoJSON.get("id")));
            user.setProvider("GITHUB");

            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static User userForCode(String code) {
        return userForCodeWithRedirect(code, Reference.rootURL + "/v1/oauth/github_callback", Reference.getEnvironmentProperty("githubClientID"), Reference.getEnvironmentProperty("githubSecret"));
    }

    public static User userForCode2(String code) {
        return userForCodeWithRedirect(code, Reference.rootURL + "/v1/connect/github_callback", Reference.getEnvironmentProperty("githubClientID2"), Reference.getEnvironmentProperty("githubSecret2"));
    }

}
