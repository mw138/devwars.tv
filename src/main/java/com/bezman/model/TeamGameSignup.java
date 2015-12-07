package com.bezman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class TeamGameSignup extends BaseModel {
    private int id;

    private UserTeam userTeam;

    private Set<TeamGameSignupUser> teamGameSignupUsers;

    @JsonIgnore
    private Tournament tournament;

    public TeamGameSignup(Tournament tournament, UserTeam userTeam, TeamGameSignupUser[] users) {
        this.setTournament(tournament);
        this.setUserTeam(userTeam);
        this.setTeamGameSignupUsers(new HashSet<>(Arrays.asList(users)));
    }

    public TeamGameSignup(Tournament tournament, UserTeam userTeam) {
        this.setTournament(tournament);
        this.setUserTeam(userTeam);

        this.teamGameSignupUsers = new HashSet<>();
    }
}
