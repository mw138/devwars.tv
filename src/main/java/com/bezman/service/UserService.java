package com.bezman.service;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.exception.NonDevWarsUserException;
import com.bezman.exception.UserNotFoundException;
import com.bezman.init.DatabaseManager;
import com.bezman.model.ConnectedAccount;
import com.bezman.model.TwitchPointStorage;
import com.bezman.model.User;
import com.bezman.model.UserSession;
import com.bezman.storage.FileStorage;
import com.dropbox.core.DbxException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Created by Terence on 1/21/2015.
 */
@Service
public class UserService {
    @Autowired
    public FileStorage fileStorage;

    @Autowired
    Security security;

    public void addUser(User user) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        try {
            session.save(user);
        } catch (Exception e) {
            System.out.println("Could not save user : " + e.getMessage());
        }

        System.out.println("User ID : " + user.getId());

        session.getTransaction().commit();
        session.close();
    }

    public User userForUsername(String username) {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where username = :username");
        query.setString("username", username);

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return user;
    }

    public boolean userHasProvider(User user, String provider) {
        return usernameForProvider(user, provider) != null;
    }

    public String usernameForProvider(User user, String provider) {
        if (provider.equals(user.getProvider())) {
            return user.getUsername();
        }

        Optional<ConnectedAccount> connectedAccount = user.getConnectedAccounts().stream().filter(account -> provider.equals(account.getProvider())).findFirst();

        if (connectedAccount.isPresent()) {
            return connectedAccount.get().getUsername();
        }

        return null;
    }

    public User userForUsernameDevWars(String username) {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where username = :username and provider =  null");
        query.setString("username", username);

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return user;
    }

    public User userForUsernameOrNewVeteranUser(String username) {
        User user = this.userForUsername(username);

        if (user != null) {
            return user;
        } else {
            return this.createVeteranUserForUsername(username);
        }
    }

    public User createVeteranUserForUsername(String username) {
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

    public User userForEmail(String email) {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where email = :email");
        query.setString("email", email);

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return user;
    }

    public User getUser(int id) {
        User user = null;

        Session session = DatabaseManager.getSession();

        user = (User) session.get(User.class, id);

        session.close();

        return user;
    }

    public User getLastUser() {
        User returnUser = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from User order by id desc");

        returnUser = (User) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return returnUser;
    }

    public User userForTwitchUsername(String username) {
        Session session = DatabaseManager.getSession();

        Query userQuery = session.createQuery("select u from User u left join u.connectedAccounts as connectedAccount where (lower(substring(u.username, 1, length(u.username)-4)) = :username and u.provider = 'TWITCH') or (lower(connectedAccount.username) = :username and connectedAccount.provider = 'TWITCH')");
        userQuery.setString("username", username.toLowerCase());
        userQuery.setMaxResults(1);

        User user = (User) userQuery.uniqueResult();

        session.close();

        return user;
    }

    public Integer userCount() {
        Long count = null;

        Session session = DatabaseManager.getSession();

        count = (Long) session.createQuery("select count(*) from User").uniqueResult();

        session.close();

        return count.intValue();
    }

    public void addTwitchPointsToUser(User user) {
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

        if (twitchPointStorage != null) {
            user.getRanking().addPoints(twitchPointStorage.getPoints());
            user.getRanking().addXP(twitchPointStorage.getXp());

            session.delete(twitchPointStorage);
        }

        session.close();
    }

    public List<User> searchUsers(String username) {
        Session session = DatabaseManager.getSession();

        List<User> users = session.createQuery("select user.id as id, user.username as username from User user where lower(username) LIKE :username")
                .setString("username", "%" + username.toLowerCase() + "%")
                .setResultTransformer(Transformers.aliasToBean(User.class))
                .list();

        session.close();

        return users;
    }

    public boolean isUserAtLeast(User user, User.Role role) {
        User.Role userRole = user.getRole();

        return (userRole.ordinal() >= role.ordinal());
    }

    public User userForToken(String token) {
        Session session = DatabaseManager.getSession();

        User user = (User) session.createQuery("from User user where user.session.sessionID = :token")
                .setString("token", token)
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return user;
    }

    public void logoutUser(User user) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        UserSession userSession = (UserSession) session.createQuery("from UserSession session where session.id = :id")
                .setInteger("id", user.getId())
                .setMaxResults(1)
                .uniqueResult();

        session.delete(userSession);

        session.getTransaction().commit();
        session.close();
    }

    public boolean isResetKeyValidForUser(User user, String key) {
        return user.getResetKey().equals(key);
    }

    public void removeResetKeyFromUser(User user) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        user.setResetKey(null);
        session.merge(user);

        session.getTransaction().commit();
        session.close();
    }

    public void beginResetPasswordForEmail(String email) throws NonDevWarsUserException, UserNotFoundException {
        Session session = DatabaseManager.getSession();

        User user = this.userForEmail(email);

        if (user == null) {
            throw new UserNotFoundException("Email", email);
        }

        if (!user.isNative()) {
            session.close();

            throw new NonDevWarsUserException();
        }

        session.beginTransaction();
        user = (User) session.merge(user);

        String resetKey = Util.randomText(64);

        user.setResetKey(resetKey);

        Util.sendEmail(user.getUsername() + " : Password Reset", Reference.rootURL + "/passwordreset?user=" + user.getId() + "&key=" + resetKey, user.getEmail());

        session.getTransaction().commit();
        session.close();
    }

    public void changePasswordForUser(User user, String password) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        user.setPassword(security.hash(password));
        session.merge(user);

        session.getTransaction().commit();
        session.close();
    }

    public String pathForProfilePictureForUser(User user) {
        return fileStorage.shareableUrlForPath(fileStorage.PROFILE_PICTURE_PATH + "/" + user.getId() + "/avatar");
    }

    public void changeProfilePictureForUser(User user, InputStream inputStream) throws IOException, DbxException {
        fileStorage.uploadFile("/profilepics/" + user.getId() + "/avatar", inputStream);

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        user.setAvatarURL(pathForProfilePictureForUser(user));

        session.getTransaction().commit();
        session.close();
    }
}