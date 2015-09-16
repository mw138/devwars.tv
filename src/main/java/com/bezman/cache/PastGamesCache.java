package com.bezman.cache;

import com.bezman.service.GameService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by teren on 9/14/2015.
 */
@Configuration
public class PastGamesCache extends CacheLoader<String, HashMap>
{
    @Override
    public HashMap load(String s) throws Exception
    {
        Integer queryCount = Integer.valueOf(s.split(":")[0]);
        Integer queryOffset = Integer.valueOf(s.split(":")[1]);

        return GameService.pastGames(queryCount, queryOffset);
    }

    @Bean(name = "pastGamesLoadingCache")
    public LoadingCache<String, HashMap> pastGamesCache()
    {
        return CacheBuilder.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build(new PastGamesCache());
    }
}
