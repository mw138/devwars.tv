package com.bezman.hibernate.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Created by Terence on 7/1/2015.
 */
public class HibernateInterceptor
{
    @SuppressWarnings("NullArgumentToVariableArgMethod")
    public static void invokeMethodWithAnnotation(Object obj, Class annotation)
    {
        Method[] methods = obj.getClass().getMethods();

        for (Method method : methods)
        {
            if (method.getAnnotation(annotation) != null && method.getParameterCount() == 0)
            {
                try
                {
                    method.invoke(obj, null);
                } catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                } catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
