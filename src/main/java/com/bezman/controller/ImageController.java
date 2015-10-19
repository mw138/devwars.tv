package com.bezman.controller;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

@RequestMapping("/v1/image")
@Controller
public class ImageController
{

    @RequestMapping("/thumbnail")
    public void thumbnailImage(@RequestParam("url") String url, HttpServletResponse response) throws UnirestException, IOException
    {
        InputStream inputStream = Unirest.get(url).asBinary().getBody();

        Thumbnailator.createThumbnail(inputStream, response.getOutputStream(), 200, 200);
    }

}
