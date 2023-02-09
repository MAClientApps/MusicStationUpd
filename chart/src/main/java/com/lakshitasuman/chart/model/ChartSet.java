package com.lakshitasuman.chart.model;

import androidx.annotation.FloatRange;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public abstract class ChartSet {
    private static final String TAG = "chart.model.ChartSet";
    private float mAlpha = 1.0f;
    private final ArrayList<ChartEntry> mEntries = new ArrayList<>();
    private boolean mIsVisible = false;

    ChartSet() {
    }

    
    public void addEntry(ChartEntry chartEntry) {
        if (chartEntry != null) {
            mEntries.add(chartEntry);
            return;
        }
        throw new IllegalArgumentException("Chart entry added can't be null object.");
    }

    public void updateValues(float[] fArr) {
        int size = size();
        if (fArr.length == size) {
            for (int i = 0; i < size; i++) {
                setValue(i, fArr[i]);
            }
            return;
        }
        throw new IllegalArgumentException("New set values given doesn't match previous number of entries.");
    }

    public ArrayList<ChartEntry> getEntries() {
        return mEntries;
    }

    public ChartEntry getEntry(int i) {
        return mEntries.get(i);
    }

    public float getValue(int i) {
        return mEntries.get(i).getValue();
    }

    public String getLabel(int i) {
        return mEntries.get(i).getLabel();
    }

    public ChartEntry getMax() {
        return (ChartEntry) Collections.max(mEntries);
    }

    public ChartEntry getMin() {
        return (ChartEntry) Collections.min(mEntries);
    }

    public float[][] getScreenPoints() {
        int size = size();
        float[][] fArr = (float[][]) Array.newInstance(float.class, new int[]{size, 2});
        for (int i = 0; i < size; i++) {
            fArr[i][0] = mEntries.get(i).getX();
            fArr[i][1] = mEntries.get(i).getY();
        }
        return fArr;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public boolean isVisible() {
        return mIsVisible;
    }

    private void setValue(int i, float f) {
        mEntries.get(i).setValue(f);
    }

    public void setAlpha(@FloatRange(from = 0.0d, to = 1.0d) float f) {
        if (f >= 1.0f) {
            f = 1.0f;
        }
        mAlpha = f;
    }

    public void setVisible(boolean visible) {
        mIsVisible = visible;
    }

    
    public void setShadow(float f, float f2, float f3, int i) {
        Iterator<ChartEntry> it = getEntries().iterator();
        while (it.hasNext()) {
            it.next().setShadow(f, f2, f3, i);
        }
    }

    public int size() {
        return mEntries.size();
    }

    public String toString() {
        return mEntries.toString();
    }
}
