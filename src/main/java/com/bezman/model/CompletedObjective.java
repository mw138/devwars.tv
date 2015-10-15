package com.bezman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.crsh.term.TermEvent;

@Getter
@Setter
@NoArgsConstructor
public class CompletedObjective extends BaseModel
{
    private int id;

    @JsonIgnore
    private Team team;

    private Objective objective;

    public CompletedObjective(Objective objective, Team team)
    {
        this.objective = objective;
        this.team = team;
    }
}
