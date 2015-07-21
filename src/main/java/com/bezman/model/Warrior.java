package com.bezman.model;

import com.bezman.exclusion.GsonExclude;

import java.util.Date;

/**
 * Created by Terence on 5/27/2015.
 */
public class Warrior extends BaseModel
{

    private int id;

    private String firstName, favFood, favTool, about, c9Name, company, location;

    private Integer htmlRate, cssRate, jsRate;

    private Date dob;

    @GsonExclude
    private User user;

    public Warrior(){}

    public Warrior(String firstName, String favFood, String favTool, String about, String c9Name, String company, String location, Integer htmlRate, Integer cssRate, Integer jsRate, Date dob, int id)
    {
        this.firstName = firstName;
        this.favFood = favFood;
        this.favTool = favTool;
        this.about = about;
        this.c9Name = c9Name;
        this.company = company;
        this.location = location;
        this.htmlRate = htmlRate;
        this.cssRate = cssRate;
        this.jsRate = jsRate;
        this.dob = dob;
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public Date getDob()
    {
        return dob;
    }

    public void setDob(Date dob)
    {
        this.dob = dob;
    }

    public String getFavFood()
    {
        return favFood;
    }

    public void setFavFood(String favFood)
    {
        this.favFood = favFood;
    }

    public String getFavTool()
    {
        return favTool;
    }

    public void setFavTool(String favTool)
    {
        this.favTool = favTool;
    }

    public String getAbout()
    {
        return about;
    }

    public void setAbout(String about)
    {
        this.about = about;
    }

    public String getC9Name()
    {
        return c9Name;
    }

    public void setC9Name(String c9Name)
    {
        this.c9Name = c9Name;
    }

    public Integer getHtmlRate()
    {
        return htmlRate;
    }

    public void setHtmlRate(Integer htmlRate)
    {
        this.htmlRate = htmlRate;
    }

    public Integer getCssRate()
    {
        return cssRate;
    }

    public void setCssRate(Integer cssRate)
    {
        this.cssRate = cssRate;
    }

    public Integer getJsRate()
    {
        return jsRate;
    }

    public void setJsRate(Integer jsRate)
    {
        this.jsRate = jsRate;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
}
