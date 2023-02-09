package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

public class MusicFolderModel {
    private String folderName;
    private List<LocalTrackModel> localTracks;

    public MusicFolderModel(String folderName, List<LocalTrackModel> localTracks) {
        this.folderName = folderName;
        this.localTracks = localTracks;
    }

    public MusicFolderModel(String folderName) {
        this.folderName = folderName;
        this.localTracks = new ArrayList();
    }


    public String getFolderName() {
        return this.folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<LocalTrackModel> getLocalTracks() {
        return this.localTracks;
    }

    public void setLocalTracks(List<LocalTrackModel> localTracks) {
        this.localTracks = localTracks;
    }
}
