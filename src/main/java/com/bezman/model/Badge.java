package com.bezman.model;

import com.bezman.exclusion.GsonExclude;

import java.util.Set;

/**
 * Created by Terence on 6/28/2015.
 */
public class Badge
{

    public int id;

    public String name, description;

    public Integer bitsAwarded, xpAwarded;

    @GsonExclude
    public Set<User> users;

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

    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers(Set<User> users)
    {
        this.users = users;
    }
}
