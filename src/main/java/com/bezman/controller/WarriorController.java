package com.bezman.controller;

import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.User;
import com.bezman.model.Warrior;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Terence on 5/27/2015.
 */
@Controller
@RequestMapping("/v1/warrior")
public class WarriorController
{

    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/register")
    public ResponseEntity register(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam("firstName") String firstName,
                                   @RequestParam("email") String email,
                                   @RequestParam("month") int month,
                                   @RequestParam("day") int day,
                                   @RequestParam("year") int year,
                                   @RequestParam("htmlRate") int htmlRate,
                                   @RequestParam("cssRate") int cssRate,
                                   @RequestParam("jsRate") int jsRate,
                                   @RequestParam("c9Name") String c9Name,
                                   @RequestParam("favFood") String favFood,
                                   @RequestParam("favTool") String favTool,
                                   @RequestParam("location") String location,
                                   @RequestParam(value = "company", required = false) String company,
                                   @RequestParam("about") String about)
    {
        User user = (User) request.getAttribute("user");

        if (user.getEmail() == null)
        {
            user.setEmail(email);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        Warrior warrior = new Warrior(firstName, favFood, favTool, about, c9Name, company, location, htmlRate, cssRate, jsRate, calendar.getTime(), user.getId());

        if(user.getWarrior() == null)
        {
            DatabaseUtil.mergeObjects(false, user);
            DatabaseUtil.saveObjects(true, warrior);
            return new ResponseEntity("Successfully registered to be a warrior", HttpStatus.OK);
        } else
        {
            return new ResponseEntity("You are already a warrior", HttpStatus.CONFLICT);
        }
    }


}
