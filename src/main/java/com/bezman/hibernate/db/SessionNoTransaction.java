package com.bezman.hibernate.db;

import org.hibernate.Session;

@FunctionalInterface
public interface SessionNoTransaction {
    void sessionNoTransaction(Session session);
}
