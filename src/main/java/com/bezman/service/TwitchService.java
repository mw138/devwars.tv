package com.bezman.service;

import com.bezman.init.DatabaseManager;
import com.bezman.model.TwitchPointStorage;
import com.bezman.model.User;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwitchService {

    private UserService userService;

    @Autowired
    public TwitchService(UserService userService) {
        this.userService = userService;
    }

    public void transferFromPoolToUser(User user) {
        TwitchPointStorage twitchPointStorage = this.pointStorageForUsername(this.userService.connectedAccountForProvider(user, "TWITCH").getUsername());

        if (twitchPointStorage == null) {
            return;
        }

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        user.getRanking().addPoints(twitchPointStorage.getPoints());
        user.getRanking().addXP(twitchPointStorage.getXp());

        session.merge(user);
        session.delete(twitchPointStorage);

        session.getTransaction().commit();
        session.close();
    }


    public TwitchPointStorage pointStorageForUsername(String username) {
        TwitchPointStorage twitchPointStorage = null;

        Session session = DatabaseManager.getSession();

        twitchPointStorage = (TwitchPointStorage) session.createCriteria(TwitchPointStorage.class)
                .add(Restrictions.eq("username", username).ignoreCase())
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return twitchPointStorage;
    }

}
