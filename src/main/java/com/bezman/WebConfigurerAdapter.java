package com.bezman;

import com.bezman.interceptor.*;
import com.bezman.resolver.JSONParamResolver;
import com.bezman.resolver.PathModelResolver;
import com.bezman.resolver.SessionResolver;
import com.bezman.resolver.UserResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfigurerAdapter extends WebMvcConfigurerAdapter {

    @Autowired PreAuthInterceptor preAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AllowCrossOriginInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new DefaultCacheHeaderInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(preAuthInterceptor).addPathPatterns("/**");
        registry.addInterceptor(new TransactionalInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new UnitOfWorkInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new JSONParamResolver());
        argumentResolvers.add(new PathModelResolver());
        argumentResolvers.add(new SessionResolver());
        argumentResolvers.add(new UserResolver());
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
