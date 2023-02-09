package com.lakshitasuman.musicstation.voice_change.soundMng;

import android.content.Context;
import android.media.SoundPool;


import com.lakshitasuman.musicstation.voice_change.utils.DBLog;

import java.io.IOException;
import java.util.ArrayList;

public class SoundManager {
    private static final int MAX_STREAM = 100;
    private static final int NORMAL_PIORITY = 1;
    private static final int QUALITY = 0;
    private static final String TAG = "SoundManager";
    private static ArrayList<SoundObject> mListSoundObjects = null;
    private static SoundManager mSingletonSoundMng = null;
    private static SoundPool mSoundPool = null;
    private static float mVolume = 1.0f;

    private SoundManager() {
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(100, 3, 0);
            mListSoundObjects = new ArrayList<>();
        }
    }

    public static SoundManager getInstance() {
        if (mSingletonSoundMng == null) {
            mSingletonSoundMng = new SoundManager();
        }
        return mSingletonSoundMng;
    }

    public void addSound(Context context, int i) {
        if (mSoundPool != null && mListSoundObjects != null && getSound(i) == null) {
            mListSoundObjects.add(new SoundObject(i, mSoundPool.load(context, i, 1)));
        }
    }

    public void addSound(Context context, String str) {
        if (mSoundPool != null && mListSoundObjects != null && getSound(str) == null) {
            try {
                mListSoundObjects.add(new SoundObject(str, mSoundPool.load(context.getResources().getAssets().openFd(str), 1)));
            } catch (IOException exception) {
                DBLog.e(TAG, "ErrorWhenLoadSoundFromAsset");
                exception.printStackTrace();
            }
        }
    }

    private SoundObject getSound(int i) {
        ArrayList<SoundObject> arrayList;
        int size;
        if (mSoundPool == null || (arrayList = mListSoundObjects) == null || (size = arrayList.size()) == 0) {
            return null;
        }
        for (int i2 = 0; i2 < size; i2++) {
            SoundObject soundObject = mListSoundObjects.get(i2);
            if (soundObject.getResId() != -1 && i == soundObject.getResId()) {
                return soundObject;
            }
        }
        return null;
    }

    private SoundObject getSound(String str) {
        ArrayList<SoundObject> arrayList;
        int size;
        if (mSoundPool == null || (arrayList = mListSoundObjects) == null || (size = arrayList.size()) == 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            SoundObject soundObject = mListSoundObjects.get(i);
            if (soundObject.getNameResource() != null && soundObject.getNameResource().equals(str)) {
                return soundObject;
            }
        }
        return null;
    }

    public void play(Context context, String str) {
        SoundObject sound;
        if (mSoundPool != null && (sound = getSound(str)) != null) {
            int soundId = sound.getSoundId();
            mSoundPool.play(soundId, mVolume, mVolume, 1, 0, 1.0f);
        }
    }

    public void play(Context context, String str, float f) {
        SoundObject sound;
        if (mSoundPool != null && (sound = getSound(str)) != null) {
            int soundId = sound.getSoundId();
            mSoundPool.play(soundId, mVolume, mVolume, 1, 0, f);
        }
    }

    public void play(Context context, int i, float f) {
        SoundObject sound;
        if (mSoundPool != null && (sound = getSound(i)) != null) {
            int soundId = sound.getSoundId();
            mSoundPool.play(soundId, mVolume, mVolume, 1, 0, f);
        }
    }

    public void play(Context context, int i, int i2) {
        SoundObject sound;
        if (mSoundPool != null && (sound = getSound(i)) != null) {
            int soundId = sound.getSoundId();
            mSoundPool.play(soundId, mVolume, mVolume, 1, i2, 1.0f);
        }
    }

    public void play(Context context, int i) {
        SoundObject sound;
        if (mSoundPool != null && (sound = getSound(i)) != null) {
            int soundId = sound.getSoundId();
            mSoundPool.play(soundId, mVolume, mVolume, 1, 0, 1.0f);
        }
    }

    public void pauseSounds() {
        if (mSoundPool != null) {
            mSoundPool.autoPause();
        }
    }

    public void resumeSounds() {
        if (mSoundPool != null) {
            mSoundPool.autoResume();
        }
    }

    public void setVolumne(float f) {
        mVolume = f;
    }



    public void releaseSound() {
        if (mSoundPool != null) {
            DBLog.d(TAG, "------>DadestroySOund");
            mSoundPool.release();
            mSoundPool = null;
        }
        if (mListSoundObjects != null) {
            DBLog.d(TAG, "------>DadestroySOundObject=" + mListSoundObjects.size());
            mListSoundObjects.clear();
            mListSoundObjects = null;
        }
        mSingletonSoundMng = null;
    }
}
