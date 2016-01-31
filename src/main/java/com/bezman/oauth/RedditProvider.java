package com.bezman.oauth;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.model.User;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class RedditProvider implements IProvider {

    public static User userForCodeWithKeys(String code, String access, String secret, String redirect) throws UnirestException {
        String accessTokenJSON = Unirest.post("https://www.reddit.com/api/v1/access_token")
            .queryString("grant_type", "authorization_code")
            .queryString("code", code)
            .queryString("redirect_uri", redirect)
            .basicAuth(access, secret)
            .asString()
            .getBody();

        JSONObject accessTokenJSONObject = (JSONObject) JSONValue.parse(accessTokenJSON);
        System.out.println("FIRST RESPONSE");
        System.out.println(accessTokenJSONObject.toJSONString());

        String accessToken = (String) accessTokenJSONObject.get("access_token");

        String meResponse = Unirest.get("https://oauth.reddit.com/api/v1/me")
            .queryString("access_token", accessToken)
            .header("Authorization", "bearer " + accessToken)
            .header("User-Agent", "DevWars2/0.1 by DevWars")
            .asString()
            .getBody();

        System.out.println(meResponse);
        JSONObject meJSONObject = (JSONObject) JSONValue.parse(meResponse);

        if (meJSONObject != null) {
            User user = new User();
            user.setEmail(null);
            user.setUsername(meJSONObject.get("name") + Util.randomNumbers(4));
            user.setRole(User.Role.USER);
            user.setProvider("REDDIT");
            user.setProviderID((String) meJSONObject.get("id"));

            return user;
        }

        return null;
    }

    public static User userForCode(String code) throws UnirestException {
        return userForCodeWithKeys(code, Reference.getEnvironmentProperty("redditAppID"), Reference.getEnvironmentProperty("redditSecret"), Reference.rootURL + "/v1/oauth/reddit_callback");
    }

    public static User userForCode2(String code) throws UnirestException {
        return userForCodeWithKeys(code, Reference.getEnvironmentProperty("redditAppID2"), Reference.getEnvironmentProperty("redditSecret2"), Reference.rootURL + "/v1/connect/reddit_callback");
    }

}
