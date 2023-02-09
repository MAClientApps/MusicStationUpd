package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

public class RecentlyPlayedModel {
    private List<UnifiedTrackModel> recentlyPlayed = new ArrayList();

    public List<UnifiedTrackModel> getRecentlyPlayed() {
        return this.recentlyPlayed;
    }

    public void setRecentlyPlayed(List<UnifiedTrackModel> recentlyPlayed) {
        this.recentlyPlayed = recentlyPlayed;
    }

    public void addSong(UnifiedTrackModel unifiedTrackModel) {
        this.recentlyPlayed.add(unifiedTrackModel);
    }
}
