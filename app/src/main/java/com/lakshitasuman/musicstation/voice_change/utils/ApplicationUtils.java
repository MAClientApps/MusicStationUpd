package com.lakshitasuman.musicstation.voice_change.utils;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ApplicationUtils {

    public static boolean hasSDcard() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static void hiddenVirtualKeyboard(Context context, View view) {
        try {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
