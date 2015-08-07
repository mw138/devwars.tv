package com.bezman.model;

/**
 * Created by Terence on 4/4/2015.
 */
public class GameSignup extends BaseModel
{

    private int id;

    private User user;
    private Game game;

    public GameSignup() {}

    public GameSignup(User user, Game game) {
        this.user = user;
        this.game = game;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }
}
