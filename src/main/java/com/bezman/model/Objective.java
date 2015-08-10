package com.bezman.model;

import com.bezman.exclusion.GsonExclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;

/**
 * Created by Terence on 4/15/2015.
 */
public class Objective extends BaseModel
{

    private int id;

    private Integer orderID;

    @JsonIgnore
    private Game game;

    private String objectiveText;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public String getObjectiveText()
    {
        return objectiveText;
    }

    public void setObjectiveText(String objectiveText)
    {
        this.objectiveText = objectiveText;
    }

    public Integer getOrderID()
    {
        return orderID == null ? 0 : orderID;
    }

    public void setOrderID(Integer orderID)
    {
        this.orderID = orderID;
    }
}
