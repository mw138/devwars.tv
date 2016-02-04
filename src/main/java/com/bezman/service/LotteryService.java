package com.bezman.service;

import com.bezman.init.DatabaseManager;
import com.bezman.model.User;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
public class LotteryService {

    public void purchaseLotteryTicketsForUser(User user, Integer count) {
        //Time for the REAL code
        //Yay!!
        int price = 100;

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        session.merge(user.getInventory());
        session.merge(user.getRanking());

        user.getInventory().setLotteryTickets(user.getInventory().getLotteryTickets() + count);
        user.getRanking().setPoints(user.getRanking().getPoints() - count * price);

        session.getTransaction().commit();
        session.close();

    }

}
