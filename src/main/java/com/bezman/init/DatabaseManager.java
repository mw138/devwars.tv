package com.bezman.init;

import com.bezman.Reference.Reference;
import com.bezman.hibernate.interceptor.HibernateInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import javax.persistence.PostLoad;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class DatabaseManager implements IInit {
    public static SessionFactory sessionFactory;

    @Override
    public void init() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.setInterceptor(new HibernateInterceptor());

        configuration.setProperty("hibernate.connection.username", Reference.getEnvironmentProperty("db.username"));
        configuration.setProperty("hibernate.connection.password", Reference.getEnvironmentProperty("db.password"));

        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        EventListenerRegistry registry = ((SessionFactoryImpl) DatabaseManager.sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(postLoadEvent ->
        {
            HibernateInterceptor.postLoadAny(postLoadEvent.getEntity());
            HibernateInterceptor.invokeMethodWithAnnotation(postLoadEvent.getEntity(), PostLoad.class);
        });
    }

    public static Session getSession() {
        try {
            sessionFactory.getCurrentSession().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessionFactory.openSession();
    }
}
