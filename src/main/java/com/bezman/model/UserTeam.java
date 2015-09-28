package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.bezman.init.DatabaseManager;
import com.bezman.service.UserTeamService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Session;

import javax.persistence.PostLoad;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserTeam extends BaseModel
{

    private int id;

    private User owner;

    private String name;

    private String tag;

    private Set<User> members;

    private Set<User> invites;

    @JsonIgnore
    private Set<Team> gameTeams;

    @HibernateDefault("0")
    private Integer xp;

    private Long gamesWon, gamesLost;

    public UserTeam(String name, String tag, User owner)
    {
        this.name = name;
        this.tag = tag;
        this.owner = owner;

        this.members = new HashSet<>();
        this.invites = new HashSet<>();

        this.members.add(owner);
    }

    public void addXP(int xp)
    {
        this.setXp(this.getXp() + xp);
    }
}

