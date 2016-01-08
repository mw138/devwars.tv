package com.bezman.oauth;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.model.User;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Terence on 3/27/2015.
 */
public class FacebookProvider implements IProvider {
    public static User userForCodeWithRedirect(String code, String redirect) throws UnirestException {
        String accessTokenJSON = Unirest.get("https://graph.facebook.com/v2.3/oauth/access_token")
                .queryString("code", code)
                .queryString("client_id", Reference.getEnvironmentProperty("facebookAppID"))
                .queryString("client_secret", Reference.getEnvironmentProperty("facebookSecret"))
                .queryString("redirect_uri", redirect)
                .asString()
                .getBody();

        System.out.println(accessTokenJSON);
        JSONObject accessTokenJSONObject = (JSONObject) JSONValue.parse(accessTokenJSON);

        if (accessTokenJSONObject != null) {
            String accessToken = (String) accessTokenJSONObject.get("access_token");

            if (accessToken != null) {
                String meResponse = Unirest.get("https://graph.facebook.com/v2.3/me")
                        .queryString("fields", "name, email, id")
                        .queryString("access_token", accessToken)
                        .asString()
                        .getBody();

                System.out.println(meResponse);

                JSONObject meJSONObject = (JSONObject) JSONValue.parse(meResponse);

                if (meJSONObject != null) {
                    User user = new User();

                    user.setUsername((String) meJSONObject.get("name") + Util.randomNumbers(4));
                    user.setEmail((String) meJSONObject.get("email"));
                    user.setRole(User.Role.USER);
                    user.setProvider("FACEBOOK");
                    user.setProviderID((String) meJSONObject.get("id"));

                    return user;
                }
            }
        }

        return null;
    }

    public static User userForCode(String code)  {
        try {
            return userForCodeWithRedirect(code, Reference.rootURL + "/v1/oauth/facebook_callback");
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static User userForCode2(String code) {
        try {
            return userForCodeWithRedirect(code, Reference.rootURL + "/v1/connect/facebook_callback");
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }
}
