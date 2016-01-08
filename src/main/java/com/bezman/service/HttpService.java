package com.bezman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class HttpService {

    private ApplicationContext applicationContext;

    @Autowired
    public HttpService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");

        this.getCurrentResponse().addCookie(cookie);
    }

    public void sendRedirect(String path) {
        try {
            this.getCurrentResponse().sendRedirect(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public HttpServletResponse getCurrentResponse() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
    }

}
