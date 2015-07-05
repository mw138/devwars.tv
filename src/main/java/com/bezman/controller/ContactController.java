package com.bezman.controller;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.Activity;
import com.bezman.model.Contact;
import com.bezman.model.User;
import com.bezman.service.Security;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Terence on 4/28/2015.
 */
@Controller
@RequestMapping("/v1/contact")
public class ContactController
{

    @RequestMapping("/create")
    public ResponseEntity create(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam("text") String text,
                                 @RequestParam("type") String type)
    {
        if(text.length() <= 1000 && type.length() < 255)
        {
            Contact contact = new Contact(name, email, type, text);

            String subject = "New " + type + " Inquiry from " + name;
            String message = "Name: " +  name + "\nEmail: " + email + "\nText: " + text + "\nType: " + type;

            Util.sendEmail(Security.emailUsername, Security.emailPassword, subject, message, "support@devwars.tv");

            DatabaseUtil.saveObjects(true, contact);

            return new ResponseEntity(Reference.gson.toJson(contact), HttpStatus.OK);
        } else
        {
            return new ResponseEntity("Enquiry is too long", HttpStatus.BAD_REQUEST);
        }
    }
}
