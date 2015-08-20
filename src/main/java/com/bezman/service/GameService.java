package com.bezman.service;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.init.DatabaseManager;
import com.bezman.model.Game;
import com.bezman.model.Team;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Terence on 1/21/2015.
 */
public class GameService
{

    public static List<Game> allGames(int count, int offset)
    {
        ArrayList<Game> returnList;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from Game order by timestamp desc");
        query.setMaxResults(count);
        query.setFirstResult(offset);

        returnList = (ArrayList) query.list();

        session.close();

        return returnList;
    }

    public static Game defaultGame()
    {
        Game game = new Game();

        game.setName("Default Name");
        game.setActive(false);
        game.setTimestamp(new Timestamp(new Date().getTime()));

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

    public static Game getGame(int id)
    {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where id = :id");
        query.setInteger("id", id);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public static Game currentGame()
    {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where active = :active order by timestamp");
        query.setBoolean("active", true);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public static Game latestGame()
    {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game order by id desc");

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public static Game nearestGame()
    {
        Game game = null;

        Session session = DatabaseManager.getSession();

        Query query = session.createQuery("from Game where timestamp > :timestamp and done = false order by timestamp asc");
        query.setTimestamp("timestamp", new Date());
        query.setMaxResults(1);

        game = (Game) DatabaseUtil.getFirstFromQuery(query);

        session.close();

        return game;
    }

    public static void downloadCurrentGame(Game game) throws UnirestException, IOException
    {
        String redPath = Reference.SITE_STORAGE_PATH + File.separator + game.getId() + File.separator + "red";
        String bluePath = Reference.SITE_STORAGE_PATH + File.separator + game.getId() + File.separator + "blue";

        File redDirectory = new File(redPath);
        File blueDirectory = new File(bluePath);

        redDirectory.mkdirs();
        blueDirectory.mkdirs();

        downloadSiteAtDirectory("https://red-devwars-1.c9.io", redPath);
        downloadSiteAtDirectory("https://blue-devwars-2.c9.io", bluePath);
    }

    public static void downloadSiteAtDirectory(String site, String path) throws IOException, UnirestException
    {
        Document document = Jsoup.parse(Unirest.get(site + "/index.html").asString().getBody());

        document.getElementsByTag("script")
                .forEach(tag -> {
                    String source = tag.attr("src");

                    if (source.charAt(0) == '/') source = source.substring(1);

                    source = source.replace("/", File.separator);

                    if(source.indexOf("http") == 0 ||source.indexOf("//") == 0) return;

                    try
                    {
                        downloadURLToFile(site + "/" + source, new File(path + File.separator + source));
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                });

        document.getElementsByTag("link")
                .forEach(tag -> {
                    String source = tag.attr("href");

                    if (source.indexOf("http") == 0 || source.indexOf("//") == 0) return;


                    if (source.charAt(0) == '/') source = source.substring(1);

                    source = source.replace("/", File.separator);

                    try
                    {
                        downloadURLToFile(site + "/" + source, new File(path + File.separator + source));
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                });

        downloadURLToFile(site + "/index.html", new File(path + File.separator + "index.html"));
    }

    public static void downloadURLToFile(String urlLink, File file) throws IOException
    {
        URL url = new URL(urlLink);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        IOUtils.copy(urlConnection.getInputStream(), fileOutputStream);

        fileOutputStream.close();
        urlConnection.getInputStream().close();
    }

}
