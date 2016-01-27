package com.bezman.service;

import com.bezman.init.DatabaseManager;
import com.bezman.model.*;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TournamentService {

    @Autowired
    UserTeamService userTeamService;

    public Tournament byID(int id) {
        Tournament tournament;
        Session session = DatabaseManager.getSession();

        tournament = (Tournament) session.get(Tournament.class, id);

        session.close();

        return tournament;
    }

    public List<Tournament> upcomingTournaments() {
        List<Tournament> tournamentList;

        Session session = DatabaseManager.getSession();

        tournamentList = session.createCriteria(Tournament.class)
                .add(Restrictions.ge("end", new Date()))
                .list();

        session.close();

        return tournamentList;
    }


    public void signupTeamForTournament(UserTeam userTeam, Tournament tournament, TeamGameSignupUser[] users)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        TeamGameSignup teamGameSignup = new TeamGameSignup(tournament, userTeam);

        for(TeamGameSignupUser user : users) {
            teamGameSignup.getTeamGameSignupUsers().add(user);
            session.save(user);
        }

        session.save(teamGameSignup);

        tournament.getTeamSignups().add(teamGameSignup);

        session.getTransaction().commit();
        session.close();
    }


    public Tournament tournamentFromGame(Game game)
    {
        Tournament tournament;

        Session session = DatabaseManager.getSession();

        game = (Game) session.merge(game);

        if (!game.getHasTournament()) return null;

        tournament = this.byID(game.getTournament().getId());

        session.close();

        return tournament;
    }
}
