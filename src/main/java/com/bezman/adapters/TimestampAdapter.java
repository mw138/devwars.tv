package com.bezman.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Adapter to make sure timestamps get adapted to SQL Timestamps
 */
public class TimestampAdapter extends TypeAdapter<Timestamp>
{

    @Override
    public void write(JsonWriter jsonWriter, Timestamp timestamp) throws IOException
    {
        jsonWriter.value(timestamp.getTime());
    }

    @Override
    public Timestamp read(JsonReader jsonReader) throws IOException
    {
        return new Timestamp(jsonReader.nextLong());
    }
}
