package com.lakshitasuman.musicstation.musicplayer.model;

import java.util.ArrayList;
import java.util.List;

public class QueueModel {
    private List<UnifiedTrackModel> queue = new ArrayList();

    public List<UnifiedTrackModel> getQueue() {
        return this.queue;
    }

    public void setQueue(List<UnifiedTrackModel> queue) {
        this.queue = queue;
    }

    public void addToQueue(UnifiedTrackModel unifiedTrackModel) {
        this.queue.add(unifiedTrackModel);
    }

    public void removeItem(UnifiedTrackModel unifiedTrackModel) {
        for (int i = 0; i < this.queue.size(); i++) {
            if (unifiedTrackModel.equals(this.queue.get(i))) {
                this.queue.remove(i);
                return;
            }
        }
    }
}
