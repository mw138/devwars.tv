package com.bezman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

/**
 * Created by teren on 9/18/2015.
 */
public class TeamGameSignup extends BaseModel
{
    private int id;

    private UserTeam userTeam;

    @JsonIgnore
    private Game game;

    private Set<User> users;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public UserTeam getUserTeam()
    {
        return userTeam;
    }

    public void setUserTeam(UserTeam userTeam)
    {
        this.userTeam = userTeam;
    }

    @JsonIgnore
    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
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
