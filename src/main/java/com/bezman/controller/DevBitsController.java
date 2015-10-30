package com.bezman.controller;

import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.annotation.JSONParam;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
import com.bezman.init.DatabaseManager;
import com.bezman.model.Ranking;
import com.bezman.model.TwitchPointStorage;
import com.bezman.model.User;
import com.bezman.request.model.EarnedBet;
import com.bezman.service.UserService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Terence on 5/15/2015.
 */
@Controller
@RequestMapping("/v1/devbits")
public class DevBitsController {

    @Autowired
    UserService userService;

    /**
     * Gets devbits of twitch user
     *
     * @param request
     * @param response
     * @param twitchUsername Twitch user's Twitch Username
     * @return
     */
    @RequestMapping("/{twitchUsername}")
    public ResponseEntity getTwitchUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("twitchUsername") String twitchUsername) {
        ResponseEntity responseEntity = null;

        Session session = DatabaseManager.getSession();

        Query userQuery = session.createQuery("select u from User u left join u.connectedAccounts as a where (lower(substring(u.username, 1, length(u.username)-4)) = :username and u.provider = 'TWITCH') or (lower(a.username) = :username and a.provider = 'TWITCH')");
        userQuery.setString("username", twitchUsername.toLowerCase());

        User user = (User) DatabaseUtil.getFirstFromQuery(userQuery);

        if (user != null) {
            responseEntity = new ResponseEntity(user.getRanking().getPoints(), HttpStatus.OK);
        } else {
            Query twitchPointsQuery = session.createQuery("from TwitchPointStorage where username = :username");
            twitchPointsQuery.setString("username", twitchUsername);

            TwitchPointStorage twitchPointStorage = (TwitchPointStorage) DatabaseUtil.getFirstFromQuery(twitchPointsQuery);
            if (twitchPointStorage != null) {
                responseEntity = new ResponseEntity(twitchPointStorage.getPoints(), HttpStatus.OK);
            }
        }

        session.close();

        if (responseEntity == null) {
            responseEntity = new ResponseEntity("0", HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    /**
     * Awards a twitch user with bits or xp
     *
     * @param request
     * @param usernames Comma separated list of twitch users
     * @param points    How many points to add to user (Can be negative)
     * @param xp        How much xp to add to user (Can be negative)
     * @return
     */
    @RequestMapping(value = "/{twitchUsernames}/{amount}", method = {RequestMethod.PUT})
    public ResponseEntity addToTwitchUser(HttpServletRequest request, @PathVariable("twitchUsernames") String usernames,
                                          @PathVariable(value = "amount") int points,
                                          @RequestParam(value = "xp", required = false, defaultValue = "0") int xp) {
        Session session = DatabaseManager.getSession();

        ResponseEntity responseEntity = null;

        System.out.println("USERNAMES : " + usernames);

        String[] usernamesArray = usernames.split(",");

        for (String username : usernamesArray) {
            Query userQuery = session.createQuery("select u from User u left join u.connectedAccounts as a where (lower(substring(u.username, 1, length(u.username)-4)) = :username and u.provider = 'TWITCH') or (lower(a.username) = :username and a.provider = 'TWITCH')");
            userQuery.setString("username", username.toLowerCase());

            User user = (User) DatabaseUtil.getFirstFromQuery(userQuery);


            if (user != null) {
                System.out.println("Found user");

                if (user.getRanking() == null) {
                    Ranking ranking = new Ranking();
                    ranking.setId(user.getId());
                }

                Ranking ranking = user.getRanking();
                ranking.setPoints(ranking.getPoints() + points);
                ranking.setXp(ranking.getXp() + xp);

                DatabaseUtil.saveOrUpdateObjects(false, ranking);

                responseEntity = new ResponseEntity(ranking, HttpStatus.OK);
            } else {
                System.out.println("Not found");

                Query twitchQuery = session.createQuery("from TwitchPointStorage s where s.username = :username");
                twitchQuery.setString("username", username);

                TwitchPointStorage pointStorage = (TwitchPointStorage) DatabaseUtil.getFirstFromQuery(twitchQuery);

                if (pointStorage != null) {
                    pointStorage.setPoints(pointStorage.getPoints() + points);
                    pointStorage.setXp(pointStorage.getXp() + xp);
                } else {
                    pointStorage = new TwitchPointStorage();
                    pointStorage.setUsername(username);
                    pointStorage.setPoints(points);
                    pointStorage.setXp(xp);
                }

                System.out.println("UPDATING POINT STORAGE FOR " + pointStorage.getUsername());
                DatabaseUtil.saveOrUpdateObjects(false, pointStorage);

                responseEntity = new ResponseEntity(pointStorage, HttpStatus.OK);
            }
        }

        session.close();

        return responseEntity;
    }

    /**
     * Updates users earned bets so we can see how much they've won from betting
     *
     * @param session
     * @param earnedBets The amount of Devbits earned
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/earnedbets", method = RequestMethod.POST)
    public ResponseEntity earnBetsForUsers(SessionImpl session, @JSONParam("bets") EarnedBet[] earnedBets) {
        for (EarnedBet earnedBet : earnedBets) {
            Query userQuery = session.createQuery("select u from User u left join u.connectedAccounts as a where (lower(substring(u.username, 1, length(u.username)-4)) = :username and u.provider = 'TWITCH') or (lower(a.username) = :username and a.provider = 'TWITCH')");
            userQuery.setString("username", earnedBet.getTwitchUsername().toLowerCase());

            User user = (User) userQuery.uniqueResult();

            if (user != null) {
                user.setBettingBitsEarned(user.getBettingBitsEarned() + earnedBet.getPointsEarned());
            }
        }

        return null;
    }

    /**
     * Adds a watched game to a user
     *
     * @param session
     * @param usernames JSON Array of twitch usernames
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/watched", method = RequestMethod.POST)
    public ResponseEntity earnBetsForUsers(SessionImpl session, @JSONParam("usernames") String[] usernames) {
        List<User> updatedUsers = new ArrayList<>();

        for (String username : usernames) {
            User user = userService.userForTwitchUsername(username);

            if (user != null) {
                user = (User) session.merge(user);

                user.setGamesWatched(user.getGamesWatched() + 1);
                updatedUsers.add(user);
            }
        }

        return new ResponseEntity(updatedUsers, HttpStatus.OK);
    }

    /**
     * Gives user badge for going all in
     *
     * @param session
     * @param usernames JSON Array of twitch usernames to award
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/allin", method = RequestMethod.POST)
    public ResponseEntity allIn(SessionImpl session, @JSONParam("usernames") String[] usernames) {
        for (String username : usernames) {
            User user = userService.userForTwitchUsername(username);

            if (user != null) {
                user.awardBadgeForName("I'm All In");
            }
        }

        return new ResponseEntity(usernames, HttpStatus.OK);
    }

}
