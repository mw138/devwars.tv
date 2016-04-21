package com.bezman.service;

import com.bezman.Reference.util.Util;
import com.bezman.init.DatabaseManager;
import com.bezman.model.PodcastEpisode;
import com.bezman.storage.FileStorage;
import com.dropbox.core.DbxException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PodcastEpisodeService {

    @Autowired
    FileStorage fileStorage;

    public String uploadPodcastForEpisode(PodcastEpisode episode, MultipartFile multipartFile) throws Exception {
        if (multipartFile != null) {
            String fileName = Util.randomText(32) + ".mp3";
            String path = fileStorage.PODCAST_AUDIO_STORAGE_PATH + "/" + episode.getEpisodeNumber() + "/" + fileName;
            fileStorage.uploadFile(path, multipartFile.getInputStream());

            return fileStorage.shareableUrlForPath(path);
        }

        return null;
    }

    public void saveEpisode(PodcastEpisode podcastEpisode, MultipartFile multipartFile) throws Exception {
        podcastEpisode.setPodcastAudioURL(this.uploadPodcastForEpisode(podcastEpisode, multipartFile));

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        session.save(podcastEpisode);

        session.getTransaction().commit();
        session.close();
    }

    public void editEpisode(PodcastEpisode podcastEpisode, MultipartFile file) throws Exception {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        podcastEpisode = (PodcastEpisode) session.merge(podcastEpisode);
        podcastEpisode.setPodcastAudioURL(this.uploadPodcastForEpisode(podcastEpisode, file));

        session.getTransaction().commit();
        session.close();
    }

    public void deleteEpisode(PodcastEpisode podcastEpisode) {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        session.delete(podcastEpisode);

        session.getTransaction().commit();
        session.close();
    }

}
