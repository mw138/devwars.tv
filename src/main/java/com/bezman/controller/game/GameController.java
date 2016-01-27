package com.bezman.controller.game;

import com.bezman.Reference.HttpMessages;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.annotation.*;
import com.bezman.init.DatabaseManager;
import com.bezman.model.*;
import com.bezman.request.model.LegacyGame;
import com.bezman.service.*;
import com.bezman.storage.FileStorage;
import com.dropbox.core.DbxException;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.compress.utils.IOUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(value = "/v1/game")
public class GameController {
    @Autowired
    LoadingCache<String, HashMap> pastGamesCache;

    @Autowired
    FileStorage fileStorage;

    @Autowired
    GameService gameService;

    @Autowired
    UserService userService;

    @Autowired
    UserTeamService userTeamService;

    @Autowired
    PlayerService playerService;

    @Autowired
    TournamentService tournamentService;

    /**
     * Retrieved all games with criteria
     *
     * @param count  Number of games wanted
     * @param offset How many games to skip in DB
     * @return All games
     */
    @RequestMapping(value = "/")
    public ResponseEntity allGames(@RequestParam(value = "count", defaultValue = "5", required = false) int count,
                                   @RequestParam(value = "offset", defaultValue = "0", required = false) int offset) {
        count = count > 50 ? 50 : count;

        return new ResponseEntity(gameService.allGames(count, offset), HttpStatus.OK);
    }

    /**
     * Retrieves upcoming DevWars games
     *
     * @return List of games
     */
    @UnitOfWork
    @AllowCrossOrigin(from = "*")
    @RequestMapping("/upcoming")
    public ResponseEntity upcomingGames(SessionImpl session) {
        Query query = session.createQuery("from Game where timestamp > :time and done = false order by timestamp asc");
        query.setTimestamp("time", new Date());

        List<Game> upcomingGames = query.list();

        if (upcomingGames.size() > 0) {
            return new ResponseEntity(upcomingGames, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get the most upcoming tournament games
     *
     * @return List of games that are apart of a tournament
     */
    @RequestMapping("/tournament/upcoming")
    public ResponseEntity upcomingTournaments() {
        return new ResponseEntity(gameService.getUpcomingTournaments(), HttpStatus.OK);
    }


    /**
     * Get the nearest tournament game
     *
     * @return The nearest Tournament game
     */
    @RequestMapping("/tournament/nearest")
    public ResponseEntity nearestTournament() {
        return new ResponseEntity(gameService.getMostUpcomingTournament(), HttpStatus.OK);
    }

    /**
     * Retrieves games that have been done in the past
     *
     * @param queryCount  Number of results (Max 16)
     * @param queryOffset Result Offset
     * @return Response
     */
    @UnitOfWork
    @RequestMapping("/pastgames")
    public ResponseEntity pastGames(
            @RequestParam(value = "count", required = false, defaultValue = "16") int queryCount,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int queryOffset
    ) throws ExecutionException {
        HashMap games = pastGamesCache.get(queryCount + ":" + queryOffset);

        return new ResponseEntity(games, HttpStatus.OK);
    }

    /**
     * Get list of games moving backwards from starting point
     *
     * @return List of games in descending order
     */
    @UnitOfWork
    @RequestMapping("/pastgamelist")
    public ResponseEntity getGameList(
            @RequestParam("firstGame") int id,
            @RequestParam(value = "count", required = false, defaultValue = "10") int count,
            SessionImpl session) {
        List<Game> pastGames = session.createCriteria(Game.class)
                .add(Restrictions.le("id", id))
                .setMaxResults(count)
                .addOrder(Order.desc("id"))
                .list();

        return new ResponseEntity(pastGames, HttpStatus.OK);
    }

    /**
     * Creates a game with the default information
     *
     * @param timestamp The time in UTC which the game will start
     * @param name      Name of the game (Classic or Zen Garden)
     * @return New game
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/create")
    public ResponseEntity createGame(SessionImpl session,
                                     @RequestParam(value = "time", required = false, defaultValue = "0") long timestamp,
                                     @RequestParam(required = false, value = "name") String name,
                                     @RequestParam(required = false, value = "tournament") Integer tournamentID) {

        Tournament tournament = tournamentID == null ?  null : tournamentService.byID(tournamentID);

        Game game = gameService.defaultGame(tournament);

        if (name != null) {
            game.setName(name);

            if (name.equals("Team Classic")) {
                game.setTeamGame(true);
            }
        }

        if (timestamp != 0) {
            game.setTimestamp(new Timestamp(timestamp));
        }

        session.save(game);

        return new ResponseEntity(game.toString(), HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/createlegacy", method = RequestMethod.POST)
    public ResponseEntity createLegacyGame(@JSONParam("game") LegacyGame legacyGame) {
        return new ResponseEntity(gameService.createGameFromLegacyGame(legacyGame), HttpStatus.OK);
    }

    /**
     * Retrieves a game with a given id
     *
     * @param id ID of the game to get
     * @return The Game
     */
    @RequestMapping("/{id}")
    public ResponseEntity getGame(@PathVariable("id") int id) {
        Game game = gameService.getGame(id);

        if (game != null) {
            return new ResponseEntity(game, HttpStatus.OK);
        } else {
            return new ResponseEntity("Could not find game for given ID", HttpStatus.NOT_FOUND);
        }

    }

    @AllowCrossOrigin(from = "*")
    @AngularServiceOmitted
    @RequestMapping("/{id}/{team}/preview/**")
    public ResponseEntity previewTeamForGame(HttpServletResponse response, HttpServletRequest request, @PathVariable("id") int gameID, @PathVariable("team") String team) throws IOException {
        try {
            String mapping = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            String slug = mapping.split("preview/")[1];
            InputStream inputStream = fileStorage.getFileDownloader(fileStorage.SITE_STORAGE_PATH + "/" + gameID + "/" + team + "/" + slug).body;

            IOUtils.copy(inputStream, response.getOutputStream());

            return null;
        } catch (Exception e) {
            return new ResponseEntity("File not uploaded yet", HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Edits a game with new information
     *
     * @return The updated Game
     */
    @SuppressWarnings("MVCPathVariableInspection")
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/update", method = {RequestMethod.POST})
    public ResponseEntity editGame(@PathModel("id") Game game, @JSONParam("game") Game newGame) throws IOException, UnirestException {
        gameService.updateGame(game, newGame);

        if (newGame.isActive()) {
            Unirest.patch("https://devwars-tv.firebaseio.com/frame/game/.json")
                    .queryString("auth", Reference.getEnvironmentProperty("firebaseToken"))
                    .body(Reference.objectMapper.writeValueAsString(newGame))
                    .asString()
                    .getBody();
        }

        return getGame(game.getId());
    }

    /**
     * Sets the game to active and all other games to inactive (Allows the currentGame method to bring back result)
     *
     * @param id ID of game to activate
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/activate")
    public ResponseEntity activateGame(SessionImpl session, @PathVariable("id") int id) {
        Game game = (Game) session.get(Game.class, id);

        if (game != null) {
            Query query = session.createQuery("update Game set active = false where active = true");
            query.executeUpdate();

            game.setActive(true);

            return new ResponseEntity(game, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Sets the game to inactive
     *
     * @param id ID of game to deactivate
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/deactivate")
    public ResponseEntity deactivateGame(SessionImpl session, @PathVariable("id") int id) {
        Game game = (Game) session.get(Game.class, id);

        if (game != null) {
            game.setActive(false);

            return new ResponseEntity(game, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Ends the game and wards winning team
     *
     * @param request
     * @param response
     * @param gameID   ID of game to end
     * @param winnerID The ID of the winning team (Awards corresponding values)
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/endgame")
    public ResponseEntity endGame(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable("id") int gameID,
                                  @RequestParam("winner") int winnerID) throws IOException, UnirestException, DbxException {
        ResponseEntity responseEntity = null;

        Game game = gameService.getGame(gameID);

        if (game != null) {
            gameService.downloadCurrentGame(game);

            Team team = game.getTeamByID(winnerID);

            if (team != null) {
                game.setDone(true);
                game.setActive(false);

                for (Team otherTeam : game.getTeams().values()) {
                    otherTeam.setWin(false);
                }

                team.setWin(true);

                DatabaseUtil.saveOrUpdateObjects(true, game, team);

                responseEntity = new ResponseEntity("Successfully ended game", HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
            }
        } else {
            responseEntity = new ResponseEntity("Game not found", HttpStatus.NOT_FOUND);
        }

        game = gameService.getGame(gameID);

        for (Team team : game.getTeams().values()) {
            //Participation
            team.getPlayers().stream().forEach(p ->
            {
                p.setXpChanged(p.getXpChanged() + 50);
                p.setPointsChanged(p.getPointsChanged() + 222);

                p.getUser().getRanking().addXP(50);
                p.getUser().getRanking().addPoints(222);
            });

            //Completed All Objectives
            if (team.didCompleteAllObjectives()) {
                team.getPlayers().stream().forEach(p ->
                {
                    p.setXpChanged(p.getXpChanged() + 150);

                    p.getUser().getRanking().addXP(150);
                });
            }

            //Team won
            if (team.isWin()) {
                team.getPlayers().stream().forEach(p ->
                {
                    p.setXpChanged(p.getXpChanged() + 250);
                    p.setPointsChanged(p.getPointsChanged() + 444);

                    p.getUser().getRanking().addXP(250);
                    p.getUser().getRanking().addPoints(444);
                });
            }

            Session session = DatabaseManager.getSession();
            session.beginTransaction();

            for (Player player : team.getPlayers()) {
                session.saveOrUpdate(player);
                session.saveOrUpdate(player.getUser().getRanking());
            }

            session.getTransaction().commit();
            session.close();
        }

        pastGamesCache.invalidateAll();

        return responseEntity;
    }

    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/resetwinner")
    public ResponseEntity resetGameWinner(@PathVariable("id") int id, SessionImpl session) {
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
     *
     * @param request
     * @param response
     * @param id       ID of game to delete
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/delete")
    public ResponseEntity deleteGame(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id) {
        ResponseEntity responseEntity = null;

        Game game = gameService.getGame(id);

        if (game != null) {
            game.deleteFullGame();
            responseEntity = new ResponseEntity("Game " + id + " was deleted", HttpStatus.OK);
        } else responseEntity = new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);


        return responseEntity;
    }

    /**
     * Gets the current active game
     *
     * @return The only  game which is active
     */
    @AllowCrossOrigin(from = "*")
    @RequestMapping(value = "/currentgame")
    public ResponseEntity currentGame() {
        Game currentGame = gameService.currentGame();

        if (currentGame != null) {
            return new ResponseEntity(currentGame, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpMessages.NO_CURRENT_GAME, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets the most recently created game
     *
     * @return The most recent game
     */
    @RequestMapping(value = "/latestgame")
    public ResponseEntity latestGame() {
        Game currentGame = gameService.latestGame();

        if (currentGame != null) {
            return new ResponseEntity(currentGame.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpMessages.NO_GAMES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets the game which is nearest to the current date in the future
     *
     * @return The next game (Nearest Game)
     */
    @RequestMapping("/nearestgame")
    public ResponseEntity nearestGame() {
        Game nearestGame = gameService.nearestGame();

        if (nearestGame != null) {
            return new ResponseEntity(nearestGame, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpMessages.NO_GAME_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets all signed up users for given game
     *
     * @param id ID of game to get sign ups for
     * @return A list of players that have signed up for the game
     */
    @UnitOfWork
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/pendingplayers")
    public ResponseEntity pendingPlayers(SessionImpl session, @PathVariable("id") int id) {
        Query query = session.createQuery("select user from GameSignup g where g.game.id = :id");
        query.setParameter("id", id);

        List<User> users = query.list();

        return new ResponseEntity(users, HttpStatus.OK);
    }

    /**
     * Removes a user from the game and penlizes them with some value
     *
     * @param session
     * @param player  The player to remove
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/forfeituser", method = RequestMethod.POST)
    public ResponseEntity forfeitUser(SessionImpl session,
                                      @JSONParam("player") Player player) {
        player = (Player) session.get(Player.class, player.getId());

        Ranking ranking = player.getUser().getRanking();
        ranking.addPoints(-50);
        ranking.addXP(-50);

        session.delete(player);

        return new ResponseEntity(player, HttpStatus.OK);
    }

    /**
     * Signs a user up for a game
     *
     * @param id ID of game to sign up for
     * @return Message
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/{id}/signup")
    public ResponseEntity signUpForGame(SessionImpl session, @AuthedUser User user, @PathVariable("id") int id) {
        Game game = (Game) session.get(Game.class, id);

        if (game != null) {
            boolean hasSignedUp = game.getSignups().stream()
                    .anyMatch(signup -> signup.getUser().getId() == user.getId());

            if (!hasSignedUp) {

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

    /**
     * Endpoint for team leaders to signup their team to a team game
     * @param user (Resolved)
     * @param users Array of TeamGameSignupUser
     * @param id ID of game to apply to
     * @return Response
     */
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping(value = "/{id}/applyteam", method = RequestMethod.POST)
    public ResponseEntity applyTeamForGame(@AuthedUser User user,
                                           @JSONParam("users") TeamGameSignupUser[] users,
                                           @PathVariable("id") int id) {
        Game game = gameService.getGame(id);

        if (!game.getTeamGame())
            return new ResponseEntity("Game must be a team game", HttpStatus.BAD_REQUEST);

        if (user.getOwnedTeam() == null)
            return new ResponseEntity("You do not own a team", HttpStatus.FORBIDDEN);

        if (!userTeamService.doesUserHaveAuthorization(user, user.getOwnedTeam()))
            return new ResponseEntity("You are not allowed to do that", HttpStatus.FORBIDDEN);

        if (users.length != 3)
            return new ResponseEntity("You must only sign up three players", HttpStatus.BAD_REQUEST);

        if (game.getTeamGameSignups().stream().anyMatch(team -> team.getUserTeam().getId() == user.getTeam().getId()))
            return new ResponseEntity("You have already signed up for this game", HttpStatus.CONFLICT);

        gameService.signupTeamForGame(user.getOwnedTeam(), users, game);

        return new ResponseEntity("Successfully applied team to game", HttpStatus.OK);
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping(value = "/{id}/addteamgamesignup", method = RequestMethod.POST)
    public ResponseEntity signupTeamForGame(@PathVariable("id") int id, @RequestParam("team") int teamID) {
        UserTeam userTeam = userTeamService.byID(teamID);

        Game game = gameService.getGame(id);

        return null;

    }

    /**
     * Method to sign up a twitch user (Meant for chat command)
     *
     * @param username Username of twitch user to apply
     * @param id       ID of game to apply user to
     * @return Response Entity
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @Transactional
    @RequestMapping(value = "/{id}/signuptwitchuser", method = RequestMethod.POST)
    public ResponseEntity signUpTwitchUser(@RequestParam("username") String username, @PathVariable("id") int id, SessionImpl session) {
        User theTwitchUser = userService.userForTwitchUsername(username);

        if (theTwitchUser != null) {
            User twitchUser = (User) session.merge(theTwitchUser);

            Game game = (Game) session.get(Game.class, id);

            if (game == null) {
                return new ResponseEntity("Game not found", HttpStatus.NOT_FOUND);
            }

            boolean isUserApplied = game.getSignups().stream()
                    .anyMatch(signup -> signup.getUser().getId() == twitchUser.getId());

            if (!isUserApplied) {
                if (twitchUser.getWarrior() == null) {
                    return new ResponseEntity("You are not a warrior, click http://devwars.tv/warrior-signup to sign up.", HttpStatus.CONFLICT);
                }

                GameSignup gameSignup = new GameSignup(twitchUser, game);

                session.save(gameSignup);

                return new ResponseEntity("Successfully signed up " + twitchUser.getUsername(), HttpStatus.OK);
            } else return new ResponseEntity("You are already signed up for that game.", HttpStatus.CONFLICT);

        } else
            return new ResponseEntity("Twitch user not found. Please connect your twitch account with your DevWars account.", HttpStatus.NOT_FOUND);
    }

    @Transactional
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/{id}/resign")
    public ResponseEntity resignFromGame(SessionImpl session, @AuthedUser User user, @PathVariable("id") int id) {
        Game game = (Game) session.get(Game.class, id);

        Optional<GameSignup> foundSignup = game.getSignups().stream()
                .filter(a -> a.getUser().getId() == user.getId())
                .findFirst();

        if (foundSignup.isPresent()) {
            session.delete(foundSignup.get());

            return new ResponseEntity("Successfully resigned", HttpStatus.OK);
        }

        return new ResponseEntity("Sign Up not found", HttpStatus.NOT_FOUND);
    }

    /**
     * Adds votes to a team for voting part of game
     *
     * @param id     ID of game
     * @param teamID ID of team
     * @param func   How many to add on the functionality vote
     * @param design How many to add on the design vote
     * @param code   How many to add on the code vote
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/team/{teamID}/addvotes")
    public ResponseEntity addVotes(@PathVariable("id") int id,
                                   @PathVariable("teamID") int teamID,
                                   @RequestParam(value = "func", required = false, defaultValue = "0") int func,
                                   @RequestParam(value = "design", required = false, defaultValue = "0") int design,
                                   @RequestParam(value = "code", required = false, defaultValue = "0") int code) {
        ResponseEntity responseEntity = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from Team where id = :id");
        query.setInteger("id", teamID);

        Team team = (Team) DatabaseUtil.getFirstFromQuery(query);

        if (team != null) {
            session.beginTransaction();

            team.setDesignVotes(team.getDesignVotes() + design);
            team.setFuncVotes(team.getFuncVotes() + func);
            team.setCodeVotes(team.getCodeVotes() + code);

            session.persist(team);
            session.getTransaction().commit();

            responseEntity = new ResponseEntity(team, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
        }

        session.close();
        return responseEntity;
    }

    /**
     * Adds points to every player in a team
     *
     * @param request
     * @param response
     * @param id       ID of game
     * @param teamID   ID of team
     * @param points   How many points to add to each player
     * @param xp       How much xp to add to each player
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/team/{teamID}/addpoints")
    public ResponseEntity addPointsToTeam(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable("id") int id,
                                          @PathVariable("teamID") int teamID,
                                          @RequestParam(value = "points", required = false, defaultValue = "0") int points,
                                          @RequestParam(value = "xp", required = false, defaultValue = "0") int xp) {
        ResponseEntity responseEntity = null;

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        Query query = session.createQuery("from Team where id = :id");
        query.setInteger("id", teamID);

        Team team = (Team) DatabaseUtil.getFirstFromQuery(query);

        if (team != null) {
            for (Player player : team.getPlayers()) {
                if (player.getUser().getRanking() == null) {
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
        } else {
            responseEntity = new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
        }

        session.close();

        return responseEntity;
    }

    /**
     * Edits a players information
     *
     * @param request
     * @param response
     * @param playerID ID of player to update
     * @param json     JSON of new information
     * @return
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/team/{teamID}/player/{playerID}/edit")
    public ResponseEntity editPlayer(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id, @PathVariable("teamID") int teamID, @PathVariable("playerID") int playerID, @RequestParam("json") String json) throws IOException {
        Team team = new Team();
        team.setId(teamID);
        Player player = Reference.objectMapper.readValue(json, Player.class);
        player.setTeam(team);

        Game game = gameService.getGame(id);

        if (game != null) {
            Player oldPlayer = playerService.getPlayer(playerID);

            if (oldPlayer != null) {
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

        return new ResponseEntity("Could not edit player", HttpStatus.CONFLICT);
    }

    /**
     * Pulls the Cloud Nine website and stores it in a folder with appropiate game name
     *
     * @param id ID of game to correspond save to
     * @return
     * @throws UnirestException
     * @throws IOException
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/{id}/sitepull")
    public ResponseEntity pullCloudNineSites(@PathVariable("id") int id) throws UnirestException, IOException, DbxException {
        Game game = gameService.getGame(id);

        if (game != null) {
            gameService.downloadCurrentGame(game);

            return new ResponseEntity("Successfully downloaded and stored Cloud Nine Site", HttpStatus.OK);

        } else {
            return new ResponseEntity("Game not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method to pull the Cloud Nine website for a specific game
     *
     * @param id       The ID of the games websites you would like to pull
     * @param response
     * @throws IOException
     */
    @PreAuthorization(minRole = User.Role.BLOGGER)
    @RequestMapping("/{id}/sitearchive")
    public String siteArchive(@PathVariable("id") int id, HttpServletResponse response) throws IOException {

        Game game = gameService.getGame(id);

        if (game != null) {
            return "redirect:" + fileStorage.shareableUrlForPath(fileStorage.SITE_STORAGE_PATH + game.getId());
        }

        return null;
    }


    @AllowCrossOrigin(from = "*")
    @RequestMapping("/{id}/teaminfo")
    public ResponseEntity teamInfoForGame(@PathVariable("id") int gameID) {
        Game game = gameService.getGame(gameID);

        if (game == null) {
            return new ResponseEntity("Game not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(gameService.getTeamPicsForGame(game), HttpStatus.OK);
    }
}
