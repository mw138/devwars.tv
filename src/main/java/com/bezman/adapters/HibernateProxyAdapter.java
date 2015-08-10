package com.bezman.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;

/**
 * Simple Class to make sure that the hibernate proxies don't get serialized
 */
public class HibernateProxyAdapter extends TypeAdapter<HibernateProxy>
{
    @Override
    public void write(JsonWriter jsonWriter, HibernateProxy hibernateProxy) throws IOException
    {
        jsonWriter.nullValue();
    }

    @Override
    public HibernateProxy read(JsonReader jsonReader) throws IOException
    {
        return null;
    }
}
