package com.lakshitasuman.chart.model;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import java.util.Iterator;

public class BarSet extends ChartSet {
    private static final String TAG = "chart.model.BarSet";

    public BarSet() {
    }

    public BarSet(@NonNull String[] strArr, @NonNull float[] fArr) {
        if (strArr == null || fArr == null) {
            throw new IllegalArgumentException("Labels or/and values can't be null.");
        } else if (strArr.length == fArr.length) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                addBar(strArr[i], fArr[i]);
            }
        } else {
            throw new IllegalArgumentException("Arrays size doesn't match.");
        }
    }

    public void addBar(String str, float f) {
        addBar(new Bar(str, f));
    }

    public void addBar(@NonNull Bar bar) {
        addEntry(bar);
    }

    public int getColor() {
        return getEntry(0).getColor();
    }

    public BarSet setColor(@ColorInt int i) {
        Iterator<ChartEntry> it = getEntries().iterator();
        while (it.hasNext()) {
            it.next().setColor(i);
        }
        return this;
    }

    public BarSet setGradientColor(@NonNull int[] iArr, float[] fArr) {
        if (iArr == null || iArr.length == 0) {
            throw new IllegalArgumentException("Colors argument can't be null or empty.");
        }
        Iterator<ChartEntry> it = getEntries().iterator();
        while (it.hasNext()) {
            ((Bar) it.next()).setGradientColor(iArr, fArr);
        }
        return this;
    }
}
