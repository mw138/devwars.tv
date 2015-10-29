package com.bezman.controller;

import com.bezman.init.DatabaseManager;
import com.bezman.model.BlogPost;
import com.bezman.model.Game;
import com.bezman.model.Ranking;
import com.bezman.model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Terence on 4/12/2015.
 */
@Controller
@RequestMapping("/v1/info")
public class InfoController extends BaseController {

    /**
     * Returns Stat info (User count, game count, blog post count, DevBits earned)
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/")
    public ResponseEntity allInfo(HttpServletRequest request, HttpServletResponse response) {
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

    /**
     * Leaderboard for devbits (Just sorts users by devbit amount)
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/bitsleaderboard")
    public ResponseEntity bitsLeaderboard(HttpServletRequest request, HttpServletResponse response) {
        List<User> users = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User u order by u.ranking.points desc");
        query.setMaxResults(5);

        users = query.list();

        session.close();

        return new ResponseEntity(users, HttpStatus.OK);
    }

    /**
     * Leaderboard for XP (Just sorts users by xp amount)
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/xpleaderboard")
    public ResponseEntity xpLeaderboard(HttpServletRequest request, HttpServletResponse response) {
        List<User> users = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from User u order by u.ranking.xp desc");
        query.setMaxResults(5);

        users = query.list();

        session.close();

        return new ResponseEntity(users, HttpStatus.OK);
    }

    /**
     * Sorts based on weird formula Synswag wanted
     *
     * @param request
     * @param response
     * @param page     Pagination if you want
     * @return
     */
    @RequestMapping("/leaderboard")
    public ResponseEntity leaderboard(HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
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

}
