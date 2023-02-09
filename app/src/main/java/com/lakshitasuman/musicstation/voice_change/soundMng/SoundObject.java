package com.lakshitasuman.musicstation.voice_change.soundMng;

public class SoundObject {
    public static final int RES_ID_ERROR = -1;
    private String nameResource;
    private int resId = -1;
    private int soundId;

    public SoundObject(int resId, int soundId) {
        this.resId = resId;
        this.soundId = soundId;
    }

    public SoundObject(String str, int i) {
        nameResource = str;
        soundId = i;
    }
    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getNameResource() {
        return nameResource;
    }

    public void setNameResource(String nameResource) {
        this.nameResource = nameResource;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }
}
