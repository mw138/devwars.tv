package com.bezman.init;

import com.bezman.Reference.Reference;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TwitterInit implements IInit {

    @Override
    public void init() {
        Twitter twitter = TwitterFactory.getSingleton();

        twitter.setOAuthConsumer(Reference.getEnvironmentProperty("twitterConsumerKey"), Reference.getEnvironmentProperty("twitterConsumerSecret"));
    }
}
