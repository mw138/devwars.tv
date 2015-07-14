package com.bezman.service;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.metamodel.relational.Database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;

/**
 * Created by Terence on 1/21/2015.
 */
public class UserService
{

    public static void initializeRequest(HttpServletRequest request, HttpServletResponse response)
    {


    }

    public static void addUser(User user)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        try
        {
            session.save(user);
            session.flush();
        }catch (Exception e)
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

    public static User userForUsernameDevWars(String username)
    {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User where username = :username and provider =  null");
        query.setString("username", username);

        User user = (User) DatabaseUtil.getFirstFromQuery(query);

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

        Query userQuery = session.createQuery("select u from User u left join u.connectedAccounts as a where (lower(substring(u.username, 1, length(u.username)-4)) = :username and u.provider = 'TWITCH') or (lower(a.username) = :username and a.provider = 'TWITCH')");
        userQuery.setString("username",username.toLowerCase());

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

}
