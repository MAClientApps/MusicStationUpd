package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

public class AllMusicFoldersModel {
    List<MusicFolderModel> musicFolders = new ArrayList();

    public List<MusicFolderModel> getMusicFolders() {
        return musicFolders;
    }

    public void setMusicFolders(List<MusicFolderModel> list) {
        musicFolders = list;
    }
}
