package com.bezman.controller.user;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.User;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Ref;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Terence on 8/2/2015.
 */
@RequestMapping("/v1/admin")
@Controller
public class AdminController
{

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/photoarchive")
    public ResponseEntity archivePhotos(HttpServletResponse response) throws IOException
    {
        response.setHeader("Content-Disposition","attachment; filename=\"" + "archive.zip" + "\"");
        response.setContentType("application/zip");

        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

        File zipDir = new File(Reference.PROFILE_PICTURE_PATH_NO_END);

        Util.zipFolder(zipDir, zipOutputStream);

        zipOutputStream.close();

        return null;
    }

    @PreAuthorization(minRole = User.Role.ADMIN)
    @RequestMapping("/photoarchivelist")
    public ResponseEntity getArchiveFolderList(HttpServletResponse response) throws IOException
    {
        File zipDir = new File(Reference.PROFILE_PICTURE_PATH_NO_END);
//        File zipDir = new File("C:\\Users\\Terence\\IdeaProjects\\DevWars Maven\\src\\main\\java");

        JSONObject jsonObject = new JSONObject();

        addFolderToJSONObject(zipDir, jsonObject);

        return new ResponseEntity(jsonObject, HttpStatus.OK);
    }

    private void addFolderToJSONObject(File dir, JSONObject jsonObject)
    {
        for(File file : dir.listFiles())
        {
            if (file.isDirectory())
            {
                JSONObject newDir = new JSONObject();

                addFolderToJSONObject(file, newDir);

                jsonObject.put(file.getName(), newDir);
            } else
            {
                jsonObject.put(file.getName(), file.getName());
            }
        }
    }


}
