package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.List;

public class ArtistModel {
    private String name;
    private List<LocalTrackModel> artistSongs;

    public ArtistModel(String name, List<LocalTrackModel> artistSongs) {
        this.name = name;
        this.artistSongs = artistSongs;
    }

    public String getName() {
        return name;
    }

    public void setName(String str) {
        name = str;
    }

    public List<LocalTrackModel> getArtistSongs() {
        return artistSongs;
    }

    public void setArtistSongs(List<LocalTrackModel> list) {
        artistSongs = list;
    }
}
