package com.bezman.model;

/**
 * Created by Terence on 5/27/2015.
 */
public class ShopItem extends BaseModel
{

    public int id;

    public String name, description;

    public Integer price, requiredLevel;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getPrice()
    {
        return price;
    }

    public void setPrice(Integer price)
    {
        this.price = price;
    }

    public Integer getRequiredLevel()
    {
        return requiredLevel;
    }

    public void setRequiredLevel(Integer requiredLevel)
    {
        this.requiredLevel = requiredLevel;
    }
}
