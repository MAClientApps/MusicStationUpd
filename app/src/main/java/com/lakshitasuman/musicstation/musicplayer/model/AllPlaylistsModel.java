package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

public class AllPlaylistsModel {
    private List<PlaylistModel> allPlaylists = new ArrayList();

    public List<PlaylistModel> getPlaylists() {
        return allPlaylists;
    }

    public void setPlaylists(List<PlaylistModel> allPlaylists) {
        this.allPlaylists = allPlaylists;
    }

    public void addPlaylist(PlaylistModel playlistModel) {
        this.allPlaylists.add(playlistModel);
    }
}
