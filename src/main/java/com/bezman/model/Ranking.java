package com.bezman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Terence on 1/26/2015.
 */
public class Ranking extends BaseModel
{

    private int id;
    private Double points;
    private Double xp;

    private int rank;

    @JsonIgnore
    private User user;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Double getPoints()
    {
        return points == null ? 0 : points;
    }

    public void setPoints(Double points)
    {
        this.points = points;
    }

    public Double getXp()
    {
        return xp == null ? 0 : xp;
    }

    public void setXp(Double xp)
    {
        this.xp = xp;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public void addPoints(int points)
    {
        this.setPoints(this.getPoints() + points);
    }

    public void addXP(int xp)
    {
        this.setXp(this.getXp() + xp);
    }
}
