package com.bezman.controller;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.model.BlogPost;
import com.bezman.model.Game;
import com.bezman.model.Ranking;
import com.bezman.model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Terence on 4/12/2015.
 */
@Controller
@RequestMapping("/v1/info")
public class InfoController extends BaseController
{

    @RequestMapping("/")
    public ResponseEntity allInfo(HttpServletRequest request, HttpServletResponse response)
    {
        org.springframework.web.context.ContextLoaderListener loaderListener;

        JSONObject jsonObject = new JSONObject();

        Session session = DatabaseManager.getSession();

        //User Count
        jsonObject.put("userCount", User.count(User.class));

        //Games Count
        jsonObject.put("gameCount", Game.count(Game.class));

        //Blog Count
        jsonObject.put("blogPostCount", BlogPost.count(BlogPost.class));

        //Ranking Sum
        jsonObject.put("pointsEarned", Ranking.sumField(Ranking.class, "points"));

        session.close();

        return new ResponseEntity(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @RequestMapping("/bitsleaderboard")
    public ResponseEntity bitsLeaderboard(HttpServletRequest request, HttpServletResponse response)
    {
        List<User> users = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User u order by u.ranking.points desc");
        query.setMaxResults(5);

        users = query.list();

        session.close();

        return new ResponseEntity(users, HttpStatus.OK);
    }

    @RequestMapping("/xpleaderboard")
    public ResponseEntity xpLeaderboard(HttpServletRequest request, HttpServletResponse response)
    {
        List<User> users = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User u order by u.ranking.xp desc");
        query.setMaxResults(5);

        users = query.list();

        session.close();

        return new ResponseEntity(users, HttpStatus.OK);
    }

    @RequestMapping("/leaderboard")
    public ResponseEntity leaderboard(HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam(value = "page", required = false, defaultValue = "0") int page)
    {
        List<Object[]> results = new ArrayList<>();

        int maxResults = 10;

        Session session = DatabaseManager.getSession();
        String queryString = "select *, (select @gamesPlayed \\:= COUNT(*) from players where user_id = u.id),\n" +
                "  (select @gamesWon \\:= COUNT(*) from players AS p where (p.user_id = u.id AND (select win from teams where id = p.team_id) = true)),\n" +
                "  (select @ratio \\:= @gamesWon / @gamesPlayed),\n" +
                "  ROUND(\n" +
                "      (((select xp from devwars.ranking as ranking where ranking.id = u.id) *  @gamesPlayed) * \n" +
                "      ( @ratio )) / 10\n" +
                "  ) AS score from accounts as u order by score desc limit :maxResults offset :offset";

        Query query = session.createSQLQuery(queryString).addEntity(User.class).addScalar("score");

        System.out.println(queryString);

        query.setParameter("maxResults", maxResults);
        query.setParameter("offset", maxResults * page);

        results = query.list();

        session.close();

        return new ResponseEntity(results, HttpStatus.OK);
    }

    /*@RequestMapping("/leaderboard/seed")
    public ResponseEntity seedLeaderboard(HttpServletRequest request, HttpServletResponse response)
    {

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        Query query = session.createQuery("from User ");

        List<User> users = query.list();

        for(User user : users)
        {
            if (user.getRanking() == null)
            {
                Ranking ranking = new Ranking();
                ranking.setId(user.getId());

                ranking.setPoints((double) new Random().nextInt(10000));
                ranking.setXp((double) new Random().nextInt(10000));

                session.save(ranking);
            }
        }

        session.getTransaction().commit();
        session.close();

        return null;
    }*/



}
