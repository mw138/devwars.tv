package com.bezman.service;

import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;

@Service
public class HandlerMethodService {

    public boolean handlerMethodHasAnnotation(HandlerMethod handlerMethod, Class annotation) {
        if (handlerMethod.getMethod().getAnnotation(annotation) == null) {
            return true;
        }

        if (handlerMethod.getMethod().getDeclaringClass().getAnnotation(annotation) == null) {
            return true;
        }

        return false;
    }

}
