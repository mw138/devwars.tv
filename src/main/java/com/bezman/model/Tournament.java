package com.bezman.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class Tournament {

    private int id;

    private Set<Game> games;

    private Date start, end;

    private Set<TeamGameSignup> teamSignups;
}
