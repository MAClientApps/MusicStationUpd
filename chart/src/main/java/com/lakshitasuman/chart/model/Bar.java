package com.lakshitasuman.chart.model;

import androidx.annotation.NonNull;

public class Bar extends ChartEntry {
    private static final String TAG = "chart.model.Bar";
    private int[] mGradientColors;
    private float[] mGradientPositions;
    private boolean mHasGradientColor = false;

    public Bar(String str, float f) {
        super(str, f);
        isVisible = true;
    }

    public boolean hasGradientColor() {
        return mHasGradientColor;
    }

    public int[] getGradientColors() {
        return mGradientColors;
    }

    public float[] getGradientPositions() {
        return mGradientPositions;
    }

    public Bar setGradientColor(@NonNull int[] iArr, float[] fArr) {
        if (iArr == null || iArr.length == 0) {
            throw new IllegalArgumentException("Colors argument can't be null or empty.");
        }
        mHasGradientColor = true;
        mGradientColors = iArr;
        mGradientPositions = fArr;
        return this;
    }
}
