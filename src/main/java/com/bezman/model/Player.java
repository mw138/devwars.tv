package com.bezman.model;

import com.bezman.exclusion.GsonExclude;

/**
 * Created by Terence Bezman on 12/27/2014.
 */
public class Player extends BaseModel
{

    public int id;

    @GsonExclude
    public Team team;

    public User user;
    public String language;

    public Integer pointsChanged;

    public Integer xpChanged;

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public Team getTeam()
    {
        return team;
    }

    public void setTeam(Team team)
    {
        this.team = team;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Integer getPointsChanged()
    {
        return pointsChanged == null ? 0 : pointsChanged;
    }

    public void setPointsChanged(Integer pointsChanged)
    {
        this.pointsChanged = pointsChanged;
    }

    public Integer getXpChanged()
    {
        return xpChanged == null ? 0 : xpChanged;
    }

    public void setXpChanged(Integer xpChanged)
    {
        this.xpChanged = xpChanged;
    }

    public Player(){}

    public Player(Team team, User user, String language)
    {
        this.team = team;
        this.user = user;
        this.language = language;
    }
}
