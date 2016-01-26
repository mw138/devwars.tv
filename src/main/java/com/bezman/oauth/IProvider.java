package com.bezman.oauth;

import com.bezman.model.User;
import com.mashape.unirest.http.exceptions.UnirestException;

public interface IProvider {

    static User userForCode(String code) throws UnirestException {
        return null;
    }

}
