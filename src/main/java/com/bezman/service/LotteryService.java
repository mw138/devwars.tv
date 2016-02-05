package com.bezman.service;

import com.bezman.Reference.DevBits;
import com.bezman.init.DatabaseManager;
import com.bezman.model.BaseModel;
import com.bezman.model.Ranking;
import com.bezman.model.User;
import com.bezman.model.UserInventory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class LotteryService {

    public void purchaseLotteryTicketsForUser(User user, Integer count) {
        //Time for the REAL code
        //Yay!!

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        UserInventory userInventory = (UserInventory) session.merge(user.getInventory());
        Ranking ranking = (Ranking) session.merge(user.getRanking());

        userInventory.setLotteryTickets(user.getInventory().getLotteryTickets() + count);
        ranking.setPoints(user.getRanking().getPoints() - count * DevBits.LOTTERY_TICKET_PRICE);

        session.getTransaction().commit();
        session.close();
    }

    public Integer getTotalLotteryTickets() {
        return (int) BaseModel.sumField(UserInventory.class, "lotteryTickets");
    }

    public User drawWinner() {
        User user;

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        Integer totalTickets = this.getTotalLotteryTickets();

        UserInventory inventory = (UserInventory) session.createCriteria(UserInventory.class)
            .add(Restrictions.sqlRestriction("1=1 order by lottery_tickets * rand() desc"))
            .setMaxResults(1)
            .uniqueResult();

        user = inventory.getUser();
        user.getRanking().addPoints(totalTickets * DevBits.LOTTERY_TICKET_PRICE);


        session.createQuery("update UserInventory i set i.lotteryTickets = 0")
            .executeUpdate();

        session.getTransaction().commit();
        session.close();

        return user;
    }

}
