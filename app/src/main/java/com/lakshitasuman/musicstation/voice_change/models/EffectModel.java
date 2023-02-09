package com.lakshitasuman.musicstation.voice_change.models;

public class EffectModel {
    private float[] eq;
    private String id;
    private boolean isEcho;
    private boolean isFlanger;
    private boolean isPlaying;
    private boolean isReverse;
    private String name;
    private String imagePath;
    private int pitch;
    private float rate;
    private float[] reverb;

    public EffectModel(String str, String str2) {
        this.id = str;
        this.name = str2;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public EffectModel(String str, String str2, String imagePath, int i, float f) {
        this.id = str;
        this.name = str2;
        this.imagePath = imagePath;
        this.pitch = i;
        this.rate = f;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setPlaying(boolean b) {
        this.isPlaying = b;
    }

    public int getPitch() {
        return this.pitch;
    }

    public void setPitch(int i) {
        this.pitch = i;
    }

    public float getRate() {
        return this.rate;
    }

    public void setRate(float f) {
        this.rate = f;
    }

    public float[] getReverb() {
        return this.reverb;
    }

    public void setReverb(float[] fArr) {
        this.reverb = fArr;
    }

    public boolean isFlanger() {
        return this.isFlanger;
    }

    public void setFlanger(boolean flanger) {
        this.isFlanger = flanger;
    }

    public boolean isReverse() {
        return this.isReverse;
    }

    public void setReverse(boolean reverse) {
        this.isReverse = reverse;
    }

    public boolean isEcho() {
        return this.isEcho;
    }

    public void setEcho(boolean echo) {
        this.isEcho = echo;
    }

    public float[] getEq() {
        return this.eq;
    }

    public void setEq(float[] fArr) {
        this.eq = fArr;
    }
}
