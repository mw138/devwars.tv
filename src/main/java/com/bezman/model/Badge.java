package com.bezman.model;

import com.bezman.Reference.DatabaseManager;
import com.bezman.exclusion.GsonExclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Set;

/**
 * Created by Terence on 6/28/2015.
 */
public class Badge extends BaseModel
{

    private int id;

    private String name, description;

    private Integer bitsAwarded, xpAwarded, userCount;

    @JsonIgnore
    private Set<User> users;

    public Badge(){}

    public Badge(String name, int bitsAwarded, int xpAwarded, String description)
    {
        this.name = name;
        this.bitsAwarded = bitsAwarded;
        this.xpAwarded = xpAwarded;
        this.description = description;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getBitsAwarded()
    {
        return bitsAwarded == null ? 0 : bitsAwarded;
    }

    public void setBitsAwarded(int bitsAwarded)
    {
        this.bitsAwarded = bitsAwarded;
    }

    public int getXpAwarded()
    {
        return xpAwarded == null ? 0 : xpAwarded;
    }

    public void setXpAwarded(int xpAwarded)
    {
        this.xpAwarded = xpAwarded;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @JsonIgnore
    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers(Set<User> users)
    {
        this.users = users;
    }

    @JsonIgnore
    public void updateUsersCount()
    {
        Session session = DatabaseManager.getSession();

        this.userCount = this.getUsers().size();

        session.close();
    }

    public static Badge badgeForName(String name)
    {
        Session session = DatabaseManager.getSession();

        Badge badge = (Badge) session.createCriteria(Badge.class)
                .add(Restrictions.eq("name", name))
                .uniqueResult();

        session.close();

        return badge;
    }
}
