package com.bezman.model;

/**
 * Created by Terence on 5/5/2015.
 */
public class ObjectiveItem extends BaseModel
{

    private int id;

    private String objectiveText;

    public ObjectiveItem(){}

    public ObjectiveItem(String objectiveText)
    {
        this.objectiveText = objectiveText;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getObjectiveText()
    {
        return objectiveText;
    }

    public void setObjectiveText(String objectiveText)
    {
        this.objectiveText = objectiveText;
    }
}
