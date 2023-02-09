package com.lakshitasuman.musicstation.ringtone.model;

public class Songs {
    private String path;
    private String title;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    private String artist;

    public Songs(String path, String title,String artist) {
        this.path = path;
        this.title = title;
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }
}
