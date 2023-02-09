package com.lakshitasuman.chart.model;

import android.graphics.Color;
import androidx.annotation.ColorInt;

public abstract class ChartEntry implements Comparable<ChartEntry> {
    private static final int DEFAULT_COLOR = -16777216;
    boolean isVisible;
    private int mColor = -16777216;
    private final String mLabel;
    private int[] mShadowColor = new int[4];
    private float mShadowDx = 0.0f;
    private float mShadowDy = 0.0f;
    private float mShadowRadius = 0.0f;
    private float mValue;
    private float mX;
    private float mY;

    ChartEntry(String str, float f) {
        mLabel = str;
        mValue = f;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean hasShadow() {
        return mShadowRadius != 0.0f;
    }

    public String getLabel() {
        return mLabel;
    }

    public float getValue() {
        return mValue;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public int getColor() {
        return mColor;
    }

    public float getShadowRadius() {
        return mShadowRadius;
    }

    public float getShadowDx() {
        return mShadowDx;
    }

    public float getShadowDy() {
        return mShadowDy;
    }

    public int[] getShadowColor() {
        return mShadowColor;
    }

    public void setValue(float f) {
        mValue = f;
    }

    public void setCoordinates(float f, float f2) {
        mX = f;
        mY = f2;
    }

    public void setColor(@ColorInt int i) {
        isVisible = true;
        mColor = i;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setShadow(float f, float f2, float f3, @ColorInt int i) {
        mShadowRadius = f;
        mShadowDx = f2;
        mShadowDy = f3;
        mShadowColor[0] = Color.alpha(i);
        mShadowColor[1] = Color.red(i);
        mShadowColor[2] = Color.blue(i);
        mShadowColor[3] = Color.green(i);
    }

    public String toString() {
        return "Label=" + mLabel + " \n" + "Value=" + mValue + "\n" + "X = " + mX + "\n" + "Y = " + mY;
    }

    public int compareTo(ChartEntry chartEntry) {
        return Float.compare(getValue(), chartEntry.getValue());
    }
}
