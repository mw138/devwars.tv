package com.bezman.model;

import com.bezman.exclusion.GsonExclude;

/**
 * Created by root on 4/25/15.
 */
public class ConnectedAccount extends BaseModel
{

    public int id;

    public String username, provider;

    @GsonExclude
    public User user;

    public Boolean disconnected;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Boolean getDisconnected()
    {
        return disconnected == null ? false : true;
    }

    public void setDisconnected(Boolean disconnected)
    {
        this.disconnected = disconnected;
    }
}
