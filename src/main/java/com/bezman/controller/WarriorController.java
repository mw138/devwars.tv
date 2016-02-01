package com.bezman.controller;

import com.bezman.annotation.AuthedUser;
import com.bezman.annotation.JSONParam;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
import com.bezman.model.User;
import com.bezman.model.Warrior;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/v1/warrior")
public class WarriorController {
    @Autowired
    Validator validator;

    /**
     * Cloud Nine registration
     *
     * @param request
     * @param response
     * @param firstName
     * @param email
     * @param month
     * @param day
     * @param year
     * @param htmlRate
     * @param cssRate
     * @param jsRate
     * @param c9Name
     * @param favFood
     * @param favTool
     * @param location
     * @param company
     * @param about
     * @return
     */
    @Transactional
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
                                   @RequestParam("about") String about,
                                   SessionImpl session) {
        User user = (User) request.getAttribute("user");
        user = (User) session.merge(user);

        if (user.getEmail() == null) {
            user.setEmail(email);
        }

        if (month < 1) {
            return new ResponseEntity("Invalid DOB entered", HttpStatus.CONFLICT);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        Warrior warrior = new Warrior(firstName, favFood, favTool, about, c9Name, company, location, htmlRate, cssRate, jsRate, calendar.getTime(), user.getId());

        if (user.getWarrior() != null) return new ResponseEntity("You are already a warrior", HttpStatus.CONFLICT);

        session.save(warrior);

        return new ResponseEntity("Successfully registered", HttpStatus.OK);
    }

    @Transactional
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity updateWarrior(@AuthedUser User user,
                                        @JSONParam("warrior") Warrior warrior,
                                        SessionImpl session) throws JsonProcessingException {

        Errors errors = new BeanPropertyBindingResult(warrior, warrior.getClass().getName());
        validator.validate(warrior, errors);

        if (errors.hasErrors()) {
            return new ResponseEntity(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        if (user.getWarrior() == null) return new ResponseEntity("You are not a warrior", HttpStatus.CONFLICT);

        user = (User) session.merge(user);

        Warrior oldWarrior = user.getWarrior();

        if (!oldWarrior.getHtmlRate().equals(warrior.getHtmlRate()) ||
            !oldWarrior.getCssRate().equals(warrior.getCssRate()) ||
            !oldWarrior.getJsRate().equals(warrior.getJsRate())) {
            if (oldWarrior.getUpdatedAt() == null) {
                warrior.setUpdatedAt(new Date());
            } else {
                long now = new Date().getTime();
                long then = oldWarrior.getUpdatedAt().getTime();

                if (now - then < (86400 * 7 * 2 * 1000))
                    return new ResponseEntity("You can only change your ratings once every two weeks", HttpStatus.CONFLICT);

                warrior.setUpdatedAt(new Date());
            }

        }

        warrior.setId(user.getWarrior().getId());
        session.merge(warrior);

        return new ResponseEntity("Ok", HttpStatus.OK);
    }

}
