package com.bezman.controller;

import com.bezman.annotation.AuthedUser;
import com.bezman.annotation.JSONParam;
import com.bezman.annotation.PathModel;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.Game;
import com.bezman.model.TeamGameSignupUser;
import com.bezman.model.Tournament;
import com.bezman.model.User;
import com.bezman.service.GameService;
import com.bezman.service.TournamentService;
import com.bezman.service.UserTeamService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/v1/tournament")
@Controller
public class TournamentController {

    @Autowired
    TournamentService tournamentService;

    @Autowired
    UserTeamService userTeamService;

    @Autowired
    GameService gameService;

    @RequestMapping("/{id}")
    public ResponseEntity getTournament(@PathVariable("id") int id) {
        Tournament tournament = tournamentService.byID(id);

        if (tournament != null) {
            return new ResponseEntity(tournament, HttpStatus.OK);
        } else return new ResponseEntity("Tornament Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/upcoming")
    public ResponseEntity upcomingTournaments() {
        return new ResponseEntity(tournamentService.upcomingTournaments(), HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping(value = "/applyteamforgame", method = RequestMethod.POST)
    public ResponseEntity signupTeamForTournamentFromGame(@AuthedUser User user,
                                                          @RequestParam("game") int id,
                                                          @JSONParam("users") TeamGameSignupUser[] users) {
        Game game = gameService.getGame(id);

        if (!game.getHasTournament())
            return new ResponseEntity("Game does not belong to tournament", HttpStatus.OK);

        return applyTeamForTournament(user, game.getTournament().getId(), users);
    }

    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping(value = "/{id}/applyteam", method = RequestMethod.POST)
    public ResponseEntity applyTeamForTournament(@AuthedUser User user,
                                                  @PathVariable("id") int id,
                                                  @JSONParam("users") TeamGameSignupUser[] users) {
        if (user.getOwnedTeam() == null)
            return new ResponseEntity("You do not own a team", HttpStatus.FORBIDDEN);

        if (!userTeamService.doesUserHaveAuthorization(user, user.getOwnedTeam()))
            return new ResponseEntity("You are not allowed to do that", HttpStatus.FORBIDDEN);

        if (users.length != 3)
            return new ResponseEntity("You must only sign up three players", HttpStatus.BAD_REQUEST);

        Tournament tournament = tournamentService.byID(id);

        if (tournament.getTeamSignups().stream().anyMatch(team -> team.getUserTeam().getId() == user.getTeam().getId()))
            return new ResponseEntity("You have already signed up for this tournament", HttpStatus.CONFLICT);

        tournamentService.signupTeamForTournament(user.getOwnedTeam(), tournament, users);

        return new ResponseEntity("Successfully signed up team", HttpStatus.OK);
    }

    @RequestMapping("/bygame")
    public ResponseEntity getTournamentFromGame(@RequestParam("game") int gameID) {
        Game game = gameService.getGame(gameID);

        Tournament tournament = tournamentService.tournamentFromGame(game);

        if (tournament != null) {
            return new ResponseEntity(tournament, HttpStatus.OK);
        } else return new ResponseEntity("Tournament not found for game", HttpStatus.NOT_FOUND);
    }
}
