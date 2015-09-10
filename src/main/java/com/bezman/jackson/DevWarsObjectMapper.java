package com.bezman.jackson;

import com.bezman.Reference.Reference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        Reference.objectMapper = this;
    }

}
