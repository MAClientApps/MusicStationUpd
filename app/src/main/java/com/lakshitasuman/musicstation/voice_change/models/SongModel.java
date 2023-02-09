package com.lakshitasuman.musicstation.voice_change.models;


import org.json.JSONObject;

import java.util.Date;

public class SongModel {
    private Date date;
    private boolean isPlaying;
    private String name;
    private String path;

    public SongModel(String name, Date date, String path) {
        this.name = name;
        this.date = date;
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date2) {
        this.date = date2;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    public JSONObject toJson() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("name", this.name);
            jSONObject.put("data", this.date);
            return jSONObject;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
