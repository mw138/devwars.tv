package com.bezman.model;

import com.bezman.exclusion.GsonExclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Terence Bezman on 12/27/2014.
 */
public class Team extends BaseModel
{
    public String name;

    public int id;

    public String embedLink;
    public String status;
    public String endGame;

    public boolean win = false;

    public String codeUrl, websiteUrl;

    @GsonExclude
    public Game game;

    public Set<Player> players;

    public Set<CompletedObjective> completedObjectives;

    public Integer designVotes, funcVotes, codeVotes;

    public String getEmbedLink()
    {
        return embedLink;
    }

    public void setEmbedLink(String embedLink)
    {
        this.embedLink = embedLink;
    }

    public String getEndGame()
    {
        return endGame;
    }

    public void setEndGame(String endGame)
    {
        this.endGame = endGame;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(Set<Player> players)
    {
        this.players = players;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public boolean isWin()
    {
        return win;
    }

    public void setWin(boolean win)
    {
        this.win = win;
    }

    public Set<CompletedObjective> getCompletedObjectives()
    {
        return completedObjectives;
    }

    public void setCompletedObjectives(Set<CompletedObjective> completedObjectives)
    {
        this.completedObjectives = completedObjectives;
    }

    public Integer getDesignVotes()
    {
        return designVotes == null ? 0 : designVotes;
    }

    public void setDesignVotes(Integer designVotes)
    {
        this.designVotes = designVotes;
    }

    public Integer getFuncVotes()
    {
        return funcVotes == null ? 0 : funcVotes;
    }

    public void setFuncVotes(Integer funcVotes)
    {
        this.funcVotes = funcVotes;
    }

    public Integer getCodeVotes()
    {
        return codeVotes == null ? 0 : codeVotes;
    }

    public void setCodeVotes(Integer codeVotes)
    {
        this.codeVotes = codeVotes;
    }

    public String getCodeUrl()
    {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl)
    {
        this.codeUrl = codeUrl;
    }

    public String getWebsiteUrl()
    {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl)
    {
        this.websiteUrl = websiteUrl;
    }

    public boolean didCompleteAllObjectives()
    {
        Set<Objective> gameObjectives = this.getGame().getObjectives();
        Set<CompletedObjective> completedObjectives = this.getCompletedObjectives();

        return gameObjectives.size() == completedObjectives.size();
    }
}
