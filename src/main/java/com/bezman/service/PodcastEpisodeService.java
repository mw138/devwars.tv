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

    public String uploadPodcastForEpisode(PodcastEpisode episode, MultipartFile podcastAudio) throws Exception {
        if (podcastAudio != null) {
            String audioFileName = Util.randomText(32) + ".mp3";
            String audioPath = fileStorage.PODCAST_AUDIO_STORAGE_PATH + "/" + episode.getEpisodeNumber() + "/" + audioFileName;
            fileStorage.uploadFile(audioPath, podcastAudio.getInputStream());

            return fileStorage.shareableUrlForPath(audioPath);
        }

       return null;
    }

    public String uploadImageForEpisode(PodcastEpisode episode, MultipartFile podcastImage) throws IOException, DbxException {
        if (podcastImage != null) {
            String imageFileName = Util.randomText(32) + ".png";
            String imagePath = fileStorage.PODCAST_AUDIO_STORAGE_PATH + "/" + episode.getEpisodeNumber() + "/" + imageFileName;
            fileStorage.uploadFile(imagePath, podcastImage.getInputStream());

            return fileStorage.shareableUrlForPath(imagePath);
        }

        return null;
    }


    public void saveEpisode(PodcastEpisode podcastEpisode, MultipartFile podcastAudio, MultipartFile podcastImage) throws Exception {
        podcastEpisode.setPodcastAudioURL(this.uploadPodcastForEpisode(podcastEpisode, podcastAudio));
        podcastEpisode.setPodcastImageURL(this.uploadImageForEpisode(podcastEpisode, podcastImage));

        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        session.save(podcastEpisode);

        session.getTransaction().commit();
        session.close();
    }

    public void editEpisode(PodcastEpisode podcastEpisode, MultipartFile podcastAudio, MultipartFile podcastImage) throws Exception {
        Session session = DatabaseManager.getSession();
        session.beginTransaction();

        podcastEpisode = (PodcastEpisode) session.merge(podcastEpisode);
        podcastEpisode.setPodcastAudioURL(this.uploadPodcastForEpisode(podcastEpisode, podcastAudio));
        podcastEpisode.setPodcastImageURL(this.uploadImageForEpisode(podcastEpisode, podcastImage));

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
