package com.bezman.cache;

import com.bezman.service.GameService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by teren on 9/14/2015.
 */
@Component
public class PastGamesCache extends CacheLoader<String, HashMap>
{
    @Autowired
    GameService gameService;

    @Override
    public HashMap load(String s) throws Exception
    {
        System.out.println(gameService);

        Integer queryCount = Integer.valueOf(s.split(":")[0]);
        Integer queryOffset = Integer.valueOf(s.split(":")[1]);

        return gameService.pastGames(queryCount, queryOffset);
    }

    @Bean(name = "pastGamesLoadingCache")
    public LoadingCache<String, HashMap> pastGamesCache(PastGamesCache pastGamesCache)
    {
        return CacheBuilder.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build(pastGamesCache);
    }
}
