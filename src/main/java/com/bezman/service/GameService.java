package com.bezman.service;

import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.init.DatabaseManager;
import com.bezman.model.*;
import com.bezman.request.model.LegacyGame;
import com.bezman.request.model.LegacyObjective;
import com.bezman.storage.FileStorage;
import com.dropbox.core.DbxException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mysql.jdbc.ReflectiveStatementInterceptorAdapter;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Terence on 1/21/2015.
 */
@Service
public class GameService {
    @Autowired
    public FileStorage fileStorage;

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    public List<Game> allGames(int count, int offset) {
        ArrayList<Game> returnList;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from Game order by timestamp desc");
        query.setMaxResults(count);
        query.setFirstResult(offset);

        returnList = (ArrayList) query.list();

        session.close();

        return returnList;
    }

    public Game defaultGame(Tournament tournament) {
        Game game = new Game();

        game.setName("Default Name");
        game.setActive(false);
        game.setTimestamp(new Timestamp(new Date().getTime()));
        game.setSeason(2);

        if (tournament != null) {
            game.setTournament(tournament);
        }

        Team blueTeam = new Team();
        blueTeam.setGame(game);
        blueTeam.setName("blue");
        blueTeam.setEmbedLink("Default embed link");
        blueTeam.setStatus("Waiting");
        blueTeam.setWin(false);

        Team redTeam = new Team();
        redTeam.setGame(game);
        redTeam.setName("red");
        redTeam.setEmbedLink("Default embed link");
        redTeam.setStatus("Waiting");
        redTeam.setWin(false);

        game.getTeams().put(blueTeam.getName(), blueTeam);
        game.getTeams().put(redTeam.getName(), redTeam);

        System.out.println(game.toString());

        return game;
    }

    public Game getGame(int id) {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where id = :id");
        query.setInteger("id", id);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public Game currentGame() {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where active = :active order by timestamp");
        query.setBoolean("active", true);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public Game latestGame() {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game order by id desc");

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public Game nearestGame() {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where timestamp > :timestamp and done = false order by timestamp asc");
        query.setTimestamp("timestamp", new Date());
        query.setMaxResults(1);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public void downloadCurrentGame(Game game) throws UnirestException, IOException, DbxException {
        String redPath = fileStorage.SITE_STORAGE_PATH + "/" + game.getId() + "/" + "red";
        String bluePath = fileStorage.SITE_STORAGE_PATH + "/" + game.getId() + "/" + "blue";

        downloadSiteAtDirectory("https://red-devwars-1.c9.io", redPath);
        downloadSiteAtDirectory("https://blue-devwars-2.c9.io", bluePath);
    }

    public Game updateGame(Game game, Game newGame) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        newGame.setSignups(game.getSignups());

        session.saveOrUpdate(newGame);

        session.getTransaction().commit();
        session.close();

        return newGame;
    }

    public void downloadSiteAtDirectory(String site, String path) throws IOException, UnirestException, DbxException {
        Document document = Jsoup.parse(Unirest.get(site + "/index.html").asString().getBody());

        document.getElementsByTag("script")
                .forEach(tag -> {
                    String source = tag.attr("src");

                    if (source.charAt(0) == '/') source = source.substring(1);

                    source = source.replace("/", File.separator);

                    if (source.indexOf("http") == 0 || source.indexOf("//") == 0) return;

                    try {
                        downloadURLToFile(site + "/" + source, (path + File.separator + source));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DbxException e) {
                        e.printStackTrace();
                    }
                });

        document.getElementsByTag("link")
                .forEach(tag -> {
                    String source = tag.attr("href");

                    if (source.indexOf("http") == 0 || source.indexOf("//") == 0) return;


                    if (source.charAt(0) == '/') source = source.substring(1);

                    source = source.replace("/", File.separator);

                    try {
                        downloadURLToFile(site + "/" + source, (path + File.separator + source));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DbxException e) {
                        e.printStackTrace();
                    }
                });

        document.getElementsByTag("img")
                .forEach(tag -> {
                    String source = tag.attr("src");

                    if (source.indexOf("http") == 0 || source.indexOf("//") == 0) return;


                    if (source.charAt(0) == '/') source = source.substring(1);

                    source = source.replace("/", File.separator);

                    try {
                        downloadURLToFile(site + "/" + source, (path + File.separator + source));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DbxException e) {
                        e.printStackTrace();
                    }
                });

        downloadURLToFile(site + "/index.html", (path + "/" + "index.html"));
    }

    public void downloadURLToFile(String urlLink, String path) throws IOException, DbxException {
        URL url = new URL(urlLink);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        fileStorage.uploadFile(path, urlConnection.getInputStream());

        urlConnection.getInputStream().close();
    }

    public HashMap pastGames(Integer queryCount, Integer queryOffset) {
        Session session = DatabaseManager.getSession();
        /**
         * Make sure count isn't too high
         */
        final Integer count = queryCount > 8 ? 8 : queryCount;

        /**
         * Get all seasons
         */
        Criteria criteria = session.createCriteria(Game.class)
                .setProjection(Projections.projectionList()
                                .add(Projections.groupProperty("season"))
                );

        HashMap pastGames = new HashMap<>();

        criteria.list().stream()
                .forEach(season ->
                {
                    Criteria seasonCriteria = session.createCriteria(Game.class)
                            .add(Restrictions.eq("season", season))
                            .add(Restrictions.eq("done", true))
                            .addOrder(Order.desc("id"))
                            .setMaxResults(count)
                            .setFirstResult(queryOffset);

                    pastGames.put(season, seasonCriteria.list());
                });

        session.close();

        return pastGames;
    }

    public Game getMostUpcomingTournament() {
        Game game;

        Session session = DatabaseManager.getSession();

        game = (Game) session.createCriteria(Game.class)
                .add(Restrictions.isNotNull("tournament"))
                .add(Restrictions.ge("timestamp", new Date()))
                .addOrder(Order.desc("timestamp"))
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return game;
    }

    public List<Game> getUpcomingTournaments() {
        List<Game> games = null;

        Session session = DatabaseManager.getSession();

        games = session.createCriteria(Game.class)
                .add(Restrictions.isNotNull("tournament"))
                .add(Restrictions.ge("timestamp", new Date()))
                .addOrder(Order.desc("timestamp"))
                .list();

        session.close();

        return games;
    }

    public Game createGameFromLegacyGame(LegacyGame legacyGame) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        Game game = this.defaultGame(null);

        session.save(game);

        game.setName(legacyGame.getName());
        game.setTimestamp(legacyGame.getTimestamp());
        game.setSeason(1);
        game.setTheme(legacyGame.getTheme());
        game.setYoutubeURL(legacyGame.getYoutubeURL());
        game.setDone(true);

        Team redTeam = game.getTeams().get("red");
        Team blueTeam = game.getTeams().get("blue");

        redTeam.setPlayers(new HashSet<>());
        blueTeam.setPlayers(new HashSet<>());

        redTeam.setGame(game);
        blueTeam.setGame(game);

        legacyGame.getRedTeam().forEach((lang, username) -> {
            Player player = new Player(redTeam, userService.userForUsernameOrNewVeteranUser(username), lang);
            session.save(player);

            redTeam.getPlayers().add(player);
        });

        legacyGame.getBlueTeam().forEach((lang, username) -> {
            Player player = new Player(blueTeam, userService.userForUsernameOrNewVeteranUser(username), lang);
            session.save(player);

            blueTeam.getPlayers().add(player);
        });

        redTeam.setDesignVotes(legacyGame.getDesignVotesRed());
        blueTeam.setDesignVotes(legacyGame.getDesignVotesBlue());

        redTeam.setFuncVotes(legacyGame.getFuncVotesRed());
        blueTeam.setFuncVotes(legacyGame.getFuncVotesBlue());

        for (int i = 0; i < legacyGame.getObjectives().size(); i++) {
            LegacyObjective legacyObjective = legacyGame.getObjectives().get(i);

            Objective objective = new Objective(legacyObjective.getName(), game, i);

            session.save(objective);

            if (legacyObjective.getRed()) {
                teamService.addObjectiveToCompleted(redTeam, objective);
            }

            if (legacyObjective.getBlue()) {
                teamService.addObjectiveToCompleted(blueTeam, objective);
            }
        }

        session.getTransaction().commit();
        session.close();

        return game;
    }

    public void applyUserToGame(User user, Game game) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        game = (Game) session.merge(game);

        game.getSignups().add(new GameSignup(user, game));

        session.getTransaction().commit();
        session.close();
    }

    public void applyTeamToGame(UserTeam userTeam, Game game)
    {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        final Game mergedGame = (Game) session.merge(game);

        userTeam.getMembers().stream().forEach(member -> {
            GameSignup gameSignup = new GameSignup(member, mergedGame);
            session.save(gameSignup);
            mergedGame.getSignups().add(gameSignup);
        });

        session.getTransaction().commit();
        session.close();
    }

    public HashMap<String, HashMap<String, String>> getTeamPicsForGame(Game game) {

        String defaultUrl = "/assets/img/default-avatar.png";

        UserTeam redTeam = teamService.userTeamForTeam(game.getTeams().get("red"));
        UserTeam blueTeam = teamService.userTeamForTeam(game.getTeams().get("blue"));

        HashMap<String, String> redTeamMap = new HashMap<>();
        HashMap<String, String> blueTeamMap = new HashMap<>();

        redTeamMap.put("link", redTeam == null ? defaultUrl : redTeam.getAvatarURL());
        blueTeamMap.put("link", blueTeam == null ? defaultUrl : blueTeam.getAvatarURL());

        if (blueTeam != null) {
            blueTeamMap.put("name", blueTeam.getName());
        }

        if (redTeam != null) {
            redTeamMap.put("name", redTeam.getName());
        }

        HashMap<String, HashMap<String, String>> returnMap = new HashMap<>();
        returnMap.put("blue", blueTeamMap);
        returnMap.put("red", redTeamMap);

        return returnMap;
    }
}
