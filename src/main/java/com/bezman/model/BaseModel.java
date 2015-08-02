package com.bezman.model;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.xml.crypto.Data;
import java.util.List;

/**
 * Created by Terence on 1/24/2015.
 */
public class BaseModel
{
    public static BaseModel byID(Class hibernateClass, int id)
    {
        BaseModel obj = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from " + hibernateClass.getSimpleName() + " where id = :id");
        query.setInteger("id", id);
        query.setMaxResults(1);

        obj = (BaseModel) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return obj;
    }


    public static List<BaseModel> all(Class hibernateClass)
    {
        List all = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from " + hibernateClass.getSimpleName());

        all = query.list();

        session.close();

        return all;
    }

    public static int count(Class hibernateClass)
    {
        int count = 0;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("select count(*) from " + hibernateClass.getSimpleName());

        count = Integer.valueOf(String.valueOf(DatabaseUtil.getFirstFromQuery(query)));

        session.close();

        return count;
    }

    public static double sumField(Class hibernateClass, String field)
    {
        double count = 0;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("select sum(o." + field + ") from " + hibernateClass.getSimpleName() + " o");

        count = Double.valueOf(String.valueOf(DatabaseUtil.getFirstFromQuery(query)));

        session.close();

        return count;
    }

    public static boolean rowExists(Class hibernateClass, String queryString, Object... params)
    {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from " + hibernateClass.getSimpleName() + " where " + queryString);

        System.out.println(query.getQueryString());

        for(int i = 0; i < params.length; i++)
        {
            System.out.println(i + " : " + params[i]);
            query.setParameter(i, params[i]);
        }

        boolean hasRow =  DatabaseUtil.getFirstFromQuery(query) != null;

        session.close();

        return hasRow;
    }

    public void delete()
    {
        DatabaseUtil.deleteObjects(this);
    }
}
