package com.bezman.model;

import jdk.internal.instrumentation.TypeMapping;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "podcast_episodes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PodcastEpisode extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private Integer episodeNumber;

    @Getter
    @Setter
    private String episodeName, podcastAudioURL, podcastImageURL;

    @Type(type = "text")
    @Getter
    @Setter
    private String episodeDescription, showNotes;
}
