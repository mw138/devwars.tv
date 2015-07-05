package com.bezman.interceptor;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.Reference.Request;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.AllowCrossOrigin;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.Access;
import com.bezman.model.SecretKey;
import com.bezman.model.User;
import com.bezman.model.UserSession;
import com.google.gson.annotations.Expose;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.metamodel.relational.Database;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

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

            Access access = new Access();
            access.setRoute(request.getRequestURL().toString());
            DatabaseUtil.saveObjects(false, access);

            boolean hasSecretKey  = Reference.requestHasSecretKey(request);

            PreAuthorization auth = handlerMethod.getMethod().getAnnotation(PreAuthorization.class);

            AllowCrossOrigin crossOrigin = handlerMethod.getMethod().getAnnotation(AllowCrossOrigin.class);

            if (crossOrigin != null)
            {
                response.addHeader("Access-Control-Allow-Origin", crossOrigin.from());
            }

            if (auth != null)
            {
                User.Role requiredRole = auth.minRole();

                Cookie cookie = Reference.getCookieFromArray(request.getCookies(), "token");

                if (cookie != null)
                {
                    String token = cookie.getValue();

                    if (token != null)
                    {
                        Session session = DatabaseManager.getSession();
                        Query query = session.createQuery("from UserSession where sessionID = :session");
                        query.setString("session", token);

                        UserSession userSession = (UserSession) DatabaseUtil.getFirstFromQuery(query);

                        if (userSession != null)
                        {
                            Query userQuery = session.createQuery("from User where id = :id");
                            userQuery.setInteger("id", userSession.getId());

                            User user = (User) DatabaseUtil.getFirstFromQuery(userQuery);

                            if (requiredRole == User.Role.NONE)
                            {
                                request.setAttribute("user", user);
                                return true;
                            }

                            if (user != null)
                            {
                                User.Role userRole = user.role;

                                System.out.println(requiredRole.toString() + ", " + requiredRole.ordinal());
                                System.out.println(userRole.toString() + ", " + userRole.ordinal());

                                if (userRole.ordinal() >= requiredRole.ordinal())
                                {
                                    request.setAttribute("user", user);
                                    return true;
                                }
                            }
                        }

                        session.close();
                    }
                }

                if(hasSecretKey)
                {
                    return true;
                }

                if (requiredRole == User.Role.NONE)
                {
                    return true;
                }

                response.setStatus(403);
                response.getWriter().print("You need to be at least " + requiredRole.toString());

                return false;
            } else
            {
                return true;
            }
        }catch (Exception e)
        {

        }

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
