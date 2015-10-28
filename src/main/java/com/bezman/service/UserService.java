package com.bezman.service;

import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.init.DatabaseManager;
import com.bezman.model.ConnectedAccount;
import com.bezman.model.TwitchPointStorage;
import com.bezman.model.User;
import com.bezman.model.UserSession;
import com.bezman.storage.FileStorage;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.Files;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Created by Terence on 1/21/2015.
 */
public class UserService
{
    public static FileStorage fileStorage;

    public static void addUser(User user)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        try
        {
            session.save(user);
            session.flush();
        } catch (Exception e)
        {
            System.out.println("Could not save user : " + e.getMessage());
        }

        System.out.println("User ID : " + user.getId());

        session.getTransaction().commit();
        session.close();
    }

    public static User userForUsername(String username)
    {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where username = :username");
        query.setString("username", username);

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return user;
    }

    public static boolean userHasProvider(User user, String provider)
    {
        return usernameForProvider(user, provider) != null;
    }

    public static String usernameForProvider(User user, String provider)
    {
        if (provider.equals(user.getProvider()))
        {
            return user.getUsername();
        }

        Optional<ConnectedAccount> connectedAccount = user.getConnectedAccounts().stream().filter(account -> provider.equals(account.getProvider())).findFirst();

        if (connectedAccount.isPresent())
        {
            return connectedAccount.get().getUsername();
        }

        return null;
    }

    public static User userForUsernameDevWars(String username)
    {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where username = :username and provider =  null");
        query.setString("username", username);

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return user;
    }

    public static User userForUsernameOrNewVeteranUser(String username)
    {
        User user = UserService.userForUsername(username);

        if (user != null)
        {
            return user;
        } else {
            return UserService.createVeteranUserForUsername(username);
        }
    }

    public static User createVeteranUserForUsername(String username)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        User user = new User();
        user.setUsername(username);
        user.setVeteran(true);
        user.setProvider("TWITCH");
        user.setRole(User.Role.USER);

        session.save(user);

        session.getTransaction().commit();
        session.close();

        return user;
    }

    public static User userForEmail(String email)
    {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where email = :email");
        query.setString("email", email);

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return user;
    }

    public static User getUser(int id)
    {
        User user = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from User where id = :id");
        query.setInteger("id", id);

        user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return user;
    }

    public static User getLastUser()
    {
        User returnUser = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from User order by id desc");

        returnUser = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return returnUser;
    }

    public static User userForTwitchUsername(String username)
    {
        Session session = DatabaseManager.getSession();

        Query userQuery = session.createQuery("select u from User u left join u.connectedAccounts as a where (lower(substring(u.username, 1, length(u.username)-4)) = :username and u.provider = 'TWITCH') or (lower(u.username) = :username and u.provider = 'TWITCH')");
        userQuery.setString("username", username.toLowerCase());
        userQuery.setMaxResults(1);

        User user = (User) userQuery.uniqueResult();

        session.close();

        return user;
    }

    public static Integer userCount()
    {
        Long count = null;

        Session session = DatabaseManager.getSession();

        count = (Long) session.createQuery("select count(*) from User").uniqueResult();

        session.close();

        return count.intValue();
    }

    public static void addTwitchPointsToUser(User user)
    {
        Session session = DatabaseManager.getSession();

        Optional<ConnectedAccount> connectedAccount = user.getConnectedAccounts().stream()
                .filter(account ->
                        "TWITCH".equals(account.getProvider()) && account.getDisconnected() == false)
                .findFirst();

        boolean isTwitchAccount = "TWITCH".equals(user.getProvider());

        if (!connectedAccount.isPresent() && !isTwitchAccount) return;

        String username = connectedAccount.isPresent() ? connectedAccount.get().getUsername() : user.getUsername();

        TwitchPointStorage twitchPointStorage = (TwitchPointStorage) session.createQuery("from TwitchPointStorage where username = :username")
                .setString("username", username)
                .setMaxResults(1)
                .uniqueResult();

        if (twitchPointStorage != null)
        {
            user.getRanking().addPoints(twitchPointStorage.getPoints());
            user.getRanking().addXP(twitchPointStorage.getXp());

            session.delete(twitchPointStorage);
        }

        session.close();
    }

    public static List<User> searchUsers(String username)
    {
        Session session = DatabaseManager.getSession();

        List<User> users = session.createQuery("select user.id as id, user.username as username from User user where lower(username) LIKE :username")
                .setString("username", "%" + username.toLowerCase() + "%")
                .setResultTransformer(Transformers.aliasToBean(User.class))
                .list();

        session.close();

        return users;
    }

    public static boolean isUserAtLeast(User user, User.Role role)
    {
        User.Role userRole = user.getRole();

        return (userRole.ordinal() >= role.ordinal());
    }

    public static User userForToken(String token)
    {
        Session session = DatabaseManager.getSession();

        User user = (User) session.createQuery("from User user where user.session.sessionID = :token")
                .setString("token", token)
                .setMaxResults(1)
                .uniqueResult();

        session.flush();
        session.close();

        return user;
    }

    public static void logoutUser(User user)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        UserSession userSession = (UserSession) session.createQuery("from UserSession session where session.id = :id")
                .setInteger("id", user.getId())
                .setMaxResults(1)
                .uniqueResult();

        session.delete(userSession);

        session.getTransaction().commit();
        session.flush();
        session.close();
    }

    public static String pathForProfilePictureForUser(User user)
    {
        return fileStorage.shareableUrlForPath(fileStorage.PROFILE_PICTURE_PATH + "/" + user.getId() + "/avatar");
    }
    public static void changeProfilePictureForUser(User user, InputStream inputStream) throws IOException, DbxException
    {
        fileStorage.uploadFile("/profilepics/" + user.getId() + "/avatar", inputStream);

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        user.setAvatarURL(pathForProfilePictureForUser(user));

        session.merge(user);

        session.getTransaction().commit();
        session.close();
    }
}