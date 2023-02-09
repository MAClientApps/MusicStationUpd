package com.lakshitasuman.musicstation.musicplayer.model;

public class EqualizerModel {
    short bassStrength;
    public boolean isEqualizerEnabled;
    int presetPos;
    short reverbPreset;
    int[] seekbarPos;

    public EqualizerModel() {
        seekbarPos = new int[5];
        reverbPreset = -1;
        bassStrength = -1;
        isEqualizerEnabled = true;
        reverbPreset = -1;
        bassStrength = -1;
    }

    public boolean isEqualizerEnabled() {
        return isEqualizerEnabled;
    }

    public void setEqualizerEnabled(boolean isEqualizerEnabled) {
        this.isEqualizerEnabled = isEqualizerEnabled;
    }

    public int[] getSeekbarPos() {
        return seekbarPos;
    }

    public void setSeekbarpos(int[] seekbarPos) {
        this.seekbarPos = seekbarPos;
    }

    public int getPresetPos() {
        return presetPos;
    }

    public void setPresetPos(int i) {
        presetPos = i;
    }

    public short getReverbPreset() {
        return reverbPreset;
    }

    public void setReverbPreset(short s) {
        reverbPreset = s;
    }

    public short getBassStrength() {
        return bassStrength;
    }

    public void setBassStrength(short s) {
        bassStrength = s;
    }
}
