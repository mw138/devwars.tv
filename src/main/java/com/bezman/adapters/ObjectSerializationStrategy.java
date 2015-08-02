package com.bezman.adapters;

import com.bezman.Reference.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Terence on 8/1/2015.
 */
public class ObjectSerializationStrategy implements JsonSerializer<Object>
{
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject jsonObject = (JsonObject) Reference.gson.toJsonTree(o);

        System.out.println(jsonObject);

        return jsonObject;
    }
}
