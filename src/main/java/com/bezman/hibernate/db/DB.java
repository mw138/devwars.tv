package com.bezman.hibernate.db;

import com.bezman.init.DatabaseManager;
import org.hibernate.Session;

public class DB {

    public static void session(SessionNoTransaction sessionNoTransaction) {
        Session session = DatabaseManager.getSession();

        sessionNoTransaction.sessionNoTransaction(session);

        session.close();
    }

    public static void withTransaction(SessionWithTransaction sessionWithTransaction) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        try {
            sessionWithTransaction.sessionWithTransaction(session);
        } catch (Exception e) {
            e.printStackTrace();
        }

        session.getTransaction().commit();
        session.close();
    }

}
