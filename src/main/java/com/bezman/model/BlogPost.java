package com.bezman.model;

import com.bezman.annotation.HibernateDefault;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringEscapeUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
public class BlogPost extends BaseModel
{

    private int id;

    private String title;
    private String description;
    private String text;
    private String image_url;

    private User user;

    @HibernateDefault
    private Timestamp timestamp;

    private Set<String> tags;
}
