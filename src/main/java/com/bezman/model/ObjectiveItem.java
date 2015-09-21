package com.bezman.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ObjectiveItem extends BaseModel
{
    private int id;

    private String objectiveText;

    public ObjectiveItem(String objectiveText)
    {
        this.objectiveText = objectiveText;
    }
}
