package com.bezman.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TwitchPointStorage extends BaseModel {
    private int id;

    private Integer points, xp;

    private String username;
}
