package com.bezman.model;

import com.bezman.exclusion.GsonExclude;

/**
 * Created by Terence on 1/21/2015.
 */
public class UserSession extends BaseModel
{

    private int id;
    private String sessionID;

    @GsonExclude
    private User user;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getSessionID()
    {
        return sessionID;
    }

    public void setSessionID(String sessionID)
    {
        this.sessionID = sessionID;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
