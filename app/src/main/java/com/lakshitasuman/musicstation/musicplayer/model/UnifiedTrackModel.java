package com.lakshitasuman.musicstation.musicplayer.model;

public class UnifiedTrackModel {
    LocalTrackModel localTrack;
    TrackModel streamTrack;
    boolean type;

    public UnifiedTrackModel(boolean type, LocalTrackModel localTrack, TrackModel streamTrack) {
        this.type = type;
        this.localTrack = localTrack;
        this.streamTrack = streamTrack;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public LocalTrackModel getLocalTrack() {
        return localTrack;
    }

    public void setLocalTrack(LocalTrackModel localTrack) {
        this.localTrack = localTrack;
    }

    public TrackModel getStreamTrack() {
        return streamTrack;
    }

    public void setStreamTrack(TrackModel streamTrack) {
        this.streamTrack = streamTrack;
    }
}
