package com.bezman.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameSignup extends BaseModel {

    private int id;

    private User user;

    private Game game;

    public GameSignup(User user, Game game) {
        this.user = user;
        this.game = game;
    }
}
