package com.bezman.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by Terence on 1/19/2015.
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
