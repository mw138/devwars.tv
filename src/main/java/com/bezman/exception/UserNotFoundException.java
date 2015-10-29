package com.bezman.exception;

public class UserNotFoundException extends Exception {
    public String query, param;

    public UserNotFoundException(String query, String param) {
        super("User could not be found for " + query + " : " + param);

        this.query = query;
        this.param = param;
    }
}
