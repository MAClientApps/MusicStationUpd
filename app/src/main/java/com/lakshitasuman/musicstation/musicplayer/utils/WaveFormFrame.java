package com.lakshitasuman.musicstation.musicplayer.utils;

public class WaveFormFrame {
    private byte[] mRawWaveForm;


    public WaveFormFrame(byte[] bArr, int i, int i2) {
        if (i + i2 <= bArr.length) {
            mRawWaveForm = new byte[i2];
            System.arraycopy(bArr, i, mRawWaveForm, 0, i2);
            return;
        }
        throw new RuntimeException("Illegal offset and len");
    }


    public byte[] getRawWaveForm() {
        return mRawWaveForm;
    }
}
