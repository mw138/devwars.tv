package com.bezman.init;

import com.bezman.Reference.Reference;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.BasicAuthorization;
import twitter4j.auth.OAuth2Authorization;

/**
 * Created by teren on 8/19/2015.
 */
public class TwitterInit implements IInit {

    @Override
    public void init() {
        Twitter twitter = TwitterFactory.getSingleton();

        twitter.setOAuthConsumer(Reference.getEnvironmentProperty("twitterConsumerKey"), Reference.getEnvironmentProperty("twitterConsumerSecret"));

        Twitter twitterBot = new TwitterFactory().getInstance();

        twitterBot.setOAuthConsumer(Reference.getEnvironmentProperty("bot.consumerKey"), Reference.getEnvironmentProperty("bot.consumerSecret"));
        twitterBot.setOAuthAccessToken(new AccessToken(Reference.getEnvironmentProperty("bot.accessToken"), Reference.getEnvironmentProperty("bot.accessTokenSecret")));

        Reference.twitterFactory = twitterBot;
    }
}
