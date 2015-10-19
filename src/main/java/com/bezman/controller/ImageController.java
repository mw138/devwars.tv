package com.bezman.controller;

import com.bezman.Reference.Reference;
import com.bezman.service.Security;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.coobird.thumbnailator.Thumbnailator;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;

@RequestMapping("/v1/image")
@Controller
public class ImageController
{

    @RequestMapping("/thumbnail")
    public void thumbnailImage(@RequestParam("url") String url, HttpServletResponse response) throws UnirestException, IOException
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(Reference.CROPPED_IMAGE_CACHE_PATH + File.separator + Security.hash(url)));

            IOUtils.copy(fileInputStream, response.getOutputStream());

            fileInputStream.close();
        } catch (FileNotFoundException ex)
        {
            InputStream inputStream = Unirest.get(url).asBinary().getBody();

            FileOutputStream fileOutputStream = new FileOutputStream(new File(Reference.CROPPED_IMAGE_CACHE_PATH + File.separator + Security.hash(url)));

            Thumbnailator.createThumbnail(inputStream, fileOutputStream, 200, 200);
            inputStream.reset();
            Thumbnailator.createThumbnail(inputStream, response.getOutputStream(), 200, 200);

                    fileOutputStream.flush();
            fileOutputStream.close();
        }
    }

}
