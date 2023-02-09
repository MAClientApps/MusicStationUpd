package com.lakshitasuman.musicstation.musicplayer.utils;

public class FFTFrame {
    private static final float MAX_DB_VALUE = 76.0f;
    private float[] mDbs;
    private byte[] mRawFFT;

    public FFTFrame(byte[] bArr, int i, int i2) {
        if (i + i2 <= bArr.length) {
            mRawFFT = new byte[i2];
            System.arraycopy(bArr, i, mRawFFT, 0, i2);
            return;
        }
        throw new RuntimeException("Illegal offset and len");
    }




    public void calculate(int i, float[] fArr) {
        int length = (mRawFFT.length / 2) - 1;
        if (mDbs == null || mDbs.length != length) {
            mDbs = new float[length];
        }
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i2 * 2;
            float f = (float) mRawFFT[i3];
            float f2 = (float) mRawFFT[i3 + 1];
            mDbs[i2] = magnitude2Db((f * f) + (f2 * f2));
        }
        for (int i4 = 0; i4 < i; i4++) {
            fArr[i4] = mDbs[(int) (((((float) i4) * 1.0f) * ((float) length)) / ((float) i))] / MAX_DB_VALUE;
        }
    }


    private static float magnitude2Db(float f) {
        if (f == 0.0f) {
            return 0.0f;
        }
        return (float) (Math.log10((double) f) * 20.0d);
    }

    private static float fastSqrt(float f) {
        return Float.intBitsToFloat((Float.floatToRawIntBits(f) >> 1) + 532483686);
    }
}
