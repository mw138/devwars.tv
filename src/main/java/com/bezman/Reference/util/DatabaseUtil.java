package com.bezman.Reference.util;

import com.bezman.Reference.DatabaseManager;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Terence on 1/24/2015.
 */
public class DatabaseUtil
{

    public static Object getFirstFromQuery(Query query)
    {
        List results = query.list();

        return results.size() > 0 ? results.get(0) : null;
    }

    public static void saveObjects(boolean refresh, Object... objects)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        for(Object object : objects)
        {
            session.save(object);
        }

        session.getTransaction().commit();

        if (refresh)
        {
            for(Object object : objects)
            {
                session.refresh(object);
            }
        }

        session.close();
    }

    public static void deleteObjects(Object... objects)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        for(Object object : objects)
        {
            session.delete(object);
        }

        session.getTransaction().commit();
        session.close();
    }

    public static void saveOrUpdateObjects(boolean refresh, Object... objects)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        for(Object object : objects)
        {
            session.saveOrUpdate(object);
        }

        session.getTransaction().commit();

        if (refresh)
        {
            for(Object object : objects)
            {
                session.refresh(object);
            }
        }

        session.close();
    }

    public static void updateObjects(boolean refresh, Object... objects)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        for(Object object : objects)
        {
            session.update(object);
        }

        session.getTransaction().commit();

        if (refresh)
        {
            for(Object object : objects)
            {
                session.refresh(object);
            }
        }

        session.close();
    }

    public static void mergeObjects(boolean refresh, Object... objects)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        for(Object object : objects)
        {
            session.merge(object);
        }

        session.getTransaction().commit();

        if (refresh)
        {
            for(Object object : objects)
            {
                session.refresh(object);
            }
        }

        session.close();
    }

    public static void refreshObjects(Object... objects)
    {
        Session session = DatabaseManager.getSession();

        for(Object object : objects)
        {
            session.refresh(object);
        }

        session.close();
    }
}
