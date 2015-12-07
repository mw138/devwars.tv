package com.bezman.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TeamGameSignupUser {

    private int id;

    private User user;

    private String language;

    private TeamGameSignup teamGameSignup;

}
