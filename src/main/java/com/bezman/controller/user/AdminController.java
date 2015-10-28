package com.bezman.controller.user;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.PreAuthorization;
import com.bezman.init.DatabaseManager;
import com.bezman.model.User;
import com.bezman.model.UserTeam;
import com.bezman.service.UserService;
import com.bezman.storage.FileStorage;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.Files;
import org.hibernate.Session;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;

/**
 * Created by Terence on 8/2/2015.
 */
@RequestMapping("/v1/admin")
@Controller
public class AdminController
{
    @Autowired
    FileStorage fileStorage;

    /**
     * Archives all photos
     *
     * @param response
     * @return null (File)
     * @throws IOException
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/photoarchive")
    public String archivePhotos(HttpServletResponse response) throws IOException
    {
        return "redirect:" + fileStorage.shareableUrlForPath(fileStorage.PROFILE_PICTURE_PATH);
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/migratestorage")
    public ResponseEntity migrateStorage() throws DbxException
    {
        ArrayList<Files.Metadata> userPicFolders = fileStorage.getDirMetaDataForPath(fileStorage.PROFILE_PICTURE_PATH);
        ArrayList<Files.Metadata> teamPicFolders = fileStorage.getDirMetaDataForPath(fileStorage.TEAM_PICTURE_PATH);

        userPicFolders.stream().forEach(metadata -> {
            try
            {
                Integer userID = Integer.parseInt(metadata.name);

                Session session = DatabaseManager.getSession();
                session.beginTransaction();

                User user = (User) session.get(User.class, userID);

                if (user != null)
                {
                    System.out.println(user.getUsername() + " was not null");

                    user.setAvatarURL(fileStorage.shareableUrlForPath(fileStorage.PROFILE_PICTURE_PATH + "/" + metadata.name + "/avatar"));
                }

                session.getTransaction().commit();
                session.close();
            } catch (NumberFormatException n){}
        });

        teamPicFolders.stream().forEach(metadata -> {
            try
            {
                Integer teamID = Integer.parseInt(metadata.name);

                Session session = DatabaseManager.getSession();
                session.beginTransaction();

                UserTeam userTeam = (UserTeam) session.get(UserTeam.class, teamID);

                if (userTeam != null)
                {
                    System.out.println(userTeam.getId() + " was not null");

                    userTeam.setAvatarURL(fileStorage.shareableUrlForPath(fileStorage.TEAM_PICTURE_PATH + "/" + metadata.name + "/avatar"));
                }

                session.getTransaction().commit();
                session.close();
            } catch (NumberFormatException n){}
        });

        return new ResponseEntity("OK", HttpStatus.OK);
    }

}
