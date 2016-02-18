package com.bezman.service;

import com.bezman.init.DatabaseManager;
import com.bezman.model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InfoService {

    public List<User> bitsLeaderboard() {
        List<User> users = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User u order by u.ranking.points desc");
        query.setMaxResults(5);

        users = query.list();

        session.close();

        return users;
    }

    public List<User> xpLeaderboard() {
        List<User> users = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User u order by u.ranking.xp desc");
        query.setMaxResults(5);

        users = query.list();

        session.close();

        return users;
    }
}
