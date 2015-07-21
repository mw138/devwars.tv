package com.bezman.model;

import java.util.Set;

/**
 * Created by Terence on 7/21/2015.
 */
public class UserTeam extends BaseModel
{

    private int id;

    private User owner;

    private String name;

    private Set<User> members;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public User getOwner()
    {
        return owner;
    }

    public void setOwner(User owner)
    {
        this.owner = owner;
    }

    public Set<User> getMembers()
    {
        return members;
    }

    public void setMembers(Set<User> members)
    {
        this.members = members;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
