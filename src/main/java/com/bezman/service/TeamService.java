package com.bezman.service;

import com.bezman.model.CompletedObjective;
import com.bezman.model.Objective;
import com.bezman.model.Team;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class TeamService
{

    public void addObjectiveToCompleted(Team team, Objective objective)
    {
        if (team.getCompletedObjectives() == null)
        {
            team.setCompletedObjectives(new HashSet<>());
        }

        team.getCompletedObjectives().add(new CompletedObjective(objective, team));
    }

}
