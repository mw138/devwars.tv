package com.bezman.service;

import com.bezman.hibernate.db.DB;
import com.bezman.init.DatabaseManager;
import com.bezman.model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class InfoService {

    public List<User> bitsLeaderboard() {
        List<User> users = new LinkedList<>();

        DB.session(session -> {
            Query query = session.createQuery("from User u order by u.ranking.points desc");
            query.setMaxResults(5);

            users.addAll(query.list());
        });


        return users;
    }

    public List<User> xpLeaderboard() {
        List<User> users = new LinkedList<>();

        DB.session(session -> {
            Query query = session.createQuery("from User u order by u.ranking.xp desc");
            query.setMaxResults(5);

            users.addAll(query.list());
        });

        return users;
    }
}
