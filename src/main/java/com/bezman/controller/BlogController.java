package com.bezman.controller;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.annotation.*;
import com.bezman.model.BlogPost;
import com.bezman.model.User;
import com.bezman.service.BlogService;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.util.List;

/**
 * Created by Terence on 4/11/2015.
 */
@Controller
@RequestMapping("/v1/blog")
public class BlogController
{

    @UnitOfWork
    @RequestMapping("/all")
    public ResponseEntity allPosts(SessionImpl session)
    {
        List<BlogPost> allPosts = BlogService.getPosts(10, 0);

        return new ResponseEntity(allPosts, HttpStatus.OK);
    }

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
