package com.lakshitasuman.musicstation.voice_change.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String REGEX_SPECIAL_CHARACTER = "[^a-zA-Z0-9_]";
    public static final String TAG = "StringUtils";

    public static String urlEncodeString(String str) {
        if (str != null && !str.equals("")) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException exception) {
                String str2 = TAG;
                DBLog.d(str2, "---------->encodeError=" + exception.getMessage());
                exception.printStackTrace();
            }
        }
        return str;
    }

    public static String urlDecodeString(String str) {
        if (str != null && !str.equals("")) {
            try {
                return URLDecoder.decode(str, "UTF-8");
            } catch (UnsupportedEncodingException exception) {
                String str2 = TAG;
                DBLog.d(str2, "---------->decodeError=" + exception.getMessage());
                exception.printStackTrace();
            }
        }
        return str;
    }

    public static String getSplitString(String str, int i) {
        if (str == null) {
            return null;
        }
        if (str.length() <= i) {
            return str;
        }
        return str.substring(0, i) + "...";
    }

    public static String formatHtmlBoldKeyword(String str, String str2) {
        if (str != null && str2 != null && !str2.equals("") && str.contains(str2)) {
            try {
                return str.replace(str2, "<b>" + str2 + "</b>");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return str;
    }

    public static boolean isNumber(String str) {
        return str.matches("[+-]?\\d*(\\.\\d+)?");
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.equals("");
    }

    public static boolean isContainsSpecialCharacter(String str) {
        return str != null && !str.equals("") && Pattern.compile(REGEX_SPECIAL_CHARACTER).matcher(str).find();
    }

    public static float formatStringNumber(float f) {
        try {
            return Float.parseFloat(String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(f)}).replace(",", "."));
        } catch (Exception exception) {
            exception.printStackTrace();
            return f;
        }
    }
}
