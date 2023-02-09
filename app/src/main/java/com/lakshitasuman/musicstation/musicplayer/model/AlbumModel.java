package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.List;

public class AlbumModel {
    private String name;
    private List<LocalTrackModel> albumSongs;

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    private Long albumId;

    public AlbumModel(String name, List<LocalTrackModel> albumSongs,Long albumId) {
       this.name = name;
       this.albumSongs = albumSongs;
       this.albumId=albumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public List<LocalTrackModel> getAlbumSongs() {
        return albumSongs;
    }

    public void setAlbumSongs(List<LocalTrackModel> list) {
       albumSongs = list;
    }
}
