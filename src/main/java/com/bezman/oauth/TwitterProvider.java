package com.bezman.oauth;

import com.bezman.Reference.util.Util;
import com.bezman.model.User;

public class TwitterProvider {

    public static com.bezman.model.User userForTwitterUser(twitter4j.User twitterUser) {
        User user = new User();
        user.setEmail(null);
        user.setUsername(twitterUser.getScreenName() + Util.randomNumbers(4));
        user.setRole(User.Role.USER);
        user.setProvider("TWITTER");
        user.setProviderID(String.valueOf(twitterUser.getId()));

        return user;
    }

}
