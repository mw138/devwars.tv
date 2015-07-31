package com.bezman.model;

import java.util.Date;

/**
 * Created by Terence on 4/28/2015.
 */
public class Contact extends BaseModel
{

    private int id;

    private String text, type, name, email;

    private Date timestamp;

    public Contact(){}

    public Contact(String name, String email, String type, String text)
    {
        this.setName(name);
        this.setEmail(email);
        this.setType(type);
        this.setText(text);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Date getTimestamp()
    {
        return timestamp == null ? new Date() : timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }
}
