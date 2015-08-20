package com.bezman.model;

import com.bezman.init.DatabaseManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Terence on 12/23/2014.
 */
public class Game extends BaseModel
{

    private int id;

    private Timestamp timestamp;
    private String name, theme, status;
    private boolean active = false, done = false;

    private String youtubeURL;

    private Map<String, Team> teams = new HashMap<>();

    private Set<Objective> objectives;

    @JsonIgnore
    private Set<GameSignup> signups;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    public Map<String, Team> getTeams()
    {
        return teams;
    }

    public void setTeams(Map<String, Team> teams)
    {
        this.teams = teams;
    }

    public Set<Objective> getObjectives()
    {
        return objectives;
    }

    public void setObjectives(Set<Objective> objectives)
    {
        this.objectives = objectives;
    }

    public boolean isDone()
    {
        return done;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public String getYoutubeURL()
    {
        return youtubeURL;
    }

    public void setYoutubeURL(String youtubeURL)
    {
        this.youtubeURL = youtubeURL;
    }

    public String getTheme()
    {
        return theme;
    }

    public void setTheme(String theme)
    {
        this.theme = theme;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @JsonIgnore
    public Set<GameSignup> getSignups() {
        return signups;
    }

    public void setSignups(Set<GameSignup> signups) {
        this.signups = signups;
    }

    public Team getTeamByID(int id)
    {
        for (Team team : this.getTeams().values())
        {
            if (team.getId() == id) return team;
        }

        return null;
    }

    public void deleteFullGame()
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        for(Team team : this.getTeams().values())
        {
            for(Player player : team.getPlayers())
            {
                session.delete(player);
            }

            session.delete(team);
        }

        session.delete(this);

        Query query = session.createQuery("delete from GameSignup as g where g.game.id = :id");
        query.setInteger("id", this.getId());

        query.executeUpdate();

        session.getTransaction().commit();
        session.close();
    }
}
