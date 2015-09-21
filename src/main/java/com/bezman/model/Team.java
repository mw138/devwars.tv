package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class Team extends BaseModel
{
    private String name;

    private int id;

    private String embedLink;
    private String status;
    private String endGame;

    private boolean win = false;

    private String codeUrl, websiteUrl;

    @JsonIgnore
    private Game game;

    private UserTeam userTeam;

    private Set<Player> players;

    private Set<CompletedObjective> completedObjectives;

    @HibernateDefault("0")
    private Integer designVotes, funcVotes, codeVotes;

    public boolean didCompleteAllObjectives()
    {
        Set<Objective> gameObjectives = this.getGame().getObjectives();
        Set<CompletedObjective> completedObjectives = this.getCompletedObjectives();

        return gameObjectives.size() == completedObjectives.size() && gameObjectives.size() > 0;
    }
}
