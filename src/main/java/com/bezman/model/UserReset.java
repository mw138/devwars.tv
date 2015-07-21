package com.bezman.model;

import com.bezman.exclusion.GsonExclude;

/**
 * Created by Terence on 4/17/2015.
 */
public class UserReset extends BaseModel
{

    private int id;

    @GsonExclude
    private User user;

    private String uid;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }
}
