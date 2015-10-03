package com.bezman.validator;

import com.bezman.model.BlogPost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by teren on 8/25/2015.
 */
@Configuration
public class BlogPostValidator implements Validator{

    @Override
    public boolean supports(Class<?> clazz) {
        return BlogPost.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BlogPost blogPost = (BlogPost) target;

        if(blogPost.getTitle().contains("-"))
        {
            errors.rejectValue("title", "field.invalid.char", "The title may not contain the '-' key.");
        }
    }
}
