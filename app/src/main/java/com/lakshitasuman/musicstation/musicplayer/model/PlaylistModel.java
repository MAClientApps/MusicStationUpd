package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

public class PlaylistModel {

    private String playlistName;
    private List<UnifiedTrackModel> songList;
    public PlaylistModel(String playlistName) {
        this.playlistName = playlistName;
         songList = new ArrayList();
    }
    public PlaylistModel(List<UnifiedTrackModel> songList, String playlistName) {
        this.songList = songList;
        this.playlistName = playlistName;
    }
    public List<UnifiedTrackModel> getSongList() {
        return songList;
    }

    public void setSongList(List<UnifiedTrackModel> songList) {
        this.songList = songList;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void addSong(UnifiedTrackModel unifiedTrackModel) {
        songList.add(unifiedTrackModel);
    }
}
