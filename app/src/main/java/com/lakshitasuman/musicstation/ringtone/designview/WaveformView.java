package com.lakshitasuman.musicstation.ringtone.designview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.lakshitasuman.musicstation.R;

public class WaveformView extends View {
    private Paint mBorderLinePaint;
    private float mDensity;
    private GestureDetector mGestureDetector;
    private Paint mGridPaint = new Paint();
    private int[] mHeightsAtThisZoomLevel;
    private boolean mInitialized;
    private int[] mLenByZoomLevel;
    public WaveformListener mListener;
    private int mNumZoomLevels;
    private int mOffset;
    private Paint mPlaybackLinePaint;
    private int mPlaybackPos;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private Paint mSelectedLinePaint;
    private int mSelectionEnd;
    private int mSelectionStart;
    private CheapSoundFile mSoundFile;
    private Paint mTimecodePaint;
    private Paint mUnselectedBkgndLinePaint;
    private Paint mUnselectedLinePaint;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int mZoomLevel;

    public interface WaveformListener {
        void waveformDraw();

        void waveformFling(float f);

        void waveformTouchEnd();

        void waveformTouchMove(float f);

        void waveformTouchStart(float f);
    }

    public WaveformView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(false);
        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(false);
        mGridPaint.setColor(getResources().getColor(R.color.appPink));
        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(getResources().getColor(R.color.waveSelected));
        mUnselectedLinePaint = new Paint();
        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(getResources().getColor(R.color.waveUnselected));
        mUnselectedBkgndLinePaint = new Paint();
        mUnselectedBkgndLinePaint.setAntiAlias(false);
        mUnselectedBkgndLinePaint.setColor(getResources().getColor(R.color.waveform_unselected_bkgnd_overlay));
        mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(8.0f);
        mBorderLinePaint.setPathEffect(new DashPathEffect(new float[]{3.0f, 0.0f}, 0.0f));
        mBorderLinePaint.setColor(getResources().getColor(R.color.white));
        mPlaybackLinePaint = new Paint();
        mPlaybackLinePaint.setAntiAlias(false);
        mPlaybackLinePaint.setStrokeWidth(8.0f);
        mPlaybackLinePaint.setColor(getResources().getColor(R.color.white));
        mTimecodePaint = new Paint();
        mTimecodePaint.setTextSize(12.0f);
        mTimecodePaint.setAntiAlias(true);
        mTimecodePaint.setColor(getResources().getColor(R.color.timecode));
        mTimecodePaint.setShadowLayer(2.0f, 1.0f, 1.0f, getResources().getColor(R.color.timecode_shadow));
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                mListener.waveformFling(f);
                return true;
            }
        });
       mSoundFile = null;
       mLenByZoomLevel = null;
       mValuesByZoomLevel = null;
       mHeightsAtThisZoomLevel = null;
       mOffset = 0;
       mPlaybackPos = -1;
       mSelectionStart = 0;
       mSelectionEnd = 0;
       mDensity = 1.0f;
       mInitialized = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (mGestureDetector.onTouchEvent(motionEvent)) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
           mListener.waveformTouchStart(motionEvent.getX());
        } else if (action == 1) {
           mListener.waveformTouchEnd();
        } else if (action == 2) {
           mListener.waveformTouchMove(motionEvent.getX());
        }
        return true;
    }


    public void setSoundFile(CheapSoundFile cheapSoundFile) {
       mSoundFile = cheapSoundFile;
       mSampleRate = cheapSoundFile.getSampleRate();
       mSamplesPerFrame =mSoundFile.getSamplesPerFrame();
        computeDoublesForAllZoomLevels();
       mHeightsAtThisZoomLevel = null;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public int getZoomLevel() {
        return mZoomLevel;
    }

    public void setZoomLevel(int i) {
        while (mZoomLevel > i) {
            zoomIn();
        }
        while (mZoomLevel < i) {
            zoomOut();
        }
    }





    public boolean canZoomIn() {
        return mZoomLevel > 0;
    }


    public void zoomIn() {
        if (canZoomIn()) {
           mZoomLevel--;
           mSelectionStart *= 2;
           mSelectionEnd *= 2;
           mHeightsAtThisZoomLevel = null;
           mOffset= ((mOffset + (getMeasuredWidth() / 2)) * 2) - (getMeasuredWidth() / 2);
           if (mOffset < 0) {
               mOffset = 0;
            }
            invalidate();
        }
    }



    public boolean canZoomOut() {
        return mZoomLevel <mNumZoomLevels - 1;
    }


    public void zoomOut() {
        if (canZoomOut()) {
           mZoomLevel++;
           mSelectionStart /= 2;
           mSelectionEnd /= 2;
            int measuredWidth = ((mOffset + (getMeasuredWidth() / 2)) / 2) - (getMeasuredWidth() / 2);
           mOffset = measuredWidth;
            if (measuredWidth < 0) {
               mOffset = 0;
            }
           mHeightsAtThisZoomLevel = null;
            invalidate();
        }
    }


    public int maxPos() {
        return mLenByZoomLevel[mZoomLevel];
    }

    public int secondsToFrames(double d) {
        return (int) ((((d * 1.0d) * ((double)mSampleRate)) / ((double)mSamplesPerFrame)) + 0.5d);
    }

    public int secondsToPixels(double d) {
        return (int) ((((mZoomFactorByZoomLevel[mZoomLevel] * d) * ((double)mSampleRate)) / ((double)mSamplesPerFrame)) + 0.5d);
    }

    public double pixelsToSeconds(int i) {
        return (((double) i) * ((double)mSamplesPerFrame)) / (((double)mSampleRate) *mZoomFactorByZoomLevel[mZoomLevel]);
    }

    public int millisecsToPixels(int i) {
        return (int) (((((((double) i) * 1.0d) * ((double)mSampleRate)) *mZoomFactorByZoomLevel[mZoomLevel]) / (((double)mSamplesPerFrame) * 1000.0d)) + 0.5d);
    }

    public int pixelsToMillisecs(int i) {
        return (int) (((((double) i) * (((double)mSamplesPerFrame) * 1000.0d)) / (((double)mSampleRate) *mZoomFactorByZoomLevel[mZoomLevel])) + 0.5d);
    }

    public void setParameters(int i, int i2, int i3) {
       mSelectionStart = i;
       mSelectionEnd = i2;
       mOffset = i3;
    }

    public int getStart() {
        return mSelectionStart;
    }

    public int getEnd() {
        return mSelectionEnd;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setPlayback(int i) {
       mPlaybackPos = i;
    }

    public void setListener(WaveformListener waveformListener) {
       mListener = waveformListener;
    }

    public void recomputeHeights(float f) {
       mHeightsAtThisZoomLevel = null;
       mDensity = f;
       mTimecodePaint.setTextSize((float) ((int) (f * 12.0f)));
        invalidate();
    }


    public void drawWaveformLine(Canvas canvas, int i, int i2, int i3, Paint paint) {
        float f = (float) i;
        canvas.drawLine(f, (float) i2, f, (float) i3, paint);
    }


    @Override
    public void onDraw(Canvas canvas) {
        double d;
        Paint paint;
        super.onDraw(canvas);
        if (mSoundFile != null) {
            if (mHeightsAtThisZoomLevel == null) {
                computeIntsForThisZoomLevel();
            }
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int length =mHeightsAtThisZoomLevel.length - mOffset;
            int i2 = measuredHeight / 2;
            int i3 = length > measuredWidth ? measuredWidth : length;
            double pixelsToSeconds = pixelsToSeconds(1);
            boolean b = pixelsToSeconds > 0.02d;
            double d2 = ((double)mOffset) * pixelsToSeconds;
            int i4 = (int) d2;
            int i5 = 0;
            while (i5 < i3) {
                i5++;
                d2 += pixelsToSeconds;
                int i6 = (int) d2;
                if (i6 != i4) {
                    if (!b || i6 % 5 == 0) {
                        float f = (float) i5;
                        canvas.drawLine(f, 0.0f, f, (float) measuredHeight,mGridPaint);
                    }
                    i4 = i6;
                }
            }
            for (int i7 = 0; i7 < i3; i7++) {
                int i8 = i7 + mOffset;
                if (i8 <mSelectionStart || i8 >=mSelectionEnd) {
                    drawWaveformLine(canvas, i7, 0, measuredHeight,mUnselectedBkgndLinePaint);
                    paint =mUnselectedLinePaint;
                } else {
                    paint =mSelectedLinePaint;
                }

                drawWaveformLine(canvas, i7, i2 - mHeightsAtThisZoomLevel[i8], i2 + 1 + mHeightsAtThisZoomLevel[i8], paint);
                if (i8 ==mPlaybackPos) {
                    float f2 = (float) i7;
                    canvas.drawLine(f2, 0.0f, f2, (float) measuredHeight,mPlaybackLinePaint);
                }
            }
            for (int i9 = i3; i9 < measuredWidth; i9++) {
                drawWaveformLine(canvas, i9, 0, measuredHeight,mUnselectedBkgndLinePaint);
            }
            canvas.drawLine(((float) (mSelectionStart - mOffset)) + 0.5f, 30.0f, ((float) (mSelectionStart - mOffset)) + 0.5f, (float) measuredHeight,mBorderLinePaint);
            canvas.drawLine(((float) (mSelectionEnd - mOffset)) + 0.5f, 0.0f, ((float) (mSelectionEnd - mOffset)) + 0.5f, (float) (measuredHeight - 30),mBorderLinePaint);
            double d3 = 1.0d;
            if (1.0d / pixelsToSeconds < 50.0d) {
                d3 = 5.0d;
            }
            if (d3 / pixelsToSeconds < 50.0d) {
                d3 = 15.0d;
            }
            double d4 = ((double)mOffset) * pixelsToSeconds;
            int i14 = (int) (d4 / d3);
            int i15 = 0;
            while (i15 < i3) {
                i15++;
                d4 += pixelsToSeconds;
                int i16 = (int) d4;
                int i17 = (int) (d4 / d3);
                if (i17 != i14) {
                    String str = "" + (i16 / 60);
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    int i18 = i16 % 60;
                    sb.append(i18);
                    String sb2 = sb.toString();
                    if (i18 < 10) {
                        sb2 = "0" + sb2;
                    }
                    String str2 = str + ":" + sb2;
                    d = d3;
                    canvas.drawText(str2, ((float) i15) - ((float) (((double)mTimecodePaint.measureText(str2)) * 0.5d)), (float) ((int) (mDensity * 12.0f)),mTimecodePaint);
                    i14 = i17;
                } else {
                    Canvas canvas3 = canvas;
                    d = d3;
                }
                d3 = d;
            }
            if (mListener != null) {
                mListener.waveformDraw();
            }
        }
    }


    private void computeDoublesForAllZoomLevels() {
        int i;
        int numFrames =mSoundFile.getNumFrames();
        int[] frameGains =mSoundFile.getFrameGains();
        double[] dArr = new double[numFrames];
        if (numFrames == 1) {
            dArr[0] = (double) frameGains[0];
        } else if (numFrames == 2) {
            dArr[0] = (double) frameGains[0];
            dArr[1] = (double) frameGains[1];
        } else if (numFrames > 2) {
            dArr[0] = (((double) frameGains[0]) / 2.0d) + (((double) frameGains[1]) / 2.0d);
            int i2 = 1;
            while (true) {
                i = numFrames - 1;
                if (i2 >= i) {
                    break;
                }
                int i3 = i2 + 1;
                dArr[i2] = (((double) frameGains[i2 - 1]) / 3.0d) + (((double) frameGains[i2]) / 3.0d) + (((double) frameGains[i3]) / 3.0d);
                i2 = i3;
            }
            dArr[i] = (((double) frameGains[numFrames - 2]) / 2.0d) + (((double) frameGains[i]) / 2.0d);
        }
        double d = 1.0d;
        for (int i4 = 0; i4 < numFrames; i4++) {
            if (dArr[i4] > d) {
                d = dArr[i4];
            }
        }
        double d2 = d > 255.0d ? 255.0d / d : 1.0d;
        int[] iArr = new int[256];
        double d3 = 0.0d;
        for (int i5 = 0; i5 < numFrames; i5++) {
            int i6 = (int) (dArr[i5] * d2);
            if (i6 < 0) {
                i6 = 0;
            }
            if (i6 > 255) {
                i6 = 255;
            }
            double d4 = (double) i6;
            if (d4 > d3) {
                d3 = d4;
            }
            iArr[i6] = iArr[i6] + 1;
        }
        int i7 = 0;
        double d5 = 0.0d;
        while (d5 < 255.0d && i7 < numFrames / 20) {
            i7 += iArr[(int) d5];
            d5 += 1.0d;
        }
        double d6 = d3;
        int i8 = 0;
        while (d6 > 2.0d && i8 < numFrames / 100) {
            i8 += iArr[(int) d6];
            d6 -= 1.0d;
        }
        double[] dArr2 = new double[numFrames];
        double d7 = d6 - d5;
        for (int i9 = 0; i9 < numFrames; i9++) {
            double d8 = ((dArr[i9] * d2) - d5) / d7;
            if (d8 < 0.0d) {
                d8 = 0.0d;
            }
            if (d8 > 1.0d) {
                d8 = 1.0d;
            }
            dArr2[i9] = d8 * d8;
        }
        mNumZoomLevels = 5;
        mLenByZoomLevel= new int[5];
        mZoomFactorByZoomLevel = new double[5];
        mValuesByZoomLevel = new double[5][];
        char c = 0;
        mLenByZoomLevel[0] = numFrames * 2;
        mZoomFactorByZoomLevel[0] = 2.0d;
        mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];
        if (numFrames > 0) {
            mValuesByZoomLevel[0][0] = dArr2[0] * 0.5d;
            mValuesByZoomLevel[0][1] = dArr2[0];
        }
        int frame = 1;
        while (frame < numFrames) {
            int i11 = frame * 2;
            mValuesByZoomLevel[c][i11] = (dArr2[frame - 1] + dArr2[frame]) * 0.5d;
            mValuesByZoomLevel[c][i11 + 1] = dArr2[frame];
            frame++;
            c = 0;
        }
        mLenByZoomLevel[1] = numFrames;
        mValuesByZoomLevel[1] = new double[mLenByZoomLevel[1]];
        mZoomFactorByZoomLevel[1] = 1.0d;
        for (int i12 = 0; i12 <mLenByZoomLevel[1]; i12++) {
           mValuesByZoomLevel[1][i12] = dArr2[i12];
        }
        for (int i13 = 2; i13 < 5; i13++) {

            int i14 = i13 - 1;
            mLenByZoomLevel[i13] = mLenByZoomLevel[i14] / 2;
            mValuesByZoomLevel[i13] = new double[mLenByZoomLevel[i13]];
            double[] dArr6 =mZoomFactorByZoomLevel;
            dArr6[i13] = dArr6[i14] / 2.0d;
            for (int i15 = 0; i15 <mLenByZoomLevel[i13]; i15++) {
                double[][] dArr7 =mValuesByZoomLevel;
                int i16 = i15 * 2;
                dArr7[i13][i15] = (dArr7[i14][i16] + dArr7[i14][i16 + 1]) * 0.5d;
            }
        }
        if (numFrames > 5000) {
           mZoomLevel = 3;
        } else if (numFrames > 1000) {
           mZoomLevel = 2;
        } else if (numFrames > 300) {
           mZoomLevel = 1;
        } else {
           mZoomLevel = 0;
        }
       mInitialized = true;
    }


    private void computeIntsForThisZoomLevel() {
        int measuredHeight = (getMeasuredHeight() / 2) - 1;
       mHeightsAtThisZoomLevel = new int[mLenByZoomLevel[mZoomLevel]];
        int i = 0;
        while (true) {
            if (i < mLenByZoomLevel[mZoomLevel]) {
               mHeightsAtThisZoomLevel[i] = (int) (mValuesByZoomLevel[mZoomLevel][i] * ((double) measuredHeight));
                i++;
            } else {
                return;
            }
        }
    }
}
