package com.lakshitasuman.musicstation.ringtone.utils;

import android.database.Cursor;

public class ContentUtill {
    public static String getLong(Cursor cursor) {
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow("_id");
        return "" + cursor.getLong(columnIndexOrThrow);
    }

    public static String getTime(Cursor cursor, String str) {
        return TimeUtils.toFormattedTime(getInt(cursor, str));
    }

    public static int getInt(Cursor cursor, String str) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(str));
    }
}
