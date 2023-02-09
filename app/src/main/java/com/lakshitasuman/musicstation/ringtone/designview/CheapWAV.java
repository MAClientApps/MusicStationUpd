package com.lakshitasuman.musicstation.ringtone.designview;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CheapWAV extends CheapSoundFile {
    private int mChannels;
    private int mFileSize;
    private int mFrameBytes;
    private int[] mFrameGains;
    private int[] mFrameLens;
    private int[] mFrameOffsets;
    private int mNumFrames;
    private int mOffset;
    private int mSampleRate;

    public String getFiletype() {
        return "WAV";
    }

    static class C07581 implements CheapSoundFile.Factory {
        public String[] getSupportedExtensions() {
            return new String[]{"wav"};
        }

        C07581() {
        }

        public CheapSoundFile create() {
            return new CheapWAV();
        }
    }

    public static CheapSoundFile.Factory getFactory() {
        return new C07581();
    }

    public int getNumFrames() {
        return mNumFrames;
    }

    public int getSamplesPerFrame() {
        return mSampleRate / 50;
    }

    public int[] getFrameOffsets() {
        return mFrameOffsets;
    }

    public int[] getFrameLens() {
        return mFrameLens;
    }

    public int[] getFrameGains() {
        return mFrameGains;
    }

    public int getFileSizeBytes() {
        return mFileSize;
    }

    public int getAvgBitrateKbps() {
        return ((mSampleRate * mChannels) * 2) / 1024;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public int getChannels() {
        return mChannels;
    }

    @Override
    public void ReadFile(File file) throws IOException {
        super.ReadFile(file);
        mFileSize = (int) mInputFile.length();
        if (mFileSize >= 128) {
            try {
                WavFile openWavFile = WavFile.openWavFile(file);
                mNumFrames = (int) (openWavFile.getNumFrames() / ((long) getSamplesPerFrame()));
                mFrameGains = new int[mNumFrames];
                mSampleRate = (int) openWavFile.getSampleRate();
                mChannels = openWavFile.getNumChannels();
                int[] iArr = new int[getSamplesPerFrame()];
                int i = 0;
                while (true) {
                    if (i >= mNumFrames) {
                        break;
                    }
                    openWavFile.readFrames(iArr, getSamplesPerFrame());
                    int i2 = -1;
                    for (int i3 = 0; i3 < getSamplesPerFrame(); i3++) {
                        int i4 = iArr[i3];
                        if (i2 < i4) {
                            i2 = i4;
                        }
                    }
                    mFrameGains[i] = (int) Math.sqrt((double) i2);
                    if (mProgressListener != null) {
                        double d = (double) i;
                        Double.isNaN(d);
                        double d2 = d * 1.0d;
                        double length = (double) mFrameGains.length;
                        Double.isNaN(length);
                        if (!mProgressListener.reportProgress(d2 / length)) {
                            break;
                        }
                    }
                    i++;
                }
                if (openWavFile != null) {
                    openWavFile.close();
                }
            } catch (WavFileException e) {
                Log.e(TAG, "Exception while reading wav file", e);
            }
        } else {
            throw new IOException("File too small to parse");
        }
    }

    @Override
    public void WriteFile(File file, int i, int i2) throws IOException {
        file.createNewFile();
        FileInputStream fileInputStream = new FileInputStream(mInputFile);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        long j = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            j += (long) mFrameLens[i + i4];
        }
        long j2 = 36 + j;
        long j4 = (long) (mSampleRate * 2 * mChannels);
        fileOutputStream.write(new byte[]{82, 73, 70, 70, (byte) ((int) (j2 & 255)), (byte) ((int) ((j2 >> 8) & 255)), (byte) ((int) ((j2 >> 16) & 255)), (byte) ((int) ((j2 >> 24) & 255)), 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, (byte) mChannels, 0, (byte) ((int) (mSampleRate & 255)), (byte) ((int) ((mSampleRate >> 8) & 255)), (byte) ((int) ((mSampleRate >> 16) & 255)), (byte) ((int) ((mSampleRate >> 24) & 255)), (byte) ((int) (j4 & 255)), (byte) ((int) ((j4 >> 8) & 255)), (byte) ((int) ((j4 >> 16) & 255)), (byte) ((int) ((j4 >> 24) & 255)), (byte) (mChannels * 2), 0, 16, 0, 100, 97, 116, 97, (byte) ((int) (j & 255)), (byte) ((int) ((j >> 8) & 255)), (byte) ((int) ((j >> 16) & 255)), (byte) ((int) ((j >> 24) & 255))}, 0, 44);
        byte[] bArr = new byte[mFrameBytes];
        int i7 = 0;
        for (int i8 = 0; i8 < i2; i8++) {
            int i9 = i + i8;
            int i10 = mFrameOffsets[i9] - i7;
            int i11 = mFrameLens[i9];
            if (i10 >= 0) {
                if (i10 > 0) {
                    fileInputStream.skip((long) i10);
                    i7 += i10;
                }
                fileInputStream.read(bArr, 0, i11);
                fileOutputStream.write(bArr, 0, i11);
                i7 += i11;
            }
        }
        fileInputStream.close();
        fileOutputStream.close();
    }
}
