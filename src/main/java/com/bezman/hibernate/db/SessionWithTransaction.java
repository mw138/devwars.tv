package com.bezman.hibernate.db;

import org.hibernate.Session;

@FunctionalInterface
public interface SessionWithTransaction {

    void sessionWithTransaction(Session session) throws Exception;

}
