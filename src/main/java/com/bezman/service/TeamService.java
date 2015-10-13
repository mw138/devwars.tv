package com.bezman.service;

import com.bezman.model.CompletedObjective;
import com.bezman.model.Objective;
import com.bezman.model.Team;

import java.util.HashSet;

public class TeamService
{

    public static void addObjectiveToCompleted(Team team, Objective objective)
    {
        if (team.getCompletedObjectives() == null)
        {
            team.setCompletedObjectives(new HashSet<>());
        }

        team.getCompletedObjectives().add(new CompletedObjective(objective, team));
    }

}
