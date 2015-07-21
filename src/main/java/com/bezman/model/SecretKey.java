package com.bezman.model;

/**
 * Created by Terence on 4/23/2015.
 */
public class SecretKey extends BaseModel
{

    private int id;
    private String uid;

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
