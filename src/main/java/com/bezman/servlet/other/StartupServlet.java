package com.bezman.servlet.other;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.AngularServiceBuilder;
import com.bezman.adapters.HibernateProxyAdapter;
import com.bezman.adapters.ObjectSerializationStrategy;
import com.bezman.adapters.TimestampAdapter;
import com.bezman.exclusion.ExclusionStrategy;
import com.bezman.model.Activity;
import com.bezman.service.Security;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Type;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

@Controller
@RequestMapping
public class StartupServlet
{

    @PostConstruct
    public void postConstruct()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Reference.connection = null;

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Timestamp.class, new TimestampAdapter());
            builder.registerTypeAdapter(HibernateProxy.class, new HibernateProxyAdapter());

            builder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()));

            builder.setExclusionStrategies(new ExclusionStrategy());

            Twitter twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(Security.twitterConsumerKey, Security.twitterConsumerSecret);

            Reference.gson = builder.create();

            DatabaseManager.init();

            Reference.firebase = new Firebase("https://devwars-tv.firebaseio.com");;
            Reference.firebase.authWithCustomToken(Security.firebaseToken , new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData)
                {

                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError)
                {

                }
            });

            AngularServiceBuilder.buildServicesFromPackage("com.bezman.controller", "C:\\Users\\teren\\IdeaProjects\\DevWars Maven\\target\\services\\", Reference.rootURL, "app");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void preDestroy()
    {
        try
        {
            Reference.connection.close();
            DatabaseManager.sessionFactory.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

}
