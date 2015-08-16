package com.bezman.controller;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.HttpMessages;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.*;
import com.bezman.model.*;
import com.bezman.service.GameService;
import com.bezman.service.PlayerService;
import com.bezman.service.Security;
import com.bezman.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.metamodel.relational.Database;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Ref;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Terence on 1/21/2015.
 */
@Controller
@RequestMapping(value = "/v1/game")
public class GameController
{
    /**
     * Retrieved all games with criteria
     * @param request
     * @param response
     * @param count Number of games wanted
     * @param offset How many games to skip in DB
     * @return
     */
    @RequestMapping(value = "/")
    public ResponseEntity allGames(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "count", defaultValue = "5", required = false) int count, @RequestParam(value = "offset", defaultValue = "0", required = false) int offset)
    {
        count = count > 50 ? 50 : count;

        return new ResponseEntity(GameService.allGames(count, offset), HttpStatus.OK);
    }

    @UnitOfWork
    /**
     * Retrieves upcoming DevWars games
     * @param request
     * @param response
     * @return
     */
    @AllowCrossOrigin(from = "*")
    @RequestMapping("/upcoming")
    public ResponseEntity upcomingGames(SessionImpl session)
    {
        Query query = session.createQuery("from Game where timestamp > :time and done = false order by timestamp asc");
        query.setTimestamp("time", new Date());

        List<Game> upcomingGames = query.list();

        if (upcomingGames != null || (upcomingGames != null && upcomingGames.size() > 0))
        {
            return new ResponseEntity(upcomingGames, HttpStatus.OK);
        } else
        {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @UnitOfWork
    /**
     * Retrieves games that have been done in the past
     * @param count
     * @param offset
     * @return
     */
    @RequestMapping("/pastgames")
    public ResponseEntity pastGames(SessionImpl session, @RequestParam(value = "count", required = false, defaultValue = "8") int count, @RequestParam(value = "offset", required = false, defaultValue = "0") int offset)
    {
        count = count > 8 ? 8 : count;

        Query query = session.createQuery("from Game where done = true order by id desc");
        query.setFirstResult(offset);
        query.setMaxResults(count);

        List<Game> pastGames = query.list();

        if (pastGames != null || (pastGames != null && pastGames.size() > 0))
        {
            return new ResponseEntity(pastGames, HttpStatus.OK);
        } else
        {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    /**
     * Creates a game with the default information
     * @param request
     * @param response
     * @param timestamp The time in UTC which the game will start
     * @param name Name of the game (Classic or Zen Garden)
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/create")
    public ResponseEntity createGame(SessionImpl session, @RequestParam(value = "time", required = false, defaultValue = "0") long timestamp, @RequestParam(required = false, value = "name") String name)
    {
        Game game = GameService.defaultGame();

        if(timestamp != 0)
        {
            game.setTimestamp(new Timestamp(timestamp));
        }

        if (name != null)
        {
            game.setName(name);
        }

        session.save(game);

        return new ResponseEntity(game.toString(), HttpStatus.OK);
    }

    /**
     * Retrieves a game with a given ind
     * @param request
     * @param response
     * @param id ID of the game to get
     * @return
     */
    @RequestMapping("/{id}")
    public ResponseEntity getGame(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id)
    {
        Game game = GameService.getGame(id);

        if (game != null)
        {
            return new ResponseEntity(game, HttpStatus.OK);
        } else
        {
            return new ResponseEntity("Could not find game for given ID", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Edits a game with new information
     * @param request
     * @param response
     * @param id The ID of the game to update
     * @param json JSON to update the game with
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/update", method =  RequestMethod.POST)
    public ResponseEntity editGame(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id, @RequestParam(value = "json", required = false) String json) throws JsonProcessingException, UnirestException {
        Session session = DatabaseManager.getSession();

        Game oldGame = GameService.getGame(id);
        boolean gameAlreadyExists = oldGame != null;

        if (!gameAlreadyExists)
        {
            return new ResponseEntity("Game for given id doesn't exist", HttpStatus.NOT_FOUND);
        } else
        {
            Game game = Reference.gson.fromJson(json, Game.class);

            session = DatabaseManager.getSession();
            session.beginTransaction();

            session.saveOrUpdate(game);

            session.getTransaction().commit();
            session.beginTransaction();

            session.createQuery("delete from CompletedObjective where team_id = null").executeUpdate();
            session.createQuery("delete from Objective where game = null").executeUpdate();

            session.getTransaction().commit();

            session.flush();

            session.close();

            if(game.isActive())
            {
                Unirest.patch("https://devwars-tv.firebaseio.com/frame/game/.json")
                        .queryString("auth", Security.firebaseToken)
                        .body(Reference.objectMapper.writeValueAsString(game))
                        .asString()
                        .getBody();
            }

            return getGame(request, response, id);
        }
    }

    @Transactional
    /**
     * Sets the game to active and all other games to inactive (Allows the currentgame method to bring back result)
     * @param request
     * @param response
     * @param id ID of game to activate
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/activate")
    public ResponseEntity activateGame(SessionImpl session, @PathVariable("id") int id)
    {
        Game game = (Game) session.get(Game.class, id);

        if (game != null)
        {
            Query query = session.createQuery("update Game set active = false where active = true");
            query.executeUpdate();

            game.setActive(true);

            return new ResponseEntity(game, HttpStatus.OK);
        } else
        {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    /**
     * Sets the game to inactive
     * @param request
     * @param response
     * @param id ID of game to deactivate
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/deactivate")
    public ResponseEntity deactivateGame(SessionImpl session, @PathVariable("id") int id)
    {
        Game game = (Game) session.get(Game.class, id);

        if (game != null)
        {
            game.setActive(false);

            return new ResponseEntity(game, HttpStatus.OK);
        } else
        {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Ends the game and wards winning team
     * @param request
     * @param response
     * @param gameID ID of game to end
     * @param winnerID The ID of the winning team (Awards corresponding values)
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/endgame")
    public ResponseEntity endGame(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable("id") int gameID,
                                  @RequestParam("winner") int winnerID)
    {
        ResponseEntity responseEntity = null;

        Game game = GameService.getGame(gameID);

        if (game != null)
        {
            Team team = game.getTeamByID(winnerID);

            if (team != null)
            {
                game.setDone(true);
                game.setActive(false);

                for(Team otherTeam : game.getTeams().values())
                {
                    otherTeam.setWin(false);
                }

                team.setWin(true);

                DatabaseUtil.saveOrUpdateObjects(true, game, team);

                responseEntity = new ResponseEntity("Successfully ended game", HttpStatus.OK);
            } else
            {
                responseEntity = new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
            }
        } else
        {
            responseEntity = new ResponseEntity("Game not found", HttpStatus.NOT_FOUND);
        }

        game = GameService.getGame(gameID);

        for (Team team : game.getTeams().values())
        {
            //Participation
            team.getPlayers().stream().forEach(p -> {
                p.setXpChanged(p.getXpChanged() + 50);
                p.setPointsChanged(p.getPointsChanged() + 222);

                System.out.println("Current User : " + p.getUser().getId() + " : " + p.getUser().getUsername());

                p.getUser().getRanking().addXP(50);
                p.getUser().getRanking().addPoints(222);
            });

            //Completed All Objectives
            if (team.didCompleteAllObjectives())
            {
                team.getPlayers().stream().forEach(p -> {
                    p.setXpChanged(p.getXpChanged() + 150);

                    p.getUser().getRanking().addXP(150);
                });
            }

            //Team won
            if (team.isWin())
            {
                System.out.println(team.getName() + " has won");
                team.getPlayers().stream().forEach(p -> {
                    p.setXpChanged(p.getXpChanged() + 250);
                    p.setPointsChanged(p.getPointsChanged() + 444);

                    p.getUser().getRanking().addXP(250);
                    p.getUser().getRanking().addPoints(444);
                });
            }

            Session session = DatabaseManager.getSession();
            session.beginTransaction();

            for (Player player : team.getPlayers())
            {
                session.saveOrUpdate(player);
                session.saveOrUpdate(player.getUser().getRanking());
            }

            session.getTransaction().commit();
            session.close();
        }

        return responseEntity;
    }

    @Transactional
    @RequestMapping("/{id}/resetwinner")
    public ResponseEntity resetGameWinner(@PathVariable("id") int id, SessionImpl session)
    {
        Game game = (Game) session.get(Game.class, id);

        game.getTeams().values().stream()
                .forEach(team -> {
                    team.setWin(false);

                    team.getPlayers().stream().forEach(player -> {
                        int xpChanged = player.getXpChanged();
                        int pointsChanged = player.getPointsChanged();

                        player.getUser().getRanking().addPoints(pointsChanged * -1);
                        player.getUser().getRanking().addXP(xpChanged * -1);

                        player.setXpChanged(0);
                        player.setPointsChanged(0);
                    });
                });

        return new ResponseEntity("Successfully reset game winner", HttpStatus.OK);
    }

    /**
     * Deletes a game
     * @param request
     * @param response
     * @param id ID of game to delete
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/delete")
    public ResponseEntity deleteGame(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id)
    {
        ResponseEntity responseEntity = null;

        Game game = GameService.getGame(id);

        if (game != null)
        {
            game.deleteFullGame();
            responseEntity = new ResponseEntity("Game " + id + " was deleted", HttpStatus.OK);
        } else responseEntity = new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);


        return responseEntity;
    }

    /**
     * Gets the current active game
     * @param request
     * @param response
     * @return
     */
    @AllowCrossOrigin(from = "*")
    @RequestMapping(value = "/currentgame")
    public ResponseEntity currentGame(HttpServletRequest request, HttpServletResponse response)
    {
        Game currentGame = GameService.currentGame();

        if (currentGame != null)
        {
            return new ResponseEntity(currentGame, HttpStatus.OK);
        } else
        {
            return new ResponseEntity(HttpMessages.NO_CURRENT_GAME, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets the most recently created game
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/latestgame")
    public ResponseEntity latestGame(HttpServletRequest request, HttpServletResponse response)
    {
        Game currentGame = GameService.latestGame();

        if (currentGame != null)
        {
            return new ResponseEntity(currentGame.toString(), HttpStatus.OK);
        } else
        {
            return new ResponseEntity(HttpMessages.NO_GAMES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets the game which is nearest to the current date in the future
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/nearestgame")
    public ResponseEntity nearestGame(HttpServletRequest request, HttpServletResponse response)
    {
        Game nearestGame = GameService.nearestGame();

        if (nearestGame != null)
        {
            return new ResponseEntity(nearestGame, HttpStatus.OK);
        } else
        {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @UnitOfWork
    /**
     * Gets all signed up users for given game
     * @param request
     * @param response
     * @param id ID of game to get sign ups for
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/pendingplayers")
    public ResponseEntity pendingPlayers(SessionImpl session, @PathVariable("id") int id)
    {
        Query query = session.createQuery("select user from GameSignup g where g.game.id = :id");
        query.setParameter("id", id);

        List<User> users = query.list();

        return new ResponseEntity(users, HttpStatus.OK);
    }

    //Player Actions
    @Transactional
    /**
     * Removes a player from a team without penalizing
     * @param request
     * @param response
     * @param gameID ID of game to remove from (Redundant)
     * @param playerID ID of player to remove
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/player/{playerID}/remove")
    public ResponseEntity removePlayer(SessionImpl session, @PathVariable("id") int gameID, @PathVariable("playerID") int playerID)
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
     * @param request
     * @param gameID ID of game to add to
     * @param teamID ID of team to add to
     * @param language The language that the user will be playing
     * @param newUser The user that will be playing
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/team/{teamID}/add", method = RequestMethod.POST)
    public ResponseEntity addPlayer(HttpServletRequest request,
                                    @PathVariable("id") int gameID,
                                    @PathVariable("teamID") int teamID,
                                    @RequestParam("language") String language,
                                    @JSONParam("user") User newUser)
    {
        Game game = GameService.getGame(gameID);
        Team teamFound = null;

        if (game != null)
        {
            for (Team team : game.getTeams().values())
            {
                if (team.getId() == teamID)
                {
                    teamFound = team;

                    for (Player player : team.getPlayers())
                    {
                        if (player.getUser().getId() == newUser.getId())
                        {
                            return new ResponseEntity("User is already apart of this team", HttpStatus.CONFLICT);
                        }
                    }
                }
            }

            if (teamFound != null)
            {
                Player newPlayer = new Player(teamFound, newUser, language);

                Session session = DatabaseManager.getSession();
                session.beginTransaction();

                session.save(newPlayer);

                session.getTransaction().commit();
                session.beginTransaction();

                session.refresh(newPlayer);

                String message = "You were added to the game on " + new SimpleDateFormat("EEE, MMM d @ K:mm a").format(new Date(game.getTimestamp().getTime()));
                Activity activity = new Activity(newPlayer.getUser(), (User) request.getAttribute("user"), message, 0, 0);

                session.save(activity);

                session.getTransaction().commit();

                session.refresh(newPlayer);

                if (newPlayer.getUser().getEmail() != null)
                {
                    String subject = "Accepted to play a game of DevWars";
                    String activityMessage = "Dear " + newPlayer.getUser().getUsername() + ", you've been accepted to play a game of DevWars on " + new Date(game.getTimestamp().getTime()).toString();
                    Util.sendEmail(Security.emailUsername, Security.emailPassword, subject, activityMessage, newPlayer.getUser().getEmail());
                }

                session.close();

                return new ResponseEntity(newPlayer, HttpStatus.OK);
            }
        } else
        {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }

        return null;
    }

    /**
     * Removes a user from the game and penlizes them with some value
     * @param session
     * @param player The player to remove
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/forfeituser", method = RequestMethod.POST)
    public ResponseEntity forfeitUser(SessionImpl session,
                                      @JSONParam("player") Player player)
    {
        player = (Player) session.get(Player.class, player.getId());

        Ranking ranking = player.getUser().getRanking();
        ranking.addPoints(-50);
        ranking.addXP(-50);

        session.delete(player);

        return new ResponseEntity(player, HttpStatus.OK);
    }

    @Transactional
    /**
     * Signs a user up for a game
     * @param request
     * @param response
     * @param id ID of game to sign up for
     * @return
     */
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/{id}/signup")
    public ResponseEntity signupForGame(SessionImpl session, @AuthedUser User user,  @PathVariable("id") int id)
    {
        Game game = (Game) session.get(Game.class, id);

        if (game != null)
        {
            boolean hasSignedUp = game.getSignups().stream()
                    .anyMatch(signup -> signup.getUser().getId() == user.getId());

            if (!hasSignedUp)
            {

                GameSignup gameSignup = new GameSignup(user, game);

                Activity activity = new Activity(user, user, "Signed up for game on " + new SimpleDateFormat("EEE, MMM d @ K:mm a").format(new Date(game.getTimestamp().getTime())), 0, 0);

                session.save(gameSignup);
                session.save(activity);

                return new ResponseEntity("Signed up user", HttpStatus.OK);
            } else {
                return new ResponseEntity("You have already signed up for that game", HttpStatus.CONFLICT);
            }
        }

        return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
    }

    @Transactional
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/{id}/resign")
    public ResponseEntity resignFromGame(SessionImpl session, @AuthedUser User user, @PathVariable("id") int id)
    {
        Game game = (Game) session.get(Game.class, id);

        Optional<GameSignup> foundSignup = game.getSignups().stream()
                .filter(a -> a.getUser().getId() == user.getId())
                .findFirst();

        if (foundSignup.isPresent())
        {
            session.delete(foundSignup.get());

            return new ResponseEntity("Successfully resigned", HttpStatus.OK);
        }

        return new ResponseEntity("Sign Up not found", HttpStatus.NOT_FOUND);
    }

    /**
     * Adds votes to a team for voting part of game
     * @param request
     * @param response
     * @param id ID of game
     * @param teamID ID of team
     * @param func How many to add on the functionality vote
     * @param design How many to add on the design vote
     * @param code How many to add on the code vote
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/team/{teamID}/addvotes")
    public ResponseEntity addVotes(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable("id") int id,
                                   @PathVariable("teamID") int teamID,
                                   @RequestParam(value = "func", required = false, defaultValue = "0") int func,
                                   @RequestParam(value = "design", required = false, defaultValue = "0") int design,
                                   @RequestParam(value = "code", required = false, defaultValue = "0") int code)
    {
        ResponseEntity responseEntity = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from Team where id = :id");
        query.setInteger("id", teamID);

        Team team = (Team) DatabaseUtil.getFirstFromQuery(query);

        if (team != null)
        {
            session.beginTransaction();

            team.setDesignVotes(team.getDesignVotes() + design);
            team.setFuncVotes(team.getFuncVotes() + func);
            team.setCodeVotes(team.getCodeVotes() + code);

            session.persist(team);
            session.getTransaction().commit();

            responseEntity = new ResponseEntity(team, HttpStatus.OK);
        } else
        {
            responseEntity = new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
        }

        session.close();
        return responseEntity;
    }

    /**
     * Adds points to every player in a team
     * @param request
     * @param response
     * @param id ID of game
     * @param teamID ID of team
     * @param points How many points to add to each player
     * @param xp How much xp to add to each player
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/team/{teamID}/addpoints")
    public ResponseEntity addPointsToTeam(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable("id") int id,
                                          @PathVariable("teamID") int teamID,
                                          @RequestParam(value = "points", required = false, defaultValue = "0") int points,
                                          @RequestParam(value = "xp", required = false, defaultValue = "0") int xp)
    {
        ResponseEntity responseEntity = null;

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        Query query = session.createQuery("from Team where id = :id");
        query.setInteger("id", teamID);

        Team team = (Team) DatabaseUtil.getFirstFromQuery(query);

        if (team != null)
        {
            for (Player player : team.getPlayers())
            {
                if (player.getUser().getRanking() == null)
                {
                    Ranking ranking = new Ranking();
                    ranking.setId(player.getUser().getId());

                    player.getUser().setRanking(ranking);
                }

                player.setPointsChanged(player.getPointsChanged() + points);
                player.setXpChanged(player.getXpChanged() + xp);
                player.getUser().getRanking().addPoints(points);
                player.getUser().getRanking().addXP(xp);

                session.saveOrUpdate(player);

                Activity activity = new Activity(player.getUser(), (User) request.getAttribute("user"), "You received points/xp", points, xp);
                session.save(activity);
            }

            session.getTransaction().commit();
            session.refresh(team);

            responseEntity = new ResponseEntity(team.getGame(), HttpStatus.OK);
        } else
        {
            responseEntity = new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
        }

        session.close();

        return responseEntity;
    }

    /**
     * Edits a players information
     * @param request
     * @param response
     * @param playerID ID of player to update
     * @param json JSON of new information
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/team/{teamID}/player/{playerID}/edit")
    public ResponseEntity editPlayer(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id, @PathVariable("teamID") int teamID, @PathVariable("playerID") int playerID, @RequestParam("json") String json)
    {
        Team team = new Team();
        team.setId(teamID);
        Player player = Reference.gson.fromJson(json, Player.class);
        player.setTeam(team);

        if (player != null)
        {
            Game game = GameService.getGame(id);

            if (game != null)
            {
                Player oldPlayer = PlayerService.getPlayer(playerID);

                if (oldPlayer != null)
                {
                    Session session = DatabaseManager.getSession();
                    session.beginTransaction();

                    session.delete(oldPlayer);
                    session.save(player);

                    session.getTransaction().commit();

                    session.refresh(player);

                    session.close();

                    return new ResponseEntity(player, HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity("Could not edit player", HttpStatus.CONFLICT);
    }

    /**
     * Pulls the Cloud Nine website and stores it in a folder with appropiate game name
     * @param id ID of game to correspond save to
     * @return
     * @throws UnirestException
     * @throws IOException
     */
    @RequestMapping("/{id}/sitepull")
    public ResponseEntity pullCloudNineSites(@PathVariable("id") int id) throws UnirestException, IOException
    {
        Game game = GameService.getGame(id);

        if (game != null)
        {
            GameService.downloadCurrentGame(game);

            return new ResponseEntity("Successfully downloaded and stored Cloud Nine Site", HttpStatus.OK);

        } else
        {
            return new ResponseEntity("Game not found", HttpStatus.NOT_FOUND);
        }
    }

}
