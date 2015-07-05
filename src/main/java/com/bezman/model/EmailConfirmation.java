package com.bezman.model;

/**
 * Created by Terence on 1/26/2015.
 */
public class EmailConfirmation extends BaseModel
{

    public int id;
    public String uid;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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
