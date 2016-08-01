package com.bezman.service;

import com.bezman.hibernate.db.DB;
import com.bezman.init.DatabaseManager;
import com.bezman.model.Ranking;
import com.bezman.model.User;
import com.bezman.model.UserInventory;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
public class LotteryService {

    public void purchaseLotteryTicketsForUser(User user, Integer count) {
        int price = 100;

        DB.withTransaction(session -> {
            UserInventory userInventory = (UserInventory) session.merge(user.getInventory());
            Ranking ranking = (Ranking) session.merge(user.getRanking());

            userInventory.setLotteryTickets(user.getInventory().getLotteryTickets() + count);
            ranking.setPoints(user.getRanking().getPoints() - count * price);
        });
    }

}
