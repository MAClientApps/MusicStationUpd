package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.Comparator;

public class AlbumComparatorModel implements Comparator<AlbumModel> {
    @Override
    public int compare(AlbumModel albumModel, AlbumModel albumModel1) {
        return albumModel.getName().toString().compareTo(albumModel1.getName().toString());
    }
}
