package com.bezman.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by Terence on 8/2/2015.
 */
public class DevWarsObjectMapper extends ObjectMapper
{

    public DevWarsObjectMapper()
    {
        super();

        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

}
