package com.bezman.interceptor;

import com.bezman.annotation.Transactional;
import com.bezman.init.DatabaseManager;
import org.hibernate.internal.SessionImpl;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class to open and close transactions on routes
 */
public class TransactionalInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        try
        {
            HandlerMethod handlerMethod = (HandlerMethod) o;

            Transactional transactional = handlerMethod.getMethodAnnotation(Transactional.class);

            if (transactional != null)
            {

                SessionImpl session = (SessionImpl) DatabaseManager.getSession();
                session.beginTransaction();

                httpServletRequest.setAttribute("session", session);
            }
        }catch (Exception e){}

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
    {
        try
        {
            HandlerMethod handlerMethod = (HandlerMethod) o;

            Transactional transactional = handlerMethod.getMethodAnnotation(Transactional.class);

            if (transactional != null)
            {
                SessionImpl session = (SessionImpl) httpServletRequest.getAttribute("session");

                session.getTransaction().commit();
                session.close();
            }
        }catch (Exception e){}
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {

    }
}
