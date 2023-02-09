package com.lakshitasuman.musicstation.voice_change.setting;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingManager implements ISettingConstants {
    public static final String DOBAO_SHARPREFS = "ypy_prefs";
    public static final String TAG = "SettingManager";

    public static void saveSetting(Context context, String str, String str2) {
        SharedPreferences.Editor edit = context.getSharedPreferences(DOBAO_SHARPREFS, 0).edit();
        edit.putString(str, str2);
        edit.commit();
    }

    public static String getSetting(Context context, String str, String str2) {
        return context.getSharedPreferences(DOBAO_SHARPREFS, 0).getString(str, str2);
    }

    public static boolean getFirstTime(Context context) {
        return Boolean.parseBoolean(getSetting(context, ISettingConstants.KEY_FIRST_TIME, "false"));
    }

    public static void setFirstTime(Context context, boolean b) {
        saveSetting(context, ISettingConstants.KEY_FIRST_TIME, String.valueOf(b));
    }

    public static void setOnline(Context context, boolean bool) {
        saveSetting(context, ISettingConstants.KEY_IS_ONLINE, String.valueOf(bool));
    }

    public static boolean getOnline(Context context) {
        return Boolean.parseBoolean(getSetting(context, ISettingConstants.KEY_IS_ONLINE, "false"));
    }

    public static int getCurrentAccentColor(Context context) {
        return Integer.parseInt(getSetting(context, ISettingConstants.KEY_COLOR_ACCENT, "0"));
    }

    public static void setCurrentAccentColor(Context context, int i) {
        saveSetting(context, ISettingConstants.KEY_COLOR_ACCENT, String.valueOf(i));
    }

    public static int getCurrentMainColor(Context context) {
        return Integer.parseInt(getSetting(context, ISettingConstants.KEY_COLOR_MAIN, "0"));
    }

    public static void setCurrentMainColor(Context context, int i) {
        saveSetting(context, ISettingConstants.KEY_COLOR_MAIN, String.valueOf(i));
    }
}
