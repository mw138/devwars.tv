package com.bezman.controller.game;

import com.bezman.Reference.Reference;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.UnitOfWork;
import com.bezman.controller.BaseController;
import com.bezman.model.Team;
import com.bezman.model.User;
import com.bezman.storage.FileStorage;
import com.dropbox.core.DbxException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

/**
 * Created by Terence on 8/5/2015.
 */
@Controller
@RequestMapping("/v1/team")
public class TeamController extends BaseController
{
    private static String[] ignored = {".c9"};

    @Autowired
    FileStorage fileStorage;

    /**
     * Upload for the teams data straight from cloud nine
     * @param session
     * @param id ID of team to set
     * @param zipFile tar.gz file from Cloud Nine
     * @return
     * @throws IOException
     */
    @PreAuthorization(minRole = User.Role.ADMIN)
    @UnitOfWork
    @RequestMapping(value = "/{id}/upload", method = RequestMethod.POST)
    public ResponseEntity uploadSite(SessionImpl session, @PathVariable("id") int id, @RequestPart("zip") MultipartFile zipFile) throws IOException, DbxException
    {
        Team team = (Team) session.get(Team.class, id);

        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(new GZIPInputStream(zipFile.getInputStream()));

        String destPath = fileStorage.SITE_STORAGE_PATH + "/" + team.getGame().getId() + "/" + team.getName();

        TarArchiveEntry tarArchiveEntry = null;
        while((tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()) != null)
        {
            String fileName = tarArchiveEntry.getName();

            fileName = fileName.split("_")[fileName.split("_").length -1];
            fileName = fileName.replace("workspace", "");

            boolean isIgnored = Arrays.asList(ignored).stream().anyMatch(fileName::contains);

            if (!isIgnored)
            {
                System.out.println(fileName);

                if (!tarArchiveEntry.isDirectory())
                {
                    fileStorage.uploadFile(destPath + "/" + fileName, tarArchiveInputStream);
                }
            }

        }

        return new ResponseEntity("Successfully Uploaded Team's files", HttpStatus.OK);
    }


}
