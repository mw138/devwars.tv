package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Player extends BaseModel
{

    private int id;

    @JsonIgnore
    private Team team;

    private User user;

    private String language;

    @HibernateDefault("0")
    private Integer pointsChanged;

    @HibernateDefault("0")
    private Integer xpChanged;

    public Player(Team team, User user, String language)
    {
        this.team = team;
        this.user = user;
        this.language = language;
    }
}
