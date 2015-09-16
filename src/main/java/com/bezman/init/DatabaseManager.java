package com.bezman.init;

import com.bezman.hibernate.interceptor.HibernateInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Terence on 1/19/2015.
 */
public class DatabaseManager implements IInit
{
    public static SessionFactory sessionFactory;

    @Override
    public void init()
    {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.setInterceptor(new HibernateInterceptor());

        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        EventListenerRegistry registry = ((SessionFactoryImpl) DatabaseManager.sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(postLoadEvent ->
        {
            HibernateInterceptor.invokeMethodWithAnnotation(postLoadEvent.getEntity(), PostLoad.class);
        });
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
