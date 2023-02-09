package com.lakshitasuman.musicstation.voice_change.wave;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WaveWriter {
    private static final int OUTPUT_STREAM_BUFFER = 16384;
    private int mBytesWritten = 0;
    private int mChannels;
    private File mOutFile;
    private BufferedOutputStream mOutStream;
    private int mSampleBits;
    private int mSampleRate;

    public WaveWriter(String str, String str2, int mSampleRate, int mChannels, int mSampleBits) {
        mOutFile = new File(str + File.separator + str2);
        this.mSampleRate = mSampleRate;
        this.mChannels = mChannels;
        this.mSampleBits = mSampleBits;
    }

    public WaveWriter(File mOutFile, int mSampleRate, int mChannels, int mSampleBits) {
        this.mOutFile = mOutFile;
        this.mSampleRate = mSampleRate;
        this.mChannels = mChannels;
        this.mSampleBits = mSampleBits;
    }

    public boolean createWaveFile() throws IOException {
        if (mOutFile.exists()) {
            mOutFile.delete();
        }
        if (!mOutFile.createNewFile()) {
            return false;
        }
        mOutStream= new BufferedOutputStream(new FileOutputStream(mOutFile), 16384);
        mOutStream.write(new byte[44]);
        return true;
    }

    public void write(short[] sArr, int i, int i2) throws IOException {
        if (mChannels == 1) {
            if (i <= i2) {
                while (i < i2) {
                    try {
                        writeUnsignedShortLE(mOutStream, sArr[i]);
                    } catch (Exception exception) {
                        System.out.println("Error " + exception.getMessage());
                    }
                    mBytesWritten += 2;
                    i++;
                }
                return;
            }
            throw new IndexOutOfBoundsException(String.format("offset %d is greater than length %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
        }
    }


    public void write(short[] sArr, short[] sArr2, int i, int i2) throws IOException {
        if (mChannels == 2) {
            if (i <= i2) {
                while (i < i2) {
                    writeUnsignedShortLE(mOutStream, sArr[i]);
                    writeUnsignedShortLE(mOutStream, sArr2[i]);
                    mBytesWritten += 4;
                    i++;
                }
                return;
            }
            throw new IndexOutOfBoundsException(String.format("offset %d is greater than length %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
        }
    }

    public void closeWaveFile() throws IOException {
        if (mOutStream != null) {
            mOutStream.flush();
            mOutStream.close();
        }
        writeWaveHeader();
    }

    private void writeWaveHeader() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(mOutFile, "rw");
        randomAccessFile.seek(0);
        int i = (mSampleBits + 7) / 8;
        randomAccessFile.writeBytes("RIFF");
        randomAccessFile.writeInt(Integer.reverseBytes(mBytesWritten + 36));
        randomAccessFile.writeBytes("WAVE");
        randomAccessFile.writeBytes("fmt ");
        randomAccessFile.writeInt(Integer.reverseBytes(16));
        randomAccessFile.writeShort(Short.reverseBytes((short) 1));
        randomAccessFile.writeShort(Short.reverseBytes((short) mChannels));
        randomAccessFile.writeInt(Integer.reverseBytes(mSampleRate));
        randomAccessFile.writeInt(Integer.reverseBytes(mSampleRate * mChannels * i));
        randomAccessFile.writeShort(Short.reverseBytes((short) (mChannels * i)));
        randomAccessFile.writeShort(Short.reverseBytes((short) mSampleBits));
        randomAccessFile.writeBytes("data");
        randomAccessFile.writeInt(Integer.reverseBytes(mBytesWritten));
        randomAccessFile.close();
    }






    private static void writeUnsignedShortLE(BufferedOutputStream bufferedOutputStream, short s) throws IOException {
        bufferedOutputStream.write(s);
        bufferedOutputStream.write(s >> 8);
    }
}
