package com.bezman.service;

import com.bezman.hibernate.expression.SubstringCriterion;
import com.bezman.init.DatabaseManager;
import com.bezman.model.BlogPost;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Created by Terence on 4/11/2015.
 */
public class BlogService
{

    public static List<BlogPost> allPosts()
    {
        List<BlogPost> allPosts = null;

        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from BlogPost order by timestamp desc");

        allPosts = query.list();

        session.close();

        return allPosts;
    }

    public static List<BlogPost> getPosts(int limit, int offset)
    {
        Session session = DatabaseManager.getSession();
        Query query = session.createQuery("from BlogPost order by timestamp desc");

        query.setMaxResults(limit);
        query.setFirstResult(offset);

        List<BlogPost> returnList = query.list();

        session.close();

        return returnList;
    }

    public static BlogPost getPost(int id)
    {
        BlogPost post = null;

        Session session = DatabaseManager.getSession();

        post = (BlogPost) session.get(BlogPost.class, id);

        session.close();

        return post;
    }

    public static BlogPost getPostByTitle(String title)
    {
        Session session = DatabaseManager.getSession();

        BlogPost blogPost = (BlogPost) session.createCriteria(BlogPost.class)
                .add(Restrictions.eq("title", title.replace("-", " ")))
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return blogPost;
    }

    public static BlogPost getPostByShortTitle(String title) {
        Session session = DatabaseManager.getSession();

        BlogPost blogPost = (BlogPost) session.createCriteria(BlogPost.class)
                .add(new SubstringCriterion("title", 1, title.length(), title.replace('-', ' ')))
                .setMaxResults(1)
                .uniqueResult();

        session.close();

        return blogPost;
    }
}
