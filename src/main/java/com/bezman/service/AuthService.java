package com.bezman.service;

import com.bezman.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private HttpService httpService;

    @Autowired
    public AuthService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void loginUser(User user) {
        this.httpService.setCookie("token", user.newSession());
    }

}
