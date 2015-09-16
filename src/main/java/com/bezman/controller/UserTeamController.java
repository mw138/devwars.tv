package com.bezman.controller;

import com.bezman.Reference.Reference;
import com.bezman.annotation.AuthedUser;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
import com.bezman.annotation.UnitOfWork;
import com.bezman.model.Activity;
import com.bezman.model.Notification;
import com.bezman.model.User;
import com.bezman.model.UserTeam;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Terence on 7/21/2015.
 */
@Controller
@RequestMapping("/v1/team")
public class UserTeamController
{
    @Transactional
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity createTeam(SessionImpl session, @AuthedUser User user, @RequestParam("name") String name)
    {
        UserTeam userTeam = new UserTeam(name, user);

        session.save(userTeam);

        return new ResponseEntity("Created Team", HttpStatus.OK);
    }

    @UnitOfWork
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/mine")
    public ResponseEntity getMyTeams(SessionImpl session, @AuthedUser User user)
    {
        List results = session.createCriteria(UserTeam.class)
                .add(Restrictions.eq("owner", user))
                .list();

        return new ResponseEntity(results, HttpStatus.OK);
    }

    @Transactional
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/{id}/invite")
    public ResponseEntity invitePlayer(SessionImpl session, @PathVariable("id") int teamID, @AuthedUser User user, @RequestParam("user") int inviteUser)
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, teamID);

        if (userTeam == null)
        {
            return new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
        }

        User invitedUser = (User) session.get(User.class, inviteUser);

        if (invitedUser == null)
        {
            return new ResponseEntity("User does not exist", HttpStatus.NOT_FOUND);
        }

        if (userTeam.inviteUser(invitedUser))
        {
            Activity activity = new Activity(invitedUser, user, "You were invited to the team : " + userTeam.getName(), 0, 0);
            Notification notification = new Notification(invitedUser, "You were invited to the team : " + userTeam.getName(), false);

            session.save(activity);
            session.save(notification);

            return new ResponseEntity("Successfully Invited User", HttpStatus.OK);
        } else
        {
            return new ResponseEntity("Could not invite user", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/{id}/invite/accept")
    public ResponseEntity acceptInvite(@PathVariable("id") int teamID, SessionImpl session, @AuthedUser User user)
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, teamID);

        if (userTeam == null)
        {
            return new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
        }

        boolean removed = userTeam.getInvites()
                .removeIf(invite -> invite.getId() == user.getId());

        if (removed)
        {
            userTeam.getMembers().stream()
                    .forEach(currentUser -> {
                        Activity activity = new Activity(currentUser, user, user.getUsername() + " joined your team : " + userTeam.getName(), 0, 0);
                        Notification notification = new Notification(currentUser, user.getUsername() + " joined your team : " + userTeam.getName(), false);

                        session.save(activity);
                        session.save(notification);
                    });

            userTeam.getMembers().add(user);

            Activity activity = new Activity(user, user, "You joined team : " + userTeam.getName(), 0, 0);

            session.save(activity);

            return new ResponseEntity("Successfully joined team", HttpStatus.OK);
        } else
        {
            return new ResponseEntity("You were not invited to that team", HttpStatus.BAD_REQUEST);
        }
    }
}