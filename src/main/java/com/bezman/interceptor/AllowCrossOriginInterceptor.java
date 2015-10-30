package com.bezman.interceptor;

import com.bezman.Reference.Reference;
import com.bezman.annotation.AllowCrossOrigin;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AllowCrossOriginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            AllowCrossOrigin crossOrigin = handlerMethod.getMethodAnnotation(AllowCrossOrigin.class);

            //Make sure it's not a double header
            if (!Reference.isProduction()) {
                response.addHeader("Access-Control-Allow-Origin", "http://localhost:81");
                response.addHeader("Access-Control-Allow-Credentials", "true");
            }

            if (Reference.isProduction() && crossOrigin != null) {
                response.addHeader("Access-Control-Allow-Origin", crossOrigin.from());
            }

        } catch (ClassCastException ignored) {
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
