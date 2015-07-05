package com.bezman.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;

/**
 * Created by Terence on 1/20/2015.
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
