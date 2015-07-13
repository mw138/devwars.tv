package com.bezman.Reference;

import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.model.SecretKey;
import com.bezman.service.Security;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.*;

/**
 * Created by Terence on 12/22/2014.
 */
public class Reference
{
    public static String rootURL = "http://devwars.tv";
    //    public static String rootURL = "http://bezcode.com:9090";
//    public static String rootURL = "http://localhost:8080";
    public static String[] allowedHosts = new String[]{"localhost:8080", "localhost:81", "devwars.tv", "bezcode.com:9090"};

    public static Firebase firebase;

    public static String PROFILE_PICTURE_PATH = File.separator + "usr" + File.separator + "share" + File.separator + "devwarspics" + File.separator;

    public static Gson gson;

    public static Connection connection;

    public static Cookie getCookieFromArray(Cookie[] cookies, String key)
    {
        if (cookies != null && key != null)
        {
            for (Cookie cookie : cookies)
            {
                if (cookie.getName().equals(key))
                    return cookie;
            }
        }

        return null;
    }

    public static void resetConnectionToDB()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static PreparedStatement prepareStatement(String query)
    {
        try
        {

            if (connection == null || connection.isClosed())
            {
                resetConnectionToDB();
            }

            PreparedStatement statement = connection.prepareStatement(query);

            if (statement == null)
            {
                resetConnectionToDB();

                return prepareStatement(query);
            } else return statement;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static PreparedStatement prepareStatement(String query, Object... values)
    {
        PreparedStatement statement = prepareStatement(query);

        int valIndex = 1;
        for (int i = 0; i < query.length(); i++)
        {
            if (query.charAt(i) == '?')
            {
                try
                {
                    statement.setObject(valIndex, values[valIndex - 1]);
                    valIndex++;
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return statement;
    }

    public static ResultSet prepareStatementAndQuery(String query, Object... values)
    {
        try
        {
            return prepareStatement(query, values).executeQuery();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static int prepareStatementAndUpdate(String query, Object... values)
    {
        try
        {
            return prepareStatement(query, values).executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean recaptchaValid(String response, String ip)
    {
        System.out.println("Testing recaptcha");

        try
        {
            HttpResponse httpResponse = Unirest.post("https://www.google.com/recaptcha/api/siteverify")
                    .field("secret", Security.recaptchaPrivateKey)
                    .field("response", response)
                    .field("remoteip", ip)
                    .asString();

            JSONObject responseObject = (JSONObject) JSONValue.parse(httpResponse.getBody().toString());

            return responseObject.get("success") != null;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean requestHasSecretKey(HttpServletRequest request)
    {
        String key = request.getParameter("key");

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from SecretKey where uid = :uid");
        query.setString("uid", key);

        SecretKey secretKey = (SecretKey) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return secretKey != null;
    }

}
