package com.lakshitasuman.musicstation.musicplayer.model;

public class TrackModel {
    private String mArtworkURL;
    private int mDuration;
    private int mID;
    private String mStreamURL;
    private String mTitle;

    public String getTitle() {
        return this.mTitle;
    }

    public int getID() {
        return this.mID;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public String getStreamURL() {
        return this.mStreamURL;
    }

    public String getArtworkURL() {
        return this.mArtworkURL;
    }
}
