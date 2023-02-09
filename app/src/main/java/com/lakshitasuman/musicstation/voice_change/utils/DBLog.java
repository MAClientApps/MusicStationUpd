package com.lakshitasuman.musicstation.voice_change.utils;

import android.util.Log;

public class DBLog {
    public static boolean LOG = false;

    public static void i(String str, String str2) {
        if (LOG) {
            Log.i(str, str2);
        }
    }

    public static void e(String str, String str2) {
        if (LOG) {
            Log.e(str, str2);
        }
    }

    public static void d(String str, String str2) {
        if (LOG) {
            Log.d(str, str2);
        }
    }

    public static void v(String str, String str2) {
        if (LOG) {
            Log.v(str, str2);
        }
    }

    public static void w(String str, String str2) {
        if (LOG) {
            Log.w(str, str2);
        }
    }

    public static void setDebug(boolean debug) {
        LOG = debug;
    }
}
