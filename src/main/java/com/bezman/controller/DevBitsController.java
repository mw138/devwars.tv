package com.bezman.controller;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.model.Ranking;
import com.bezman.model.TwitchPointStorage;
import com.bezman.model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.Authenticator;

/**
 * Created by Terence on 5/15/2015.
 */
@Controller
@RequestMapping("/v1/devbits")
public class DevBitsController
{

    @RequestMapping("/{twitchUsername}")
    public ResponseEntity getTwitchUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("twitchUsername") String twitchUsername)
    {
        ResponseEntity responseEntity = null;

        Session session = DatabaseManager.getSession();

        Query userQuery = session.createQuery("select u from User u left join u.connectedAccounts as a where (lower(substring(u.username, 1, length(u.username)-4)) = :username and u.provider = 'TWITCH') or (lower(a.username) = :username and a.provider = 'TWITCH')");
        userQuery.setString("username", twitchUsername.toLowerCase());

        User user = (User) DatabaseUtil.getFirstFromQuery(userQuery);

        if (user != null)
        {
            responseEntity = new ResponseEntity(user.getRanking().getPoints(), HttpStatus.OK);
        } else
        {
            Query twitchPointsQuery = session.createQuery("from TwitchPointStorage where username = :username");
            twitchPointsQuery.setString("username", twitchUsername);

            TwitchPointStorage twitchPointStorage = (TwitchPointStorage) DatabaseUtil.getFirstFromQuery(twitchPointsQuery);
            if (twitchPointStorage != null)
            {
                responseEntity = new ResponseEntity(twitchPointStorage.getPoints(), HttpStatus.OK);
            }
        }

        session.close();

        if (responseEntity == null)
        {
            responseEntity = new ResponseEntity("0", HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/{twitchUsernames}/{amount}", method = {RequestMethod.PUT})
    public ResponseEntity addToTwitchUser(HttpServletRequest request, @PathVariable("twitchUsernames") String usernames,
                                          @PathVariable(value = "amount") int points,
                                          @RequestParam(value = "xp", required = false, defaultValue = "0") int xp)
    {
        Session session = DatabaseManager.getSession();

        ResponseEntity responseEntity = null;

        System.out.println("USERNAMES : " + usernames);

        String[] usernamesArray = usernames.split(",");

        for(String username : usernamesArray)
        {
            Query userQuery = session.createQuery("select u from User u left join u.connectedAccounts as a where (lower(substring(u.username, 1, length(u.username)-4)) = :username and u.provider = 'TWITCH') or (lower(a.username) = :username and a.provider = 'TWITCH')");
            userQuery.setString("username", username.toLowerCase());

            User user = (User) DatabaseUtil.getFirstFromQuery(userQuery);

            System.out.println("Current username is " + username);
            System.out.println(user);

            if (user != null)
            {
                System.out.println("Found user");

                if (user.getRanking() == null)
                {
                    Ranking ranking = new Ranking();
                    ranking.setId(user.getId());
                }

                Ranking ranking = user.getRanking();
                ranking.setPoints(ranking.getPoints() + points);
                ranking.setXp(ranking.getXp() + xp);

                DatabaseUtil.saveOrUpdateObjects(false, ranking);

                responseEntity = new ResponseEntity(Reference.gson.toJson(ranking), HttpStatus.OK);
            } else
            {
                System.out.println("Not found");

                Query twitchQuery = session.createQuery("from TwitchPointStorage s where s.username = :username");
                twitchQuery.setString("username", username);

                TwitchPointStorage pointStorage = (TwitchPointStorage) DatabaseUtil.getFirstFromQuery(twitchQuery);

                if (pointStorage != null)
                {
                    pointStorage.setPoints(pointStorage.getPoints() + points);
                    pointStorage.setXp(pointStorage.getXp() + xp);
                } else
                {
                    pointStorage = new TwitchPointStorage();
                    pointStorage.setUsername(username);
                    pointStorage.setPoints(points);
                    pointStorage.setXp(xp);
                }

                System.out.println("UPDATING POINT STORAGE FOR " + pointStorage.getUsername());
                DatabaseUtil.saveOrUpdateObjects(false, pointStorage);

                responseEntity = new ResponseEntity(Reference.gson.toJson(pointStorage), HttpStatus.OK);
            }
        }

        session.close();

        return responseEntity;
    }
}
