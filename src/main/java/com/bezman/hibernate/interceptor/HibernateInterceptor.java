package com.bezman.hibernate.interceptor;

import com.bezman.annotation.PreFlush;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import sun.invoke.empty.Empty;

import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Created by Terence on 7/1/2015.
 */
public class HibernateInterceptor extends EmptyInterceptor
{

    @Override
    public void preFlush(Iterator entities) {
        entities.forEachRemaining(entity -> HibernateInterceptor.invokeMethodWithAnnotation(entity, PreFlush.class));
    }

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
