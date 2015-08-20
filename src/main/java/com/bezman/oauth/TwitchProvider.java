package com.bezman.oauth;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.model.User;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Terence on 3/26/2015.
 */
public class TwitchProvider implements IProvider
{

    public static User userForCodeWithKeys(String code, String access, String secret, String redirect)
    {
        try
        {
            String body = Unirest.post("https://api.twitch.tv/kraken/oauth2/token")
                    .queryString("client_id", access)
                    .queryString("client_secret", secret)
                    .queryString("grant_type", "authorization_code")
                    .queryString("redirect_uri", redirect)
                    .queryString("code", code)
                    .asString()
                    .getBody();

            JSONObject accessTokenJSONObject = (JSONObject) JSONValue.parse(body);
            String accessToken = (String) accessTokenJSONObject.get("access_token");

            if (accessToken != null)
            {
                String api = Unirest.get("https://api.twitch.tv/kraken/user")
                        .queryString("oauth_token", accessToken)
                        .asString()
                        .getBody();

                System.out.println(api);
                JSONObject userJSONObject = (JSONObject) JSONValue.parse(api);

                User user = new User();
                user.setEmail((String) userJSONObject.get("email"));
                user.setUsername((String) userJSONObject.get("display_name") + Util.randomNumbers(4));
                user.setRole(User.Role.USER.toString());
                user.setProvider("TWITCH");
                user.setProviderID(String.valueOf(userJSONObject.get("_id")));

                return user;
            }
        } catch (UnirestException e)
        {
            e.printStackTrace();
        }

        return  null;
    }

    public static User userForCode(String code)
    {
        return userForCodeWithKeys(code, Reference.getEnvironmentProperty("twitchClientID"), Reference.getEnvironmentProperty("twitchSecret"), Reference.rootURL + "/v1/oauth/twitch_callback");
    }

    public static User userForCode2(String code)
    {
        return userForCodeWithKeys(code, Reference.getEnvironmentProperty("twitchClientID2"), Reference.getEnvironmentProperty("twitchSecret2"), Reference.rootURL + "/v1/connect/twitch_callback");
    }

}
