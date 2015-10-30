package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ranking extends BaseModel {

    private int id;

    @HibernateDefault("0")
    private Double points;

    @HibernateDefault("0")
    private Double xp;

    private int rank;

    @JsonIgnore
    private User user;

    public Ranking() {
        this.setXp((double) 0);
        this.setPoints((double) 0);
    }

    public void addPoints(int points) {
        this.setPoints(this.getPoints() + points);
    }

    public void addXP(int xp) {
        this.setXp(this.getXp() + xp);
    }
}
