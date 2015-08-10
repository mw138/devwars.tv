package com.bezman.model;

import com.bezman.exclusion.GsonExclude;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Terence on 7/5/2015.
 */
public class Notification extends BaseModel
{

    private int id;

    @JsonIgnore
    private User user;

    private String notificationText;

    private boolean hasRead;

    public Notification(){}

    public Notification(User user, String notificationText, boolean hasRead)
    {
        this.user = user;
        this.notificationText = notificationText;
        this.hasRead = hasRead;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    @JsonIgnore
    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getNotificationText()
    {
        return notificationText;
    }

    public void setNotificationText(String notificationText)
    {
        this.notificationText = notificationText;
    }

    public boolean isHasRead()
    {
        return hasRead;
    }

    public void setHasRead(boolean hasRead)
    {
        this.hasRead = hasRead;
    }
}
