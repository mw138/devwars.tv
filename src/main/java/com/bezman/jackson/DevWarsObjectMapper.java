package com.bezman.jackson;

import com.bezman.Reference.Reference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Bean for the Jackson Object Mapper
 */
public class DevWarsObjectMapper extends ObjectMapper
{

    public DevWarsObjectMapper()
    {
        super();

        setSerializationInclusion(JsonInclude.Include.NON_NULL);

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Reference.objectMapper = this;
    }

}
