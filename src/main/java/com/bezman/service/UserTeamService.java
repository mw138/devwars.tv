package com.bezman.service;

import com.bezman.model.Team;
import com.bezman.model.User;
import com.bezman.model.UserTeam;
import org.springframework.stereotype.Service;

/**
 * Created by teren on 9/16/2015.
 */
@Service
public class UserTeamService
{

    public static boolean isUserInvitedToTeam(UserTeam team, User user)
    {
        return team.getInvites().stream()
                .anyMatch(current -> current.getId() == user.getId());
    }

    public static boolean inviteUserToTeam(User user, UserTeam userTeam)
    {
        for(User currentUser : userTeam.getMembers())
        {
            if(currentUser.getId() == user.getId()) return false;
        }

        for(User currentUser : userTeam.getInvites())
        {
            if(currentUser.getId() == user.getId()) return false;
        }

        userTeam.getInvites().add(user);

        return true;
    }

}
