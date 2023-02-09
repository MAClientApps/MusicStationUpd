package com.lakshitasuman.musicstation.ringtone.designview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class CheapSoundFile {
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    static HashMap<String, Factory> sExtensionMap = new HashMap<>();
    static Factory[] sSubclassFactories = {CheapAAC.getFactory(), CheapAMR.getFactory(), CheapMP3.getFactory(), CheapWAV.getFactory()};
    static ArrayList<String> sSupportedExtensions = new ArrayList<>();
    protected File mInputFile = null;
    protected ProgressListener mProgressListener = null;

    public interface Factory {
        CheapSoundFile create();

        String[] getSupportedExtensions();
    }

    public interface ProgressListener {
        boolean reportProgress(double d);
    }

    public void WriteFile(File file, int i, int i2) throws IOException {
    }

    public int getAvgBitrateKbps() {
        return 0;
    }

    public int getChannels() {
        return 0;
    }

    public int getFileSizeBytes() {
        return 0;
    }

    public String getFiletype() {
        return "Unknown";
    }

    public int[] getFrameGains() {
        return null;
    }

    public int[] getFrameLens() {
        return null;
    }

    public int[] getFrameOffsets() {
        return null;
    }

    public int getNumFrames() {
        return 0;
    }

    public int getSampleRate() {
        return 0;
    }

    public int getSamplesPerFrame() {
        return 0;
    }

    public int getSeekableFrameOffset(int i) {
        return -1;
    }

    static {
        for (Factory factory : sSubclassFactories) {
            for (String str : factory.getSupportedExtensions()) {
                sSupportedExtensions.add(str);
                sExtensionMap.put(str, factory);
            }
        }
    }

    public static CheapSoundFile create(String str, ProgressListener progressListener) throws FileNotFoundException, IOException {
        Factory factory;
        File file = new File(str);
        if (file.exists()) {
            String[] split = file.getName().toLowerCase().split("\\.");
            if (split.length < 2 || (factory = sExtensionMap.get(split[split.length - 1])) == null) {
                return null;
            }
            CheapSoundFile create = factory.create();
            create.setProgressListener(progressListener);
            create.ReadFile(file);
            return create;
        }
        throw new FileNotFoundException(str);
    }

    public static boolean isFilenameSupported(String str) {
        String[] split = str.toLowerCase().split("\\.");
        if (split.length < 2) {
            return false;
        }
        return sExtensionMap.containsKey(split[split.length - 1]);
    }

    public static String[] getSupportedExtensions() {
        ArrayList<String> arrayList = sSupportedExtensions;
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    protected CheapSoundFile() {
    }

    public void ReadFile(File file) throws FileNotFoundException, IOException {
        this.mInputFile = file;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.mProgressListener = progressListener;
    }

    public static String bytesToHex(byte[] bArr) {
        char[] cArr = new char[(bArr.length * 2)];
        int i = 0;
        for (int i2 = 0; i2 < bArr.length; i2++) {
            int i3 = i + 1;

            cArr[i] = HEX_CHARS[(bArr[i2] >>> 4) & 15];
            i = i3 + 1;
            cArr[i3] = HEX_CHARS[bArr[i2] & 15];
        }
        return new String(cArr);
    }

    public String computeMd5OfFirst10Frames() throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        int[] frameOffsets = getFrameOffsets();
        int[] frameLens = getFrameLens();
        int length = frameLens.length;
        if (length > 10) {
            length = 10;
        }
        MessageDigest instance = MessageDigest.getInstance("MD5");
        FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = frameOffsets[i2] - i;
            int i4 = frameLens[i2];
            if (i3 > 0) {
                fileInputStream.skip((long) i3);
                i += i3;
            }
            byte[] bArr = new byte[i4];
            fileInputStream.read(bArr, 0, i4);
            instance.update(bArr);
            i += i4;
        }
        fileInputStream.close();
        return bytesToHex(instance.digest());
    }
}
