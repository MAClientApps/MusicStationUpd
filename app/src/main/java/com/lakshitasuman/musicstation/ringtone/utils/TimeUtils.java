package com.lakshitasuman.musicstation.ringtone.utils;

public class TimeUtils {

    public class MilliSeconds {
        public static final int ONE_HOUR = 3600000;
        public static final int ONE_MINUTE = 60000;
        public static final int ONE_SECOND = 1000;

        public MilliSeconds() {
        }
    }

    public static String toFormattedTime(int i) {
        int i2 = i / MilliSeconds.ONE_HOUR;
        int i3 = i - (MilliSeconds.ONE_HOUR * i2);
        int i4 = i3 / MilliSeconds.ONE_MINUTE;
        int i5 = (i3 - (MilliSeconds.ONE_MINUTE * i4)) / 1000;
        if (i2 > 0) {
            return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i4), Integer.valueOf(i5)});
        }
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5)});
    }
}
