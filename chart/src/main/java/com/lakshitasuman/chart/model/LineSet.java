package com.lakshitasuman.chart.model;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import com.lakshitasuman.chart.Tools;
import java.util.Iterator;

public class LineSet extends ChartSet {
    private static final int DEFAULT_COLOR = -16777216;
    private static final float LINE_THICKNESS = 4.0f;
    private static final String TAG = "chart.model.LineSet";
    private int mBegin;
    private int mColor;
    private float[] mDashedIntervals;
    private int mDashedPhase;
    private int mEnd;
    private int mFillColor;
    private int[] mGradientColors;
    private float[] mGradientPositions;
    private boolean mHasFill;
    private boolean mHasGradientFill;
    private boolean mIsDashed;
    private boolean mIsSmooth;
    private int[] mShadowColor;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private float mThickness;

    public LineSet() {
        init();
    }

    public LineSet(@NonNull String[] strArr, @NonNull float[] fArr) {
        init();
        if (strArr == null || fArr == null) {
            throw new IllegalArgumentException("Labels or/and values can't be null.");
        } else if (strArr.length == fArr.length) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                addPoint(strArr[i], fArr[i]);
            }
        } else {
            throw new IllegalArgumentException("Arrays size doesn't match.");
        }
    }

    private void init() {
        mThickness = Tools.fromDpToPx(LINE_THICKNESS);
        mColor = -16777216;
        mIsDashed = false;
        mDashedIntervals = null;
        mDashedPhase = 0;
        mIsSmooth = false;
        mHasFill = false;
        mFillColor = -16777216;
        mHasGradientFill = false;
        mGradientColors = null;
        mGradientPositions = null;
        mBegin = 0;
        mEnd = 0;
        mShadowRadius = 0.0f;
        mShadowDx = 0.0f;
        mShadowDy = 0.0f;
        mShadowColor = new int[4];
    }

    public void addPoint(String str, float f) {
        addPoint(new Point(str, f));
    }

    public void addPoint(@NonNull Point point) {
        addEntry(point);
    }

    public boolean isDashed() {
        return mIsDashed;
    }

    public boolean isSmooth() {
        return mIsSmooth;
    }

    public boolean hasFill() {
        return mHasFill;
    }

    public boolean hasGradientFill() {
        return mHasGradientFill;
    }

    public boolean hasShadow() {
        return mShadowRadius != 0.0f;
    }

    public float getThickness() {
        return mThickness;
    }

    public int getColor() {
        return mColor;
    }

    public int getFillColor() {
        return mFillColor;
    }

    public int[] getGradientColors() {
        return mGradientColors;
    }

    public float[] getGradientPositions() {
        return mGradientPositions;
    }

    public int getBegin() {
        return mBegin;
    }

    public int getEnd() {
        if (mEnd == 0) {
            return size();
        }
        return mEnd;
    }

    public float[] getDashedIntervals() {
        return mDashedIntervals;
    }

    public int getDashedPhase() {
        return mDashedPhase;
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

    public LineSet setDashedPhase(int i) {
        mDashedPhase = i;
        return this;
    }

    public LineSet setDashed(@NonNull float[] fArr) {
        if (fArr != null) {
            mIsDashed = true;
            mDashedIntervals = fArr;
            return this;
        }
        throw new IllegalArgumentException("Argument can't be null.");
    }

    public LineSet setSmooth(boolean smooth) {
        mIsSmooth = smooth;
        return this;
    }

    public LineSet setThickness(@FloatRange(from = 0.0d) float f) {
        if (f >= 0.0f) {
            mThickness = f;
            return this;
        }
        throw new IllegalArgumentException("Line thickness can't be <= 0.");
    }

    public LineSet setColor(@ColorInt int i) {
        mColor = i;
        return this;
    }

    public LineSet setFill(@ColorInt int i) {
        mHasFill = true;
        mFillColor = i;
        if (mColor == -16777216) {
            mColor = i;
        }
        return this;
    }

    public LineSet setGradientFill(@NonNull int[] iArr, float[] fArr) {
        if (iArr == null || iArr.length == 0) {
            throw new IllegalArgumentException("Colors argument can't be null or empty.");
        }
        mHasGradientFill = true;
        mGradientColors = iArr;
        mGradientPositions = fArr;
        if (mColor == -16777216) {
            mColor = iArr[0];
        }
        return this;
    }

    public LineSet beginAt(@IntRange(from = 0) int i) {
        if (i < 0 || i > size()) {
            throw new IllegalArgumentException("Index is negative or greater than set's size.");
        }
        mBegin = i;
        return this;
    }

    public LineSet endAt(@IntRange(from = 0) int i) {
        if (i < 0 || i > size()) {
            throw new IllegalArgumentException("Index is negative or greater than set's size.");
        } else if (i >= mBegin) {
            mEnd = i;
            return this;
        } else {
            throw new IllegalArgumentException("Index cannot be lesser than the start entry defined in beginAt(index).");
        }
    }

    public LineSet setDotsColor(@ColorInt int i) {
        Iterator<ChartEntry> it = getEntries().iterator();
        while (it.hasNext()) {
            it.next().setColor(i);
        }
        return this;
    }

    public LineSet setDotsRadius(@FloatRange(from = 0.0d) float f) {
        if (f >= 0.0f) {
            Iterator<ChartEntry> it = getEntries().iterator();
            while (it.hasNext()) {
                ((Point) it.next()).setRadius(f);
            }
            return this;
        }
        throw new IllegalArgumentException("Dots radius can't be < 0.");
    }

    public LineSet setDotsStrokeThickness(@FloatRange(from = 0.0d) float f) {
        if (f >= 0.0f) {
            Iterator<ChartEntry> it = getEntries().iterator();
            while (it.hasNext()) {
                ((Point) it.next()).setStrokeThickness(f);
            }
            return this;
        }
        throw new IllegalArgumentException("Dots thickness can't be < 0.");
    }

    public LineSet setDotsStrokeColor(@ColorInt int i) {
        Iterator<ChartEntry> it = getEntries().iterator();
        while (it.hasNext()) {
            ((Point) it.next()).setStrokeColor(i);
        }
        return this;
    }

    public LineSet setDotsDrawable(@NonNull Drawable drawable) {
        if (drawable != null) {
            Iterator<ChartEntry> it = getEntries().iterator();
            while (it.hasNext()) {
                ((Point) it.next()).setDrawable(drawable);
            }
            return this;
        }
        throw new IllegalArgumentException("Drawable argument can't be null.");
    }

    public void setShadow(float f, float f2, float f3, int i) {
        super.setShadow(f, f2, f3, i);
        mShadowRadius = f;
        mShadowDx = f2;
        mShadowDy = f3;
        mShadowColor[0] = Color.alpha(i);
        mShadowColor[1] = Color.red(i);
        mShadowColor[2] = Color.blue(i);
        mShadowColor[3] = Color.green(i);
    }
}
