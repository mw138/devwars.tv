package com.bezman.service;

import com.bezman.init.DatabaseManager;
import com.bezman.model.Game;
import com.bezman.model.Team;
import com.bezman.model.User;
import com.bezman.model.UserTeam;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * Created by teren on 9/16/2015.
 */
@Service
public class UserTeamService
{

    public static boolean isUserInvitedToTeam(UserTeam team, User user)
    {
        return team.getInvites().stream()
                .anyMatch(current -> current.getId() == user.getId());
    }

    public static boolean inviteUserToTeam(User user, UserTeam userTeam)
    {
        for(User currentUser : userTeam.getMembers())
        {
            if(currentUser.getId() == user.getId()) return false;
        }

        for(User currentUser : userTeam.getInvites())
        {
            if(currentUser.getId() == user.getId()) return false;
        }

        userTeam.getInvites().add(user);

        return true;
    }

    public static List<Game> getGamesForUserTeam(UserTeam userTeam, int page, int count)
    {
        Session session = DatabaseManager.getSession();

        List<Game> games = session.createQuery("from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id)")
                .setInteger("id", userTeam.getId())
                .setMaxResults(count)
                .setFirstResult(count * (page - 1))
                .list();

        session.close();

        return games;
    }

    public static List<Game> getGamesForUserTeam(UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();

        List<Game> games = session.createQuery("from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id)")
                .setInteger("id", userTeam.getId())
                .list();

        session.close();

        return games;
    }

    public static Long getNumberOfGamesPlayedForUserTeam(UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();

        Long gamesCount = (Long) session.createQuery("select count(*) from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id)")
                .setInteger("id", userTeam.getId())
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return gamesCount;
    }

    public static Long getNumberOfGamesWonForUserTeam(UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();

        Long gamesCount = (Long) session.createQuery("select count(*) from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id and team.win = true)")
                .setInteger("id", userTeam.getId())
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return gamesCount;
    }

    public static Long getNumberOfGamesLostForUserTeam(UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();

        Long gamesCount = (Long) session.createQuery("select count(*) from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id and team.win = false)")
                .setInteger("id", userTeam.getId())
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return gamesCount;
    }

    public static HashMap<Object, Object> getStatisticsForUserTeam(UserTeam userTeam)
    {
        HashMap hashMap = new HashMap();

        hashMap.put("gamesPlayed", getNumberOfGamesPlayedForUserTeam(userTeam));
        hashMap.put("gamesWon", getNumberOfGamesWonForUserTeam(userTeam));
        hashMap.put("gamesLost", getNumberOfGamesLostForUserTeam(userTeam));

        return hashMap;
    }

    public static boolean doesUserOwnUserTeam(User user, UserTeam userTeam)
    {
        return userTeam.getOwner().getId() == user.getId();
    }

    public static boolean doesUserHaveAuthorization(User user, UserTeam userTeam)
    {
        return doesUserOwnUserTeam(user, userTeam) || UserService.isUserAtLeast(user, User.Role.ADMIN);
    }
}