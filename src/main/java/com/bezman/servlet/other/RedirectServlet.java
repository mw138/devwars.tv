package com.bezman.servlet.other;

import com.bezman.annotation.PreAuthorization;
import com.bezman.model.User;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Terence on 2/25/2015.
 */
@Controller
@RequestMapping("/")
public class RedirectServlet {

    @PreAuthorization(minRole = User.Role.NONE)
    @RequestMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getAttribute("user");

        if (user != null) {
            response.sendRedirect("/dashboard");
            return null;
        } else {
            return "index";
        }
    }


    @RequestMapping(value = {"passwordreset", "modDoc", "team", "blog/*", "settings/warrior", "modCP/createteams", "modCP/createobjectives", "modCP/livegame", "modCP/postgame", "/modCP/creategame", "/dashboard/badges", "/leaderboards", "/coming", "/dashboard", "/dashboard/profile", "/warrior-signup", "/about", "/blog", "/games", "/contact", "/help", "/shop", "/badges", "/profile", "/settings", "/settings/profile", "/settings/notifications", "/settings/connections", "/leaderboard", "/live", "/gpanel"})
    public String about(HttpServletRequest request, HttpServletResponse response) {
        return "index";
    }

    @RequestMapping("/unauth")
    public ResponseEntity unauth(HttpServletRequest request, HttpServletResponse response, @RequestParam("requiredRole") String required) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("required", required);

        return new ResponseEntity(jsonObject.toJSONString(), HttpStatus.FORBIDDEN);
    }

    @RequestMapping("/challenge")
    public String challenge() {
        return "/challenge/index";
    }

    @RequestMapping("/codeview")
    public String codeView() {
        return "/codeview/index";
    }

    @RequestMapping("/s{seasonID}/{gameID}")
    public String getGamePage(@PathVariable("gameID") int gameID, @PathVariable("seasonID") int seasonID) {
        return "redirect:/games?game=" + gameID + "&season=" + seasonID;
    }

    @RequestMapping({"/font/{slug:.+}", "/assets/fonts/{slug:.+}"})
    public String getFontAwesome(@PathVariable("slug") String slug) {
        return "redirect:/bower_components/font-awesome/fonts/" + slug;
    }
}
