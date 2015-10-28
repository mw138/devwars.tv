package com.bezman.service;

import com.bezman.init.DatabaseManager;
import com.bezman.model.Rank;
import com.bezman.model.Ranking;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class RankService
{

    public Rank rankForRanking(Ranking ranking)
    {
        Session session = DatabaseManager.getSession();

        Rank rank = (Rank) session.createCriteria(Rank.class)
                .add(Restrictions.le("xpRequired", ranking.getXp().intValue()))
                .addOrder(Order.desc("xpRequired"))
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return rank;
    }

}
