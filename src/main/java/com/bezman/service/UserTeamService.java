package com.bezman.service;

import com.bezman.Reference.Reference;
import com.bezman.init.DatabaseManager;
import com.bezman.model.*;
import com.bezman.storage.FileStorage;
import com.dropbox.core.DbxException;
import org.apache.commons.io.IOUtils;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.List;

@Service
public class UserTeamService
{
    @Autowired
    public FileStorage fileStorage;

    @Autowired
    UserService userService;

    public boolean isUserInvitedToTeam(UserTeam team, User user)
    {
        return team.getInvites().stream()
                .anyMatch(current -> current.getId() == user.getId());
    }

    public void disbandTeam(UserTeam userTeam, Integer newOwner)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        userTeam = (UserTeam) session.merge(userTeam);

        if (newOwner != null)
        {
            User newOwnerUser = (User) session.get(User.class, newOwner);

            userTeam.setOwner(newOwnerUser);
        } else
        {
            userTeam.getInvites().stream()
                    .forEach(session::delete);

            userTeam.setOwner(null);
        }

        session.getTransaction().commit();
        session.close();
    }

    public boolean inviteUserToTeam(User user, UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        for (User currentUser : userTeam.getMembers())
        {
            if (currentUser.getId() == user.getId()) return false;
        }

        for (UserTeamInvite currentInvite : userTeam.getInvites())
        {
            User currentUser = currentInvite.getUser();

            if (currentUser.getId() == user.getId()) return false;
        }

        session.save(new UserTeamInvite(user, userTeam));

        session.getTransaction().commit();
        session.close();

        return true;
    }

    public List<Game> getGamesForUserTeam(UserTeam userTeam, int page, int count)
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

    public List<Game> getGamesForUserTeam(UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();

        List<Game> games = session.createQuery("from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id)")
                .setInteger("id", userTeam.getId())
                .list();

        session.close();

        return games;
    }

    public Long getNumberOfGamesPlayedForUserTeam(UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();

        Long gamesCount = (Long) session.createQuery("select count(*) from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id)")
                .setInteger("id", userTeam.getId())
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return gamesCount;
    }

    public Long getNumberOfGamesWonForUserTeam(UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();

        Long gamesCount = (Long) session.createQuery("select count(*) from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id and team.win = true)")
                .setInteger("id", userTeam.getId())
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return gamesCount;
    }

    public Long getNumberOfGamesLostForUserTeam(UserTeam userTeam)
    {
        Session session = DatabaseManager.getSession();

        Long gamesCount = (Long) session.createQuery("select count(*) from Game game where id in (select team.game.id from Team team where team.userTeam.id = :id and team.win = false)")
                .setInteger("id", userTeam.getId())
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return gamesCount;
    }

    public Integer getPositionInLeaderBoards(UserTeam userTeam)
    {
        Long place = 0L;

        Session session = DatabaseManager.getSession();

        place = (Long) session.createQuery("select count(*) from UserTeam u where u.gamesWon >= :gamesWon")
                .setLong("gamesWon", userTeam.getGamesWon())
                .setMaxResults(0)
                .uniqueResult();

        session.close();

        return place.intValue();
    }

    public HashMap<Object, Object> getStatisticsForUserTeam(UserTeam userTeam)
    {
        HashMap hashMap = new HashMap();

        hashMap.put("gamesPlayed", getNumberOfGamesPlayedForUserTeam(userTeam));
        hashMap.put("gamesWon", getNumberOfGamesWonForUserTeam(userTeam));
        hashMap.put("gamesLost", getNumberOfGamesLostForUserTeam(userTeam));
        hashMap.put("position", getPositionInLeaderBoards(userTeam));

        return hashMap;
    }

    public List<UserTeamInvite> teamsInvitedTo(User user)
    {
        List<UserTeamInvite> returnList;

        Session session = DatabaseManager.getSession();

        returnList = session.createCriteria(UserTeamInvite.class)
                .add(Restrictions.eq("user.id", user.getId()))
                .list();

        session.close();

        return returnList;
    }

    public void changeTeamPicture(UserTeam userTeam, InputStream inputStream) throws IOException, DbxException
    {
        fileStorage.uploadFile(fileStorage.TEAM_PICTURE_PATH + "/" + userTeam.getId() + "/avatar", inputStream);

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        userTeam.setAvatarURL(fileStorage.shareableUrlForPath(fileStorage.TEAM_PICTURE_PATH + "/" + userTeam.getId() + "/avatar"));

        session.merge(userTeam);

        session.getTransaction().commit();
        session.close();
    }

    public boolean isNameTaken(String name)
    {
        if (name.isEmpty()) return true;

        Session session = DatabaseManager.getSession();

        UserTeam userTeam = (UserTeam) session.createCriteria(UserTeam.class)
                .add(Restrictions.ilike("name", name))
                .add(Restrictions.isNotNull("owner"))
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return userTeam != null;
    }

    public boolean isTagTaken(String name)
    {
        if (name.isEmpty()) return true;

        Session session = DatabaseManager.getSession();

        UserTeam userTeam = (UserTeam) session.createCriteria(UserTeam.class)
                .add(Restrictions.ilike("tag", name))
                .add(Restrictions.isNotNull("owner"))
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return userTeam != null;
    }

    public boolean doesUserOwnUserTeam(User user, UserTeam userTeam)
    {
        return userTeam.getOwner().getId() == user.getId();
    }

    public boolean doesUserHaveAuthorization(User user, UserTeam userTeam)
    {
        return doesUserOwnUserTeam(user, userTeam) || userService.isUserAtLeast(user, User.Role.ADMIN);
    }

    public boolean doesUserBelongToTeam(User user)
    {
        return user.getTeam() != null && user.getTeam().getOwner() != null;
    }
}
