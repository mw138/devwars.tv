package com.bezman.interceptor;

import com.bezman.Reference.Reference;
import com.bezman.Reference.Request;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.annotation.AllowCrossOrigin;
import com.bezman.annotation.PreAuthorization;
import com.bezman.init.DatabaseManager;
import com.bezman.model.Access;
import com.bezman.model.User;
import com.bezman.model.UserSession;
import com.bezman.service.UserService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Ref;

/**
 * Created by Terence on 3/22/2015.
 */
@Component
public class PreAuthInterceptor implements HandlerInterceptor
{
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception
    {
        boolean successful = true;

        try
        {
            HandlerMethod handlerMethod = (HandlerMethod) o;

            PreAuthorization preAuthorization = handlerMethod.getMethod().getAnnotation(PreAuthorization.class);
            User.Role requiredRole = preAuthorization == null ? User.Role.NONE : preAuthorization.minRole();

            boolean hasSecretKey = Reference.requestHasSecretKey(request);
            request.setAttribute("hasSecretKey", hasSecretKey);

            if (hasSecretKey) return true;

            Cookie cookie = Reference.getCookieFromArray(request.getCookies(), "token");

            if (cookie != null)
            {
                String token = cookie.getValue();

                User user = userService.userForToken(token);

                request.setAttribute("user", user);

                if (user == null && requiredRole == User.Role.NONE)
                {
                    return true;
                }

                if (user != null && user.getRole().ordinal() >= requiredRole.ordinal())
                {
                    return true;
                }
            } else if(requiredRole == User.Role.NONE) {
                return true;
            }

            response.getWriter().println("You need to be at least : " + requiredRole.toString());
            response.setStatus(403);
            return false;
        }catch (Exception e){}

        return successful;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
    {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {

    }
}
