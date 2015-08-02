package com.bezman.jackson.serializer;


import com.bezman.Reference.Reference;
import com.bezman.annotation.UserPermissionFilter;
import com.bezman.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Terence on 8/2/2015.
 */
public class UserPermissionSerializer extends JsonSerializer<User> implements ContextualSerializer
{

    String userFieldName;

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {
        ArrayList<Field> fields = new ArrayList<Field>(Arrays.asList(user.getClass().getDeclaredFields()));

        jsonGenerator.writeStartObject();

        fields.forEach(field -> {
            try
            {
                JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
                field.setAccessible(true);

                if (jsonIgnore == null && field.get(user) != null)
                {
                    jsonGenerator.writeObjectField(field.getName(), field.get(user));
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        });

        jsonGenerator.writeEndObject();
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException
    {
        return this;
    }
}
