package com.bezman.interceptor;

import com.bezman.Reference.Reference;
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
public class PreAuthInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception
    {
        boolean successful = true;

        try
        {
            HandlerMethod handlerMethod = (HandlerMethod) o;

            PreAuthorization preAuthorization = handlerMethod.getMethod().getAnnotation(PreAuthorization.class);
            User.Role requiredRole = preAuthorization == null ? User.Role.NONE : preAuthorization.minRole();

            request.setAttribute("hasSecretKey", Reference.requestHasSecretKey(request));

            Cookie cookie = Reference.getCookieFromArray(request.getCookies(), "token");

            if (cookie != null)
            {
                String token = cookie.getValue();

                User user = UserService.userForToken(token);

                request.setAttribute("user", user);

                if (user == null && requiredRole == User.Role.NONE)
                {
                    return true;
                }

                if (user != null && user.getRole().ordinal() >= requiredRole.ordinal())
                {
                    return true;
                }
            }

            response.getWriter().println("You need to be at least : " + requiredRole.toString());
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
