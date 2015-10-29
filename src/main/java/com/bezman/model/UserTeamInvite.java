package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UserTeamInvite {

    private int id;

    private User user;

    private UserTeam team;

    @HibernateDefault
    private Date createdAt;

    public UserTeamInvite(User user, UserTeam team) {
        this.user = user;
        this.team = team;
    }
}
