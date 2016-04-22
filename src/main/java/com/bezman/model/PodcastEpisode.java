package com.bezman.model;

import com.bezman.Reference.Reference;
import com.bezman.annotation.HibernateDefault;
import com.sun.syndication.feed.rss.Enclosure;
import com.sun.syndication.feed.synd.*;
import jdk.internal.instrumentation.TypeMapping;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    @HibernateDefault
    public Date createdAt;

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

    public String getSlug() {
        return "";
    }

    public SyndEntry getSyndEntry() {
        SyndEntry entry = new SyndEntryImpl();
        SyndContent content = new SyndContentImpl();

        List enclosureList = new LinkedList<>();
        SyndEnclosure enclosure = new SyndEnclosureImpl();
        enclosure.setType("audio/mpeg");
        enclosure.setUrl(this.getPodcastAudioURL());
        enclosureList.add(enclosure);


        entry.setAuthor("DevWars LLC");
        entry.setTitle(this.getEpisodeName());
        entry.setLink(Reference.rootURL + "/podcast/" + this.getSlug());
        entry.setPublishedDate(this.createdAt);
        entry.setEnclosures(enclosureList);

        content.setValue(this.getEpisodeDescription());
        entry.setDescription(content);

        return entry;
    }
}
