package com.bezman.controller;

import com.bezman.Reference.DatabaseManager;
import com.bezman.Reference.Reference;
import com.bezman.Reference.util.DatabaseUtil;
import com.bezman.annotation.AuthedUser;
import com.bezman.annotation.PreAuthorization;
import com.bezman.annotation.Transactional;
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

    @RequestMapping("/all")
    public ResponseEntity allPosts(HttpServletRequest request, HttpServletResponse response)
    {
        List<BlogPost> allPosts = BlogService.getPosts(10, 0);

        return new ResponseEntity(allPosts, HttpStatus.OK);
    }

    @Transactional
    @PreAuthorization(minRole = User.Role.BLOGGER)
    @RequestMapping("/create")
    public ResponseEntity createBlog(@RequestParam("title") String title,
                                     @RequestParam("description") String description,
                                     @RequestParam("text") String text,
                                     @RequestParam("image_url") String image_url,
                                     @AuthedUser User user,
                                     SessionImpl session)
    {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(title);
        blogPost.setDescription(description);
        blogPost.setUser(user);
        blogPost.setText(text);
        blogPost.setImage_url(image_url);

        session.save(blogPost);

        return new ResponseEntity(blogPost, HttpStatus.OK);
    }

    @RequestMapping("/{id}")
    public ResponseEntity getBlog(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id)
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

    @PreAuthorization(minRole = User.Role.BLOGGER)
    @RequestMapping("/{id}/update")
    public ResponseEntity updateBlog(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id,
                                     @RequestParam("title") String title,
                                     @RequestParam("description") String description,
                                     @RequestParam("text") String text,
                                     @RequestParam("image_url") String image_url)
    {
        BlogPost blogPost = BlogService.getPost(id);

        if (blogPost != null)
        {
            blogPost.setTitle(title);
            blogPost.setDescription(description);
            blogPost.setText(text);
            blogPost.setImage_url(image_url);

            DatabaseUtil.saveOrUpdateObjects(true, blogPost);

            return new ResponseEntity(blogPost, HttpStatus.OK);
        } else
        {
            return new ResponseEntity("No post found", HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorization(minRole = User.Role.BLOGGER)
    @RequestMapping("/{id}/delete")
    public ResponseEntity deleteBlog(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id)
    {
        BlogPost blogPost = BlogService.getPost(id);

        if (blogPost != null)
        {
            DatabaseUtil.deleteObjects(blogPost);

            return new ResponseEntity(blogPost, HttpStatus.OK);
        } else
        {
            return new ResponseEntity("No post found", HttpStatus.NOT_FOUND);
        }
    }
}
