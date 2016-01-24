package com.bezman.service;

import com.bezman.hibernate.interceptor.HibernateInterceptor;
import com.bezman.init.DatabaseManager;
import com.bezman.model.Activity;
import com.bezman.model.Objective;
import com.bezman.model.ShopItem;
import com.bezman.model.User;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

    public void purchaseItemForUser(ShopItem shopItem, User user) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        user.getInventory().setUser(user);
        user.getRanking().addPoints(-1 * shopItem.getPrice());

        Activity activity = new Activity(user, user, "Purchased: " + shopItem.getName(), -1 * shopItem.getPrice(), 0);

        session.save(activity);
        session.saveOrUpdate(user);

        session.getTransaction().commit();
        session.close();
    }

}
