package com.lakshitasuman.musicstation.voice_change.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Display;

public class ResolutionUtils {
    public static final String LANSCAPE = "Landscape";
    public static final String PORTRAIT = "Portrait";

    public static int[] getDeviceResolution(Activity activity) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        int i = activity.getResources().getConfiguration().orientation;
        if (i == 1) {
            int[] iArr = new int[2];
            int i2 = height >= width ? width : height;
            if (height > width) {
                width = height;
            }
            iArr[0] = i2;
            iArr[1] = width;
            return iArr;
        } else if (i != 2) {
            return null;
        } else {
            int[] iArr2 = new int[2];
            int i3 = height <= width ? width : height;
            if (height < width) {
                width = height;
            }
            iArr2[0] = i3;
            iArr2[1] = width;
            return iArr2;
        }
    }

    public static float convertDpToPixel(Context context, float f) {
        return f * (((float) context.getResources().getDisplayMetrics().densityDpi) / 160.0f);
    }

    public static float convertPixelsToDp(Context context, float f) {
        return f / (((float) context.getResources().getDisplayMetrics().densityDpi) / 160.0f);
    }

    public static float convertPixelsToSp(Context context, float f) {
        return f / context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static float convertSpToPixel(Context context, float f) {
        return f * (context.getResources().getDisplayMetrics().scaledDensity / 160.0f);
    }
}
