package com.bezman.controller.user;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.User;
import com.bezman.storage.FileStorage;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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
