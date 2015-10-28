package com.bezman.model;

import com.bezman.Reference.Reference;
import com.bezman.annotation.HibernateDefault;
import com.bezman.annotation.PreFlush;
import com.bezman.service.RankService;
import com.bezman.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mashape.unirest.http.Unirest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Ranking extends BaseModel
{

    private int id;

    @HibernateDefault("0")
    private Double points;

    @HibernateDefault("0")
    private Double xp;

    private int rank;

    @JsonIgnore
    private User user;

    public Ranking()
    {
        this.setXp((double) 0);
        this.setPoints((double) 0);
    }

    public void addPoints(int points)
    {
        this.setPoints(this.getPoints() + points);
    }

    public void addXP(int xp)
    {
        this.setXp(this.getXp() + xp);
    }
}
