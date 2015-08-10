package com.bezman.adapters;

import com.bezman.Reference.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 *
 */
public class ObjectSerializationStrategy implements JsonSerializer<Object>
{
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject jsonObject = (JsonObject) Reference.gson.toJsonTree(o);

        return jsonObject;
    }
}
