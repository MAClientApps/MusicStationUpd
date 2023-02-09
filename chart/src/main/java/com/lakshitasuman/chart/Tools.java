package com.lakshitasuman.chart;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

public class Tools {
    public static float fromDpToPx(float f) {
        try {
            return f * Resources.getSystem().getDisplayMetrics().density;
        } catch (Exception exceptionxception) {
            return f;
        }
    }


    public static Bitmap drawableToBitmap(@NonNull Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    public static int GCD(int i, int i2) {
        return i2 == 0 ? i : GCD(i2, i % i2);
    }

    public static int largestDivisor(int i) {
        if (i > 1) {
            for (int i2 = i / 2; i2 >= 0; i2--) {
                if (i % i2 == 0) {
                    return i2;
                }
            }
        }
        return 1;
    }
}
