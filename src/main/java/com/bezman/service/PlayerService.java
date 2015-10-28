package com.bezman.service;

import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.init.DatabaseManager;
import com.bezman.model.Player;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

/**
 * Created by Terence on 4/2/2015.
 */
@Service
public class PlayerService
{

    public Player getPlayer(int id) {
        Player player = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from Player where id = :id");
        query.setInteger("id", id);

        player = (Player) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return player;
    }

}
