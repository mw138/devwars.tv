package com.bezman.model;

import com.bezman.exclusion.GsonExclude;

/**
 * Created by Terence on 4/30/2015.
 */
public class Settings extends BaseModel
{

    public int id;

    @GsonExclude
    public User user;

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
}
