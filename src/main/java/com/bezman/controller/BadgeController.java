package com.bezman.controller;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
import com.bezman.annotation.UnitOfWork;
import com.bezman.model.Badge;
import com.bezman.model.BaseModel;
import com.bezman.model.User;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Terence on 6/28/2015.
 */
@Controller
@RequestMapping("/v1/badge")
public class BadgeController
{

    @RequestMapping("/all")
    public ResponseEntity getAll(HttpServletRequest request, HttpServletResponse response)
    {
        //Comment
        return new ResponseEntity(BaseModel.all(Badge.class), HttpStatus.OK);
    }

    @RequestMapping("/{id}")
    @UnitOfWork
    public ResponseEntity getBadge(@PathVariable("id") int id, SessionImpl session)
    {
        return new ResponseEntity(Reference.gson.toJson(session.get(Badge.class, id)), HttpStatus.OK);
    }


}
