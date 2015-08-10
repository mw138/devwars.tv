package com.bezman.model;

import org.apache.commons.lang.StringEscapeUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

/**
 * Created by Terence on 4/11/2015.
 */
public class BlogPost extends BaseModel
{

    private int id;

    private String title;
    private String description;
    private String text;
    private String image_url;

    private User user;

    private Timestamp timestamp;

    private Set<String> tags;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Timestamp getTimestamp()
    {
        return timestamp == null ? new Timestamp(new Date().getTime()) : timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getText()
    {
        return StringEscapeUtils.unescapeHtml(text);
    }

    public void setText(String text)
    {
        this.text = StringEscapeUtils.unescapeHtml(text);
    }

    public String getImage_url()
    {
        return image_url;
    }

    public void setImage_url(String image_url)
    {
        this.image_url = image_url;
    }

    public Set<String> getTags()
    {
        return tags;
    }

    public void setTags(Set<String> tags)
    {
        this.tags = tags;
    }
}
