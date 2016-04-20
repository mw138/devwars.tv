package com.bezman.controller.podcast;

import com.bezman.annotation.JSONParam;
import com.bezman.annotation.PathModel;
import com.bezman.annotation.PreAuthorization;
import com.bezman.model.PodcastEpisode;
import com.bezman.model.User;
import com.bezman.service.PodcastEpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/v1/podcast")
public class PodcastController {

    @Autowired
    PodcastEpisodeService podcastEpisodeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getEpisode(@PathModel("id") PodcastEpisode podcastEpisode) {
        if (podcastEpisode != null) {
            return new ResponseEntity(podcastEpisode, HttpStatus.OK);
        } else {
            return new ResponseEntity("Not Found.", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @PreAuthorization(minRole = User.Role.BLOGGER)
    public ResponseEntity createEpisode(@JSONParam("episode") PodcastEpisode podcastEpisode, @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        podcastEpisodeService.saveEpisode(podcastEpisode, file);

        return new ResponseEntity(podcastEpisode, HttpStatus.OK);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @PreAuthorization(minRole = User.Role.BLOGGER)
    public ResponseEntity editEpisode(@JSONParam("episode") PodcastEpisode podcastEpisode, @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws Exception {
        podcastEpisodeService.editEpisode(podcastEpisode, multipartFile);

        return new ResponseEntity(podcastEpisode, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @PreAuthorization(minRole = User.Role.BLOGGER)
    public ResponseEntity deleteEpisode(@PathModel("id") PodcastEpisode podcastEpisode) {
        if (podcastEpisode != null) {
            podcastEpisodeService.deleteEpisode(podcastEpisode);
            return new ResponseEntity(podcastEpisode, HttpStatus.OK);
        } else {
            return new ResponseEntity("Not Found.", HttpStatus.NOT_FOUND);
        }
    }

}
