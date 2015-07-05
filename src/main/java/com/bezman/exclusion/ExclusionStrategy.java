package com.bezman.exclusion;

import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import org.hibernate.proxy.HibernateProxy;

/**
 * Created by Terence on 4/15/2015.
 */
public class ExclusionStrategy implements com.google.gson.ExclusionStrategy
{
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes)
    {
        return fieldAttributes.getAnnotation(GsonExclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass)
    {
        return aClass.equals(HibernateProxy.class);
    }
}
