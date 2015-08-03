package com.bezman.controller.user;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.User;
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

        File zipDir = new File(Reference.PROFILE_PICTURE_PATH);

        Util.zipFolder(zipDir, zipOutputStream);

        zipOutputStream.close();

        return null;
    }


}
