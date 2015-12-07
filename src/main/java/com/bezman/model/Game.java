package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import com.bezman.init.DatabaseManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class Game extends BaseModel {

    private int id;

    @HibernateDefault
    private Timestamp timestamp;

    private String name, theme, status;

    private boolean active = false, done = false, teamGame;

    private String youtubeURL;

    private Map<String, Team> teams = new HashMap<>();

    private Set<Objective> objectives;

    @JsonIgnore
    private Set<GameSignup> signups;

    @HibernateDefault("2")
    private Integer season;

    @JsonIgnore
    private Tournament tournament;

    @HibernateDefault("false")
    private Boolean hasTournament;

    public Team getTeamByID(int id) {
        for (Team team : this.getTeams().values()) {
            if (team.getId() == id) return team;
        }

        return null;
    }

    public void deleteFullGame() {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        for (Team team : this.getTeams().values()) {
            for (Player player : team.getPlayers()) {
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
