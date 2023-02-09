package com.lakshitasuman.chart.model;

import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import com.lakshitasuman.chart.Tools;

public class Point extends ChartEntry {
    private static final int DEFAULT_COLOR = -16777216;
    private static final float DOTS_RADIUS = 3.0f;
    private static final float DOTS_THICKNESS = 4.0f;
    private static final String TAG = "chart.model.Point";
    private Drawable mDrawable = null;
    private boolean mHasStroke = false;
    private float mRadius = Tools.fromDpToPx(DOTS_THICKNESS);
    private int mStrokeColor = -16777216;
    private float mStrokeThickness = Tools.fromDpToPx(DOTS_RADIUS);

    public Point(String str, float f) {
        super(str, f);
        isVisible = false;
    }

    public boolean hasStroke() {
        return mHasStroke;
    }

    public float getStrokeThickness() {
        return mStrokeThickness;
    }

    public float getRadius() {
        return mRadius;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public Point setRadius(@FloatRange(from = 0.0d) float f) {
        if (f >= 0.0f) {
            isVisible = true;
            mRadius = f;
            return this;
        }
        throw new IllegalArgumentException("Dot radius can't be < 0.");
    }

    public Point setStrokeThickness(@FloatRange(from = 0.0d) float f) {
        if (f >= 0.0f) {
            isVisible = true;
            mHasStroke = true;
            mStrokeThickness = f;
            return this;
        }
        throw new IllegalArgumentException("Grid thickness < 0.");
    }

    public Point setStrokeColor(@ColorInt int i) {
        isVisible = true;
        mHasStroke = true;
        mStrokeColor = i;
        return this;
    }

    public Point setDrawable(@NonNull Drawable drawable) {
        if (drawable != null) {
            isVisible = true;
            mDrawable = drawable;
            return this;
        }
        throw new IllegalArgumentException("Drawable argument can't be null.");
    }
}
