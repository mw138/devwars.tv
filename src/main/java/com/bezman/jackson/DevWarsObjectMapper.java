package com.bezman.jackson;

import com.bezman.Reference.Reference;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Bean for the Jackson Object Mapper
 */
@Component
public class DevWarsObjectMapper extends ObjectMapper {

    public DevWarsObjectMapper() {
        super();

        setSerializationInclusion(JsonInclude.Include.NON_NULL);

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        setVisibilityChecker(getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        Reference.objectMapper = this;
    }

}
