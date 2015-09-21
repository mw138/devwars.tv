package com.bezman.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompletedObjective extends BaseModel
{
    private int id;

    private int team_id;

    private Objective objective;
}
