package com.bezman.hibernate.interceptor;

import com.bezman.Reference.util.Util;
import com.bezman.annotation.HibernateDefault;
import com.bezman.annotation.PreFlush;
import com.bezman.annotation.PreFlushHibernateDefault;
import org.hibernate.EmptyInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Terence on 7/1/2015.
 */
public class HibernateInterceptor extends EmptyInterceptor {

    @Override
    public void preFlush(Iterator entities) {
        entities.forEachRemaining(entity ->
        {
            HibernateInterceptor.loadEntityDefaults(entity);
            HibernateInterceptor.setPreFlushers(entity);
            HibernateInterceptor.invokeMethodWithAnnotation(entity, PreFlush.class);
        });
    }

    public static void setPreFlushers(Object object) {
        Arrays.stream(object.getClass().getDeclaredFields())
                .forEach(field ->
                {
                    PreFlushHibernateDefault hibernateDefault = field.getAnnotation(PreFlushHibernateDefault.class);

                    field.setAccessible(true);

                    try {
                        if (hibernateDefault != null && field.get(object) == null) {
                            field.set(object, Util.toObject(field.getType(), hibernateDefault.value()));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void loadEntityDefaults(Object object) {
        Arrays.stream(object.getClass().getDeclaredFields())
                .forEach(field ->
                {
                    HibernateDefault hibernateDefault = field.getAnnotation(HibernateDefault.class);

                    field.setAccessible(true);

                    try {
                        if (hibernateDefault != null && field.get(object) == null) {
                            Object generatedObject = Util.toObject(field.getType(), hibernateDefault.value());
                            field.set(object, generatedObject);

                            HibernateInterceptor.loadEntityDefaults(generatedObject);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void postLoadAny(Object object) {
        HibernateInterceptor.loadEntityDefaults(object);
    }

    @SuppressWarnings("NullArgumentToVariableArgMethod")
    public static void invokeMethodWithAnnotation(Object obj, Class annotation) {
        Method[] methods = obj.getClass().getMethods();

        for (Method method : methods) {
            if (method.getAnnotation(annotation) != null && method.getParameterCount() == 0) {
                try {
                    method.invoke(obj, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
