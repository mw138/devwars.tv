package com.bezman.service;

import com.bezman.init.DatabaseManager;
import com.bezman.model.*;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class TeamService {

    public void addObjectiveToCompleted(Team team, Objective objective) {
        if (team.getCompletedObjectives() == null) {
            team.setCompletedObjectives(new HashSet<>());
        }

        team.getCompletedObjectives().add(new CompletedObjective(objective, team));
    }

    public UserTeam userTeamForTeam(Team team)
    {
        if (team.getPlayers().size() < 1) return null;

        Session session = DatabaseManager.getSession();

        Player somePlayer = (Player) team.getPlayers().toArray()[0];

        somePlayer = (Player) session.merge(somePlayer);

        UserTeam userTeam = somePlayer.getUser().getTeam();

        final UserTeam finalUserTeam = userTeam;

        boolean notOnSameTeam = team.getPlayers().stream().anyMatch(player -> player.getUser().getTeam().getId() != finalUserTeam.getId());

        if (notOnSameTeam)
            userTeam = null;

        session.close();

        return userTeam;
    }
}
