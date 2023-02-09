package com.lakshitasuman.musicstation.voice_change.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class IOUtils {
    private static final String TAG = "IOUtils";

    public static String readStringFromAssets(Context context, String str) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(str)));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return sb.toString();
                }
                sb.append(readLine);
                sb.append("\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }






    public static void deleteAllFileInDirectory(File file) {
        if (!file.exists()) {
            return;
        }
        File[] arrfile = file.listFiles();
        int n = arrfile.length;
        if (n > 0) {
            for (int i = 0; i < n; n++) {
                arrfile[i].delete();
            }
        }

    }

    public static void clearCache(Context context, String str) {
        File file;
        if (ApplicationUtils.hasSDcard()) {
            file = new File(Environment.getExternalStorageDirectory(), str);
        } else {
            file = context.getCacheDir();
        }
        if (file.exists()) {
            deleteAllFileInDirectory(file);
        }
    }

    public static File getDiskCacheDir(Context context, String str) {
        String str2;
        if ("mounted".equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable()) {
            str2 = getExternalCacheDir(context).getPath();
        } else {
            str2 = context.getCacheDir().getPath();
        }
        return new File(str2 + File.separator + str);
    }

    public static boolean isExternalStorageRemovable() {
        if (hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getExternalCacheDir(Context context) {
        if (hasFroyo()) {
            return context.getExternalCacheDir();
        }
        return new File(Environment.getExternalStorageDirectory().getPath() + ("/Android/data/" + context.getPackageName() + "/cache/"));
    }

    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= 8;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= 9;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= 11;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= 12;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= 16;
    }
}
