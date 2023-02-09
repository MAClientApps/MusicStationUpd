package com.lakshitasuman.musicstation.ringtone.model;

public class SongList {
    String name;
    String path;

    public SongList(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public SongList() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }
}
