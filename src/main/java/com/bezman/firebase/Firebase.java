package com.bezman.firebase;

import com.bezman.Reference.Reference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Firebase {
    private final String token;
    private final String url;

    public Firebase(String url, String token) {
        this.token = token;
        this.url = url;
    }

    public void patchValueAtPath(String path, String value) {
        try {
            String response = Unirest.patch(this.url + path + ".json")
                .queryString("auth", this.token)
                .body(value)
                .asString()
                .getBody();

            System.out.println(response);

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void setValueAtPath(String path, String value) {
        try {
            Unirest.put(this.url + path + ".json")
                .queryString("auth", this.token)
                .body(value)
                .asString()
                .getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public String getValueAtPath(String path) {
        try {
            return Unirest.get(this.url + path + ".json")
                .queryString("auth", this.token)
                .asString()
                .getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

}
