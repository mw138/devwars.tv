package com.bezman.request.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class LegacyGame {
    private String name;

    private Timestamp timestamp;

    private String theme;

    private ArrayList<LegacyObjective> objectives;

    private HashMap<String, String> redTeam, blueTeam;

    private int designVotesBlue, designVotesRed, funcVotesBlue, funcVotesRed;

    private String youtubeURL;
}

