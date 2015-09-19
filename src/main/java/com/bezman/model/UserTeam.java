package com.bezman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.List;
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

    @JsonIgnore
    private Set<Team> gameTeams;

    public UserTeam(){}

    public UserTeam(String name, User owner)
    {
        this.name = name;
        this.owner = owner;

        this.members = new HashSet<>();
        this.invites = new HashSet<>();

        this.members.add(owner);
        owner.setTeam(this);

        this.setId(owner.getId());
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

    @JsonIgnore
    public Set<Team> getGameTeams()
    {
        return gameTeams;
    }

    public void setGameTeams(Set<Team> gameTeams)
    {
        this.gameTeams = gameTeams;
    }
}

