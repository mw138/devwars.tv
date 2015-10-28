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

}
