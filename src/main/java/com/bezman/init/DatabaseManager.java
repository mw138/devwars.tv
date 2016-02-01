package com.bezman.init;

import com.bezman.Reference.Reference;
import com.bezman.hibernate.interceptor.HibernateInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import javax.persistence.PostLoad;

public class DatabaseManager implements IInit {
    public static SessionFactory sessionFactory;

    @Override
    public void init() {
        boolean testing = Boolean.parseBoolean(System.getProperty("testing"));

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.setInterceptor(new HibernateInterceptor());

        if (!testing) {
            configuration.setProperty("hibernate.connection.username", Reference.getEnvironmentProperty("db.username"));
            configuration.setProperty("hibernate.connection.password", Reference.getEnvironmentProperty("db.password"));
        } else {
            configuration.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
            configuration.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:testdb");
            configuration.setProperty("hibernate.connection.username", "sa");
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        }

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
