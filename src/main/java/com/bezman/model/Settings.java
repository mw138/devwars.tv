package com.bezman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Terence on 4/30/2015.
 */
public class Settings extends BaseModel
{

    private int id;

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

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
