package com.bezman.controller;

import com.bezman.annotation.UnitOfWork;
import com.bezman.model.Badge;
import com.bezman.service.UserService;
import org.hibernate.internal.SessionImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/v1/badge")
public class BadgeController
{

    /**
     * @param session
     * @return All available badges
     */
    @RequestMapping("/all")
    @UnitOfWork
    public ResponseEntity getAll(SessionImpl session)
    {
        System.out.println(session);
        List<Badge> badges = session.createCriteria(Badge.class)
                                        .list();

        HashMap<String, Object> map = new HashMap<>();

        map.put("badges", badges);
        map.put("userCount", UserService.userCount());

        return new ResponseEntity(map, HttpStatus.OK);
    }

    /**
     * @param id of badge requested
     * @param session
     * @return The badge
     */
    @RequestMapping("/{id}")
    @UnitOfWork
    public ResponseEntity getBadge(@PathVariable("id") int id, SessionImpl session)
    {
        return new ResponseEntity(session.get(Badge.class, id), HttpStatus.OK);
    }


}
