package com.bezman.model;

/**
 * Created by Terence on 3/23/2015.
 */
public class Access extends BaseModel
{

    private int id;

    private User user;

    private String route;

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

    public String getRoute()
    {
        return route;
    }

    public void setRoute(String route)
    {
        this.route = route;
    }
}
