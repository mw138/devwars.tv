package com.bezman.controller;

import com.bezman.Reference.Reference;
import com.bezman.annotation.AuthedUser;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
import com.bezman.annotation.UnitOfWork;
import com.bezman.model.*;
import com.bezman.service.UserService;
import com.bezman.service.UserTeamService;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by Terence on 7/21/2015.
 */
@Controller
@RequestMapping("/v1/teams")
public class UserTeamController
{

    @Autowired
    Validator validator;

    /**
     * Returns a team for a given ID
     *
     * @param id      ID of the team
     * @param session (Resolved)
     * @return The team
     */
    @UnitOfWork
    @RequestMapping("/{id}")
    public ResponseEntity getTeam(@PathVariable("id") int id, SessionImpl session)
    {
        return new ResponseEntity(session.get(UserTeam.class, id), HttpStatus.OK);
    }

    /**
     * Get the avatar image for a team
     * @param id ID of the team
     * @param response (Resolved)
     * @throws IOException
     */
    @RequestMapping("/{id}/avatar")
    public void getTeamAvatar(@PathVariable("id") int id, HttpServletResponse response) throws IOException
    {
        File file = new File(Reference.TEAM_PICTURE_PATH + File.separator + id, "avatar.jpg");
        File defaultFile = new File(Reference.TEAM_PICTURE_PATH, "default.jpg");

        if(file.exists())
            IOUtils.copy(new FileInputStream(file), response.getOutputStream());

        IOUtils.copy(new FileInputStream(defaultFile), response.getOutputStream());
    }

    /**
     * Change a teams avatar
     * @param session (Resolved)
     * @param multipartFile (Image)
     * @param id ID of team to change picture
     * @return Response
     * @throws IOException
     */
    @UnitOfWork
    @RequestMapping(value = "/{id}/avatar", method = RequestMethod.POST)
    public ResponseEntity changeAvatar(SessionImpl session,
                                       @AuthedUser User user,
                                       @RequestParam("image") MultipartFile multipartFile,
                                       @PathVariable("id") int id) throws IOException
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, id);

        if (userTeam == null)
            return new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);

        if (!UserTeamService.doesUserHaveAuthorization(user, userTeam))
            return new ResponseEntity("You cannot do that", HttpStatus.FORBIDDEN);

        UserTeamService.changeTeamPicture(userTeam, multipartFile.getInputStream());

        return new ResponseEntity("Successfully changed picture", HttpStatus.OK);
    }

    /**
     * Validates if names or tags are taken
     * @param name The name to check
     * @param tag The tag to check
     * @return JSON with true if the name is taken
     */
    @RequestMapping("/check")
    public ResponseEntity checkTeamInformation(@RequestParam(value = "name", required = false, defaultValue = "") String name, @RequestParam(value = "tag", required = false, defaultValue = "") String tag)
    {
        HashMap info = new HashMap();

        info.put("name", UserTeamService.isNameTaken(name));
        info.put("tag", UserTeamService.isTagTaken(tag));

        return new ResponseEntity(info, HttpStatus.OK);
    }

    /**
     * Creates a team and adds the user to it
     *
     * @param session (Resolved)
     * @param user    (Resolved)
     * @param name    The name of the team
     * @return A message for the user
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity createTeam(SessionImpl session,
                                     @AuthedUser User user,
                                     @RequestParam("name") String name,
                                     @RequestParam("tag") String tag,
                                     @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException
    {
        user = (User) session.merge(user);

        if (UserTeamService.doesUserBelongToTeam(user))
            return new ResponseEntity("You already belong to a team", HttpStatus.CONFLICT);

        if (user.getWarrior() == null)
            return new ResponseEntity("You must be a warrior", HttpStatus.CONFLICT);

        UserTeam userTeam = new UserTeam(name, tag, user);

        Errors errors = new BeanPropertyBindingResult(userTeam, "userTeam");
        validator.validate(userTeam, errors);

        if (errors.hasErrors())
        {
            return new ResponseEntity(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        session.save(userTeam);
        session.flush();
        session.refresh(userTeam);

        if (multipartFile != null)
            UserTeamService.changeTeamPicture(userTeam, multipartFile.getInputStream());

        return new ResponseEntity(userTeam, HttpStatus.OK);
    }

    /**
     * Deletes a team
     * @param session (Resolved)
     * @param id ID of the team to delete
     * @param teamName Team name confirmation
     * @return Message
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
    public ResponseEntity deleteTeam(SessionImpl session, @AuthedUser User user, @PathVariable("id") int id, @RequestParam("name") String teamName, @RequestParam(value = "newOwner", required = false) Integer newOwner)
    {
        user = (User) session.merge(user);

        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, id);

        if (!teamName.equals(userTeam.getName()))
            return new ResponseEntity("Team name did not match", HttpStatus.BAD_REQUEST);

        if (!UserTeamService.doesUserHaveAuthorization(user, userTeam))
            return new ResponseEntity("You are not allowed to do this", HttpStatus.FORBIDDEN);

        if (newOwner != null)
        {
            User newOwnerUser = (User) session.get(User.class, newOwner);

            userTeam.setOwner(newOwnerUser);
        } else userTeam.setOwner(null);

        user.setTeam(null);

        return new ResponseEntity(userTeam, HttpStatus.OK);
    }

    /**
     * Kick a player from a team
     * @param session (Resolved)
     * @param id ID Of the team to kick from
     * @param userID User to kick
     * @return Response
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/{id}/kick/{user}")
    public ResponseEntity kickUser(SessionImpl session,
                                   @AuthedUser User authedUser,
                                   @PathVariable("id") int id,
                                   @PathVariable("user") int userID)
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, id);

        if (userTeam == null)
            return new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);

        if(!UserTeamService.doesUserHaveAuthorization(authedUser, userTeam))
            return new ResponseEntity("You're not allowed to do that", HttpStatus.FORBIDDEN);

        userTeam.getMembers().removeIf(
                user -> user.getId() == userID);

        return new ResponseEntity("Successfully kicked player", HttpStatus.OK);
    }

    /**
     * Leave the team (Remove yourself from the member list)
     * @param session (Resolved)
     * @param user (Resolved)
     * @param id The ID of the team to leave
     * @return Response
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.USER)
    @RequestMapping("/{id}/leave")
    public ResponseEntity leaveTeam(SessionImpl session,
                                    @AuthedUser User user,
                                    @PathVariable("id") int id)
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, id);

        if (userTeam == null)
            return new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);

        userTeam.getMembers().removeIf(currentUser ->
                currentUser.getId() == user.getId());

        return new ResponseEntity("Successfully left team", HttpStatus.OK);
    }

    /**
     * Change the name of a team
     * @param session (Resolved)
     * @param user (Resolved)
     * @param id The ID of the team
     * @param newName The new name of the team
     * @return Message
     */
    @PreAuthorization(minRole = User.Role.USER)
    @Transactional
    @RequestMapping("/{id}/changename")
    public ResponseEntity editTeamName(SessionImpl session, @AuthedUser User user, @PathVariable("id") int id, @RequestParam("newName") String newName)
    {

        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, id);

        if (userTeam == null)
            return new ResponseEntity("That team was not found", HttpStatus.NOT_FOUND);

        if (!UserTeamService.doesUserHaveAuthorization(user, userTeam))
            return new ResponseEntity("You are not allowed to do that", HttpStatus.FORBIDDEN);

        userTeam.setName(newName);

        return new ResponseEntity("Successfully changed team name", HttpStatus.OK);
    }

    /**
     * Invites a player to a roster
     * @param session (Resolved)
     * @param teamID ID of the team to invite player to
     * @param user (Resolved)
     * @param inviteUser The ID of the user to invite
     * @return Message
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/{id}/invite")
    public ResponseEntity invitePlayer(SessionImpl session, @PathVariable("id") int teamID, @AuthedUser User user, @RequestParam("user") int inviteUser)
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, teamID);
        User invitedUser = (User) session.get(User.class, inviteUser);

        if (userTeam == null)
            return new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);

        if (userTeam.getMembers().size() >= 5)
            return new ResponseEntity("This team has too many players", HttpStatus.CONFLICT);

        if (!UserTeamService.doesUserHaveAuthorization(user, userTeam))
            return new ResponseEntity("You are not allowed to do that", HttpStatus.FORBIDDEN);

        if (invitedUser == null)
            return new ResponseEntity("User does not exist", HttpStatus.NOT_FOUND);

        if (invitedUser.getWarrior() == null)
            return new ResponseEntity("You cannot invite non warriors", HttpStatus.CONFLICT);

        if (UserTeamService.inviteUserToTeam(invitedUser, userTeam))
        {
            Activity activity = new Activity(invitedUser, user, "You were invited to the team : " + userTeam.getName(), 0, 0);
            Notification notification = new Notification(invitedUser, "You were invited to the team : " + userTeam.getName(), false);

            session.save(activity);
            session.save(notification);

            return new ResponseEntity("Successfully Invited User", HttpStatus.OK);
        } else
        {
            return new ResponseEntity("User already has an invite", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Accept an invite to a team
     * @param teamID ID of the team to accept invite from
     * @param session (Resolved)
     * @param user (Resolved)
     * @return Message
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.PENDING)
    @RequestMapping("/{id}/invite/accept")
    public ResponseEntity acceptInvite(@PathVariable("id") int teamID, SessionImpl session, @AuthedUser User authedUser)
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, teamID);
        User user = (User) session.merge(authedUser);

        if (userTeam == null)
        {
            return new ResponseEntity("Team not found", HttpStatus.NOT_FOUND);
        }

        Optional<UserTeamInvite> removed = userTeam.getInvites().stream()
                .filter(invite -> invite.getUser().getId() == user.getId())
                .findFirst();

        if (removed.isPresent())
        {
            session.delete(removed.get());

            userTeam.getMembers().stream()
                    .forEach(currentUser -> {
                        Activity activity = new Activity(currentUser, user, user.getUsername() + " joined your team : " + userTeam.getName(), 0, 0);
                        Notification notification = new Notification(currentUser, user.getUsername() + " joined your team : " + userTeam.getName(), false);

                        session.save(activity);
                        session.save(notification);
                    });

            user.setTeam(userTeam);

            Activity activity = new Activity(user, user, "You joined team : " + userTeam.getName(), 0, 0);

            session.save(activity);

            return new ResponseEntity(userTeam, HttpStatus.OK);
        } else
        {
            return new ResponseEntity("You were not invited to that team", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gets the game history of a team
     * @param session (Resolved)
     * @param id ID of the team
     * @param page Page offset
     * @param count Number of results
     * @return history
     */
    @UnitOfWork
    @RequestMapping("/{id}/history")
    public ResponseEntity getHistory(SessionImpl session,
                                     @PathVariable("id") int id,
                                     @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                     @RequestParam(value = "count", defaultValue = "8", required = false) int count)
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, id);

        if (userTeam != null)
        {
            page = page < 1 ? 1 : page;
            count = count < 1 || count > 8 ? 8 : count;

            List<Game> games = UserTeamService.getGamesForUserTeam(userTeam, page, count);

            return new ResponseEntity(games, HttpStatus.OK);
        }

        return new ResponseEntity("That team was not found", HttpStatus.NOT_FOUND);
    }

    /**
     * Statistics of a team
     * @param session (Resolved)
     * @param id ID of the team
     * @return Statistics
     */
    @UnitOfWork
    @RequestMapping("/{id}/statistics")
    public ResponseEntity getStatistics(SessionImpl session, @PathVariable("id") int id)
    {
        UserTeam userTeam = (UserTeam) session.get(UserTeam.class, id);

        if (userTeam != null)
        {
            return new ResponseEntity(UserTeamService.getStatisticsForUserTeam(userTeam), HttpStatus.OK);
        }

        return new ResponseEntity("That team was not found", HttpStatus.NOT_FOUND);
    }
}
