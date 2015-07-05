package com.bezman.oauth;

import com.bezman.model.User;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by Terence on 3/27/2015.
 */
public interface IProvider
{

    public static User userForCode(String code) throws UnirestException
    {return null;};

}
