package com.bezman.controller.game;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.AuthedUser;
import com.bezman.annotation.JSONParam;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
import com.bezman.model.Activity;
import com.bezman.model.Player;
import com.bezman.model.Team;
import com.bezman.model.User;
import org.hibernate.internal.SessionImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;

@RequestMapping("/v1/player")
@Controller
public class PlayerController {

    /**
     * Removes a player from a team without penalizing
     * @param playerID ID of player to remove
     * @return The player which was removed
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{playerID}/remove")
    public ResponseEntity removePlayer(SessionImpl session, @PathVariable("playerID") int playerID)
    {
        Player player = (Player) session.get(Player.class, playerID);

        if (player != null) {
            session.delete(player);

            return new ResponseEntity("Successfully deleted player", HttpStatus.OK);
        }

        return new ResponseEntity("Player could not be found", HttpStatus.NOT_FOUND);
    }

    /**
     * Adds a player to a team
     * @param teamID ID of team to add to
     * @param language The language that the user will be playing
     * @param newUser The user that will be playing
     * @return The newly added player
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity addPlayer(@RequestParam("teamID") int teamID,
                                    @RequestParam("language") String language,
                                    @JSONParam("user") User newUser,
                                    @AuthedUser User user,
                                    SessionImpl session)
    {
        Team team = (Team) session.get(Team.class, teamID);

        if (team != null)
        {
            Player newPlayer = new Player(team, newUser, language);

            session.save(newPlayer);

            String message = "You were added to the game on " + new SimpleDateFormat("EEE, MMM d @ K:mm a").format(new Date(team.getGame().getTimestamp().getTime()));
            Activity activity = new Activity(newPlayer.getUser(), user, message, 0, 0);

            session.save(activity);

            if (newPlayer.getUser().getEmail() != null)
            {
                String subject = "Accepted to play a game of DevWars";
                String activityMessage = "Dear " + newPlayer.getUser().getUsername() + ", you've been accepted to play a game of DevWars on " + new Date(team.getGame().getTimestamp().getTime()).toString();

                Util.sendEmail(Reference.getEnvironmentProperty("emailUsername"), Reference.getEnvironmentProperty("emailPassword"), subject, activityMessage, newPlayer.getUser().getEmail());
            }

            return new ResponseEntity(newPlayer, HttpStatus.OK);
        }

        return null;
    }

}
