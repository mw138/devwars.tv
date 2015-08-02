package com.bezman.model;

import com.bezman.exclusion.GsonExclude;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Terence on 4/27/2015.
 */
public class Activity extends BaseModel
{

    private int id;

    @JsonIgnore
    private User affectedUser, user;

    private String description;

    private Date timestamp;

    private Integer pointsChanged, xpChanged;

    public Activity(){}

    public Activity(User affectedUser, User user, String description, Timestamp timestamp, Integer pointsChanged, Integer xpChanged)
    {
        this.affectedUser = affectedUser;
        this.user = user;
        this.description = description;
        this.timestamp = timestamp;
        this.pointsChanged = pointsChanged;
        this.xpChanged = xpChanged;
    }

    public Activity(User affectedUser, User user, String description, Integer pointsChanged, Integer xpChanged)
    {
        this.affectedUser = affectedUser;
        this.user = user;
        this.description = description;
        this.pointsChanged = pointsChanged;
        this.xpChanged = xpChanged;
        this.timestamp = new Date();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public User getAffectedUser()
    {
        return affectedUser;
    }

    public void setAffectedUser(User affectedUser)
    {
        this.affectedUser = affectedUser;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
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
}
