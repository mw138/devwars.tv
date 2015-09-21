package com.bezman.oauth;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.model.User;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Terence on 3/24/2015.
 */
public class GoogleProvider implements IProvider
{

    public static User userForCodeWithRedirect(String code, String redirect) throws UnirestException
    {
        com.mashape.unirest.http.HttpResponse<String> accessTokenResponse = Unirest.post("https://www.googleapis.com/oauth2/v3/token")
                .queryString("code", code)
                .queryString("client_id", Reference.getEnvironmentProperty("googleClientID"))
                .queryString("client_secret", Reference.getEnvironmentProperty("googleSecret"))
                .queryString("redirect_uri", redirect)
                .queryString("grant_type", "authorization_code")
                .asString();

        System.out.println(accessTokenResponse);
        JSONObject jsonObject = (JSONObject) JSONValue.parse(accessTokenResponse.getBody());

        String accessToken = (String) jsonObject.get("access_token");

        System.out.println("Access Token : " + accessToken);

        HttpResponse<String> meResponse = Unirest.get("https://www.googleapis.com/plus/v1/people/me")
                .queryString("access_token", accessToken)
                .asString();

        if (meResponse.getBody() != null)
        {
            JSONObject userObject = (JSONObject) JSONValue.parse(meResponse.getBody());

            System.out.println(userObject.toJSONString());

            if (userObject != null)
            {
                User user = new User();
                String email = (String) ((JSONObject) ((JSONArray) userObject.get("emails")).get(0)).get("value");
                String username = (String) userObject.get("displayName") + Util.randomNumbers(4);
                String providerID = (String) userObject.get("id");
                com.bezman.model.User.Role role = com.bezman.model.User.Role.USER;

                user.setEmail(email);
                user.setUsername(username);
                user.setRole(role);
                user.setProvider("GOOGLE");
                user.setProviderID(providerID);

                return user;
            }
        }

        return null;
    }

    public static User userForCode(String code) throws UnirestException
    {
        return userForCodeWithRedirect(code, Reference.rootURL + "/v1/oauth/google_callback");
    }

    public static User userForCode2(String code) throws UnirestException
    {
        return userForCodeWithRedirect(code, Reference.rootURL + "/v1/connect/google_callback");
    }

}
