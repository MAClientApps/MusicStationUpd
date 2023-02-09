package com.lakshitasuman.chart.animation;

import android.graphics.Path;
import android.graphics.PathMeasure;
import androidx.annotation.FloatRange;
import com.lakshitasuman.chart.animation.easing.BaseEasingMethod;
import com.lakshitasuman.chart.animation.easing.QuintEase;
import com.lakshitasuman.chart.model.ChartSet;
import com.lakshitasuman.chart.view.ChartView;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Animation {
    private static final int DEFAULT_ALPHA_OFF = -1;
    private static final int DEFAULT_DURATION = 1000;
    private static final float DEFAULT_OVERLAP_FACTOR = 1.0f;
    private static final long DELAY_BETWEEN_UPDATES = 20;
    private static final String TAG = "animation.Animation";
    private int mAlphaSpeed;
    private final Runnable mAnimator = new Runnable() {
        public void run() {
            if (mChartView.canIPleaseAskYouToDraw()) {
                mChartView.addData((ArrayList<ChartSet>) getUpdate(mChartView.getData()));
                mChartView.postInvalidate();
            }
        }
    };
    private boolean mCancelled;
    
    public ChartView mChartView;
    private long[] mCurrentDuration;
    private long mCurrentGlobalDuration;
    private int mDuration;
    private BaseEasingMethod mEasing;
    private long mGlobalDuration;
    private long mGlobalInitTime;
    private long[] mInitTime;
    private int[] mOrder;
    private float mOverlapingFactor;
    private PathMeasure[][] mPathMeasures;
    private boolean mPlaying;
    private Runnable mRunnable;
    private float[] mSetsAlpha;
    private float mStartXFactor;
    private float mStartYFactor;

    public Animation() {
        init(1000);
    }

    public Animation(int i) {
        init(i);
    }

    private void init(int i) {
        mGlobalDuration = (long) i;
        mOverlapingFactor = DEFAULT_OVERLAP_FACTOR;
        mAlphaSpeed = -1;
        mEasing = new QuintEase();
        mStartXFactor = -1.0f;
        mStartYFactor = -1.0f;
        mPlaying = false;
        mCurrentGlobalDuration = 0;
        mGlobalInitTime = 0;
    }

    private ArrayList<ChartSet> prepareAnimation(ChartView chartView, ArrayList<float[][]> arrayList, ArrayList<float[][]> arrayList2) {
        int size = arrayList.size();
        int length = arrayList.get(0).length;
        mChartView = chartView;
        mCurrentDuration = new long[length];
        if (mOrder == null) {
            mOrder = new int[length];
            for (int i = 0; i < mOrder.length; i++) {
                mOrder[i] = i;
            }
        } else if (mOrder.length != length) {
            throw new IllegalArgumentException("Size of overlap order different than set's entries size.");
        }

        float f = (float) (mGlobalDuration / length);
        mDuration = (int) (f + ((((float) mGlobalDuration) - f) * mOverlapingFactor));
        mPathMeasures = (PathMeasure[][]) Array.newInstance(PathMeasure.class, new int[]{size, length});
        for (int i2 = 0; i2 < size; i2++) {
            for (int i3 = 0; i3 < length; i3++) {
                Path path = new Path();
                path.moveTo(arrayList.get(i2)[i3][0], arrayList.get(i2)[i3][1]);
                path.lineTo(arrayList2.get(i2)[i3][0], arrayList2.get(i2)[i3][1]);
                mPathMeasures[i2][i3] = new PathMeasure(path, false);
            }
        }
        mInitTime = new long[length];
        mGlobalInitTime = System.currentTimeMillis();
        for (int i4 = 0; i4 < length; i4++) {
            long j2 = mGlobalInitTime + (((long) i4) * (mGlobalDuration / length));
            mInitTime[mOrder[i4]] = j2 - ((long) (mOverlapingFactor * ((float) (j2 - mGlobalInitTime))));
        }
        mPlaying = true;
        return getUpdate(mChartView.getData());
    }

    private ArrayList<ChartSet> prepareAnimation(ChartView chartView) {
        float f;
        float f2;

        ArrayList<ChartSet> data = chartView.getData();
        float f3 = -1.0f;
        if (mStartXFactor != -1.0f) {
            f = chartView.getInnerChartLeft() + ((chartView.getInnerChartRight() - chartView.getInnerChartLeft()) * mStartXFactor);
        } else {
            f = chartView.getZeroPosition();
        }
        if (mStartYFactor != -1.0f) {
            f2 = chartView.getInnerChartBottom() - ((chartView.getInnerChartBottom() - chartView.getInnerChartTop()) * mStartYFactor);
        } else {
            f2 = chartView.getZeroPosition();
        }
        int size = data.size();
        char c = 0;
        int size2 = data.get(0).size();
        mSetsAlpha = new float[size];
        ArrayList arrayList = new ArrayList(size);
        ArrayList arrayList2 = new ArrayList(size);
        int i = 0;
        while (i < size) {
            mSetsAlpha[i] = data.get(i).getAlpha();
            float[][] fArr = (float[][]) Array.newInstance(float.class, new int[]{size2, 2});
            float[][] fArr2 = (float[][]) Array.newInstance(float.class, new int[]{size2, 2});
            int i2 = 0;
            while (i2 < size2) {
                if (mStartXFactor == f3 && chartView.getOrientation() == ChartView.Orientation.VERTICAL) {
                    fArr[i2][c] = data.get(i).getEntry(i2).getX();
                } else {
                    fArr[i2][c] = f;
                }
                if (mStartYFactor == -1.0f && chartView.getOrientation() == ChartView.Orientation.HORIZONTAL) {
                    fArr[i2][1] = data.get(i).getEntry(i2).getY();
                } else {
                    fArr[i2][1] = f2;
                }
                fArr2[i2][0] = data.get(i).getEntry(i2).getX();
                fArr2[i2][1] = data.get(i).getEntry(i2).getY();
                i2++;
                f3 = -1.0f;
                c = 0;
            }
            arrayList.add(fArr);
            arrayList2.add(fArr2);
            i++;
            f3 = -1.0f;
            c = 0;
        }
        if (mEasing.getState() == 0) {
            return prepareAnimation(chartView, arrayList, arrayList2);
        }
        return prepareAnimation(chartView, arrayList2, arrayList);
    }

    public ArrayList<ChartSet> prepareEnterAnimation(ChartView chartView) {
        mEasing.setState(0);
        return prepareAnimation(chartView);
    }

    public ArrayList<ChartSet> prepareUpdateAnimation(ChartView chartView, ArrayList<float[][]> arrayList, ArrayList<float[][]> arrayList2) {
        mEasing.setState(1);
        return prepareAnimation(chartView, arrayList, arrayList2);
    }

    public ArrayList<ChartSet> prepareExitAnimation(ChartView chartView) {
        mEasing.setState(2);
        return prepareAnimation(chartView);
    }

    
    public ArrayList<ChartSet> getUpdate(ArrayList<ChartSet> arrayList) {
        int size = arrayList.size();
        int size2 = arrayList.get(0).size();
        long currentTimeMillis = System.currentTimeMillis();
        mCurrentGlobalDuration = currentTimeMillis - mGlobalInitTime;
        int i = 0;
        while (true) {
            long j = 0;
            if (i >= size2) {
                break;
            }
            long j2 = currentTimeMillis - mInitTime[i];
            long[] jArr = mCurrentDuration;
            if (j2 >= 0) {
                j = j2;
            }
            jArr[i] = j;
            i++;
        }
        if (mCurrentGlobalDuration > mGlobalDuration) {
            mCurrentGlobalDuration = mGlobalDuration;
        }
        float[] fArr = new float[2];
        for (int i2 = 0; i2 < size; i2++) {
            for (int i3 = 0; i3 < size2; i3++) {
                float normalizeTime = normalizeTime(i3);
                if (!(mAlphaSpeed == -1 || mEasing.getState() == 1)) {
                    arrayList.get(i2).setAlpha(mEasing.next(normalizeTime) * ((float) mAlphaSpeed) * mSetsAlpha[i2]);
                }
                if (!getEntryUpdate(i2, i3, normalizeTime, fArr)) {
                    fArr[0] = arrayList.get(i2).getEntry(i3).getX();
                    fArr[1] = arrayList.get(i2).getEntry(i3).getY();
                }
                arrayList.get(i2).getEntry(i3).setCoordinates(fArr[0], fArr[1]);
            }
        }
        if (mCurrentGlobalDuration >= mGlobalDuration || mCancelled) {
            mCurrentGlobalDuration = 0;
            mGlobalInitTime = 0;
            if (mRunnable != null) {
                mRunnable.run();
            }
            mPlaying = false;
        } else {
            mChartView.postDelayed(mAnimator, DELAY_BETWEEN_UPDATES);
            mCurrentGlobalDuration += DELAY_BETWEEN_UPDATES;
        }
        return arrayList;
    }

    private float normalizeTime(int i) {
        return ((float) mCurrentDuration[i]) / ((float) mDuration);
    }

    public void cancel() {
        mCancelled = true;
    }

    private boolean getEntryUpdate(int i, int i2, float f, float[] fArr) {
        return mPathMeasures[i][i2].getPosTan(mPathMeasures[i][i2].getLength() * mEasing.next(f), fArr, (float[]) null);
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public Runnable getEndAction() {
        return mRunnable;
    }

    public Animation setEasing(BaseEasingMethod baseEasingMethod) {
        mEasing = baseEasingMethod;
        return this;
    }

    public Animation setDuration(int i) {
        mGlobalDuration = (long) i;
        return this;
    }

    private Animation setOverlap(@FloatRange(from = 0.0d, to = 1.0d) float f) {
        if (f > DEFAULT_OVERLAP_FACTOR || f < 0.0f) {
            throw new IllegalArgumentException("Overlap factor must be between 0 and 1, the current defined is " + f);
        }
        mOverlapingFactor = f;
        return this;
    }

    public Animation setOverlap(@FloatRange(from = 0.0d, to = 1.0d) float f, int[] iArr) {
        setOverlap(f);
        mOrder = iArr;
        return this;
    }

    public Animation setEndAction(Runnable runnable) {
        mRunnable = runnable;
        return this;
    }

    public Animation setStartPoint(@FloatRange(from = -1.0d, to = 1.0d) float f, @FloatRange(from = -1.0d, to = 1.0d) float f2) {
        mStartXFactor = f;
        mStartYFactor = f2;
        return this;
    }

    public Animation setAlpha(int i) {
        mAlphaSpeed = i;
        return this;
    }
}
