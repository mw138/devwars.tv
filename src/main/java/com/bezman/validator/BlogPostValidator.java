package com.bezman.validator;

import com.bezman.model.BlogPost;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Component
public class BlogPostValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return BlogPost.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BlogPost blogPost = (BlogPost) target;

        if (blogPost.getTitle().contains("-")) {
            errors.rejectValue("title", "field.invalid.char", "The title may not contain the '-' key.");
        }
    }
}
