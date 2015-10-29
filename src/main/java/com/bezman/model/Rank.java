package com.bezman.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Rank extends BaseModel {
    private int id;

    private int level, rankLevel;

    private String rank, levelName;

    private int xpRequired;
}
