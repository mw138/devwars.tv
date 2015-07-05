package com.bezman.model;

/**
 * Created by Terence on 4/29/2015.
 */
public class TwitchPointStorage extends BaseModel
{

    public int id;

    public Integer points, xp;

    public String username;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Integer getPoints()
    {
        return points;
    }

    public void setPoints(Integer points)
    {
        this.points = points;
    }

    public Integer getXp()
    {
        return xp;
    }

    public void setXp(Integer xp)
    {
        this.xp = xp;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
}
