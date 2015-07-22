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

    private Set<User> invites;

    public UserTeam(){}

    public UserTeam(String name, User owner)
    {
        this.name = name;
        this.owner = owner;
    }

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

    public Set<User> getInvites()
    {
        return invites;
    }

    public void setInvites(Set<User> invites)
    {
        this.invites = invites;
    }


    public boolean inviteUser(User user)
    {
        for(User currentUser : this.getMembers())
        {
            if(currentUser.getId() == user.getId()) return false;
        }

        for(User currentUser : this.getInvites())
        {
            if(currentUser.getId() == user.getId()) return false;
        }

        this.getInvites().add(user);

        return true;
    }
}

