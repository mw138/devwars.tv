package com.bezman.service;

import com.bezman.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

@Service
public class AuthService {

    private HttpService httpService;

    @Autowired
    public AuthService(HttpService httpService) {
        this.httpService = httpService;
    }

    public Cookie loginUser(User user) {
        Cookie cookie = new Cookie("token", user.newSession());

        this.httpService.setCookie(cookie.getName(), cookie.getValue());

        return cookie;
    }

}
