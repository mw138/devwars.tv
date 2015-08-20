package com.bezman.interceptor;

import com.bezman.annotation.UnitOfWork;
import com.bezman.init.DatabaseManager;
import org.hibernate.Session;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class to open and close sessions on routes
 */
public class UnitOfWorkInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        try
        {
            HandlerMethod handlerMethod = (HandlerMethod) o;

            UnitOfWork unitOfWork = handlerMethod.getMethodAnnotation(UnitOfWork.class);

            if (unitOfWork != null)
            {
                httpServletRequest.setAttribute("session", DatabaseManager.getSession());
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

           UnitOfWork unitOfWork = handlerMethod.getMethodAnnotation(UnitOfWork.class);

           if (unitOfWork != null)
           {
               Session session = (Session) httpServletRequest.getAttribute("session");
               session.close();
           }
       }catch (Exception e){}
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {

    }
}
