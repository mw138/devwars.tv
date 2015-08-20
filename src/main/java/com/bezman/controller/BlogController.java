package com.bezman.controller;

import com.bezman.annotation.*;
import com.bezman.hibernate.expression.DayCriterion;
import com.bezman.hibernate.expression.MonthCriterion;
import com.bezman.hibernate.expression.YearCriterion;
import com.bezman.model.BlogPost;
import com.bezman.model.User;
import com.bezman.service.BlogService;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.internal.SessionImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Terence on 4/11/2015.
 */
@Controller
@RequestMapping("/v1/blog")
public class BlogController
{

    /**
     * Gets blog posts
     * @param session
     * @param year (Optional) Year of blog posts wanted
     * @param month (Optional) Month of blog posts wanted
     * @param day (Optional) Day of blog posts wanted
     * @return Blog posts which match criteria
     */
    @UnitOfWork
    @RequestMapping("/all")
    public ResponseEntity allPosts(SessionImpl session, @RequestParam(value = "year", required = false) Integer year, @RequestParam(value = "month", required = false) Integer month, @RequestParam(value = "day", required = false) Integer day)
    {
        Criteria criteria = session.createCriteria(BlogPost.class)
                .setMaxResults(10)
                .addOrder(Order.desc("timestamp"));

        if (year != null) {
            criteria.add(new YearCriterion("timestamp", year));
        }

        if (month != null) {
            criteria.add(new MonthCriterion("timestamp", month));
        }

        if (day != null) {
            criteria.add(new DayCriterion("timestamp", day));
        }

        return new ResponseEntity(criteria.list(), HttpStatus.OK);
    }

    /**
     * Created new blog post
     * @param blogPost JSON of the new post
     * @param user
     * @param session
     * @return
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.BLOGGER)
    @RequestMapping("/create")
    public ResponseEntity createBlog(@JSONParam("post") BlogPost blogPost,
                                     @AuthedUser User user,
                                     SessionImpl session)
    {
        blogPost.setUser(user);

        session.save(blogPost);

        return new ResponseEntity(blogPost, HttpStatus.OK);
    }

    /**
     * @param session
     * @param id of requested blog post
     * @return The requested blog post
     */
    @UnitOfWork
    @RequestMapping("/{id}")
    public ResponseEntity getBlog(SessionImpl session, @PathVariable("id") int id)
    {
        BlogPost blogPost = BlogService.getPost(id);

        if (blogPost != null)
        {
            return new ResponseEntity(blogPost, HttpStatus.OK);
        } else
        {
            return new ResponseEntity("No post found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates a blog posts with given information
     * @param session
     * @param id The ID of the blog post to update
     * @param blogPost JSON of post to update with
     * @return The new blog post
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.BLOGGER)
    @RequestMapping("/{id}/update")
    public ResponseEntity updateBlog(SessionImpl session,
                                     @PathVariable("id") int id,
                                     @JSONParam("post") BlogPost blogPost)
    {
        BlogPost oldPost = (BlogPost) session.get(BlogPost.class, id);

        if (oldPost != null)
        {
            blogPost.setId(id);

            session.merge(blogPost);

            return new ResponseEntity(blogPost, HttpStatus.OK);
        } else
        {
            return new ResponseEntity("No post found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes the blog post
     * @param session
     * @param id The ID of the blog post to delete
     * @return The deleted blog post
     */
    @Transactional
    @PreAuthorization(minRole = User.Role.BLOGGER)
    @RequestMapping("/{id}/delete")
    public ResponseEntity deleteBlog(SessionImpl session, @PathVariable("id") int id)
    {
        BlogPost blogPost = (BlogPost) session.get(BlogPost.class, id);

        if (blogPost != null)
        {
            session.delete(blogPost);

            return new ResponseEntity(blogPost, HttpStatus.OK);
        } else
        {
            return new ResponseEntity("No post found", HttpStatus.NOT_FOUND);
        }
    }
}
