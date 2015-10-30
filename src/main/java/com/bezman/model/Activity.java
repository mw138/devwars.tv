package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Terence on 4/27/2015.
 */
@NoArgsConstructor
@Getter
@Setter
public class Activity extends BaseModel {

    private int id;

    @JsonIgnore
    private User affectedUser, user;

    private String description;

    private Date timestamp;

    @HibernateDefault("0")
    private Integer pointsChanged, xpChanged;

    public Activity(User affectedUser, User user, String description, Timestamp timestamp, Integer pointsChanged, Integer xpChanged) {
        this.affectedUser = affectedUser;
        this.user = user;
        this.description = description;
        this.timestamp = timestamp;
        this.pointsChanged = pointsChanged;
        this.xpChanged = xpChanged;
    }

    public Activity(User affectedUser, User user, String description, Integer pointsChanged, Integer xpChanged) {
        this.affectedUser = affectedUser;
        this.user = user;
        this.description = description;
        this.pointsChanged = pointsChanged;
        this.xpChanged = xpChanged;
        this.timestamp = new Date();
    }
}
