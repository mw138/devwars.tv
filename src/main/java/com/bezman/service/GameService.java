package com.bezman.service;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.model.Game;
import com.bezman.model.Player;
import com.bezman.model.Team;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Terence on 1/21/2015.
 */
public class GameService
{

    public static List<Game> allGames(int count, int offset)
    {
        ArrayList<Game> returnList;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from Game order by timestamp desc");
        query.setMaxResults(count);
        query.setFirstResult(offset);

        returnList = (ArrayList) query.list();

        session.close();

        return returnList;
    }

    public static Game defaultGame()
    {
        Game game = new Game();

        game.setName("Default Name");
        game.setActive(false);
        game.setTimestamp(new Timestamp(new Date().getTime()));

        Team blueTeam = new Team();
        blueTeam.setGame(game);
        blueTeam.setName("blue");
        blueTeam.setEmbedLink("Default embed link");
        blueTeam.setStatus("Waiting");
        blueTeam.setWin(false);

        Team redTeam = new Team();
        redTeam.setGame(game);
        redTeam.setName("red");
        redTeam.setEmbedLink("Default embed link");
        redTeam.setStatus("Waiting");
        redTeam.setWin(false);

        game.teams.put(blueTeam.getName(), blueTeam);
        game.teams.put(redTeam.getName(), redTeam);

        System.out.println(game.toString());

        return game;
    }

    public static Game getGame(int id)
    {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where id = :id");
        query.setInteger("id", id);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public static Game currentGame()
    {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where active = :active order by timestamp");
        query.setBoolean("active", true);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public static Game latestGame()
    {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game order by id desc");

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public static Game nearestGame()
    {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where timestamp > :timestamp and done = false order by timestamp asc");
        query.setTimestamp("timestamp", new Date());
        query.setMaxResults(1);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

}
