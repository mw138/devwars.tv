package com.bezman.Reference;

import com.bezman.hibernate.interceptor.HibernateInterceptor;
import com.google.gson.Gson;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Terence on 1/19/2015.
 */
public class DatabaseManager
{
    public static SessionFactory sessionFactory;

    public static void init()
    {
        Gson gson = new Gson();

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        configuration.setInterceptor(new HibernateInterceptor());

        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static Session getSession()
    {
        try
        {
            sessionFactory.getCurrentSession().close();
        } catch (Exception e){e.printStackTrace();}

        return sessionFactory.openSession();
    }

    public static void clearCache()
    {
        Map classMap = sessionFactory.getAllClassMetadata();
        Collection values = classMap.values();
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            ClassMetadata meta = (ClassMetadata)iter.next();
            sessionFactory.evict(meta.getMappedClass());
        }

        sessionFactory.getCache().evictQueryRegions();
        sessionFactory.getCache().evictCollectionRegions();
        sessionFactory.getCache().evictEntityRegions();
        sessionFactory.getCache().evictNaturalIdRegions();
    }
}
