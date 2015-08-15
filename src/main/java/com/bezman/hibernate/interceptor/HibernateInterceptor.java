package com.bezman.hibernate.interceptor;

import com.bezman.Reference.Reference;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.type.Type;

import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Terence on 7/1/2015.
 */
public class HibernateInterceptor extends EmptyInterceptor
{



    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
    {
        super.onLoad(entity, id, state, propertyNames, types);

        invokeMethodWithAnnotation(entity, PostLoad.class);

        return false;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
    {
        invokeMethodWithAnnotation(entity, PreUpdate.class);

        return false;
    }

    @Override
    public void preFlush(Iterator entities)
    {
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
