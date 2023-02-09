package com.lakshitasuman.musicstation.musicplayer.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lakshitasuman.musicstation.MainActivity;


public class AnalogController extends View {
    static float height;
    static float width;
    String angle;
    Paint circlePaint;
    public Paint circlePaint2;
    float currDeg;
    float deg = 3.0f;
    float downDeg;
    boolean isDecreasing;
    boolean isIncreasing;
    String label;
    int lineColor;
    public Paint linePaint;
    onProgressChangedListener mListener;
    float midx;
    float midy;
    float prevCurrDeg;
    int progressColor;
    Paint textPaint;


    public interface onProgressChangedListener {
        void onProgressChanged(int i);
    }

    public void setOnProgressChangedListener(onProgressChangedListener onprogresschangedlistener) {
        mListener = onprogresschangedlistener;
    }

    public AnalogController(Context context) {
        super(context);
        init();
    }

    public AnalogController(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public AnalogController(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public void init() {
        textPaint = new Paint();
        textPaint.setColor(-1);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#f52b7e"));
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint2 = new Paint();
        circlePaint2.setColor(Color.parseColor("#f52b7e"));
        circlePaint2.setStyle(Paint.Style.FILL);
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#f52b7e"));
        linePaint.setStrokeWidth(MainActivity.ratio * 7.0f);
        angle = "0.0";
        label = "Label";

    }


    @Override
    public void onDraw(Canvas canvas) {
        double d;
        float f;
        super.onDraw(canvas);
        midx = (float) (canvas.getWidth() / 2);
        midy = (float) (canvas.getHeight() / 2);
        int min = (int) (Math.min(midx, midy) * 0.90625f);
        float max = Math.max(3.0f, deg);
        float min2 = Math.min(deg, 21.0f);
        while (true) {
            d = 6.283185307179586d;
            f = 24.0f;
            if (max >= 22) {
                break;
            }
            double d3 = (double) (((float) max) / 24.0f);
            Double.isNaN(d3);
            double d4 = (1.0d - d3) * 6.283185307179586d;
            double sin = Math.sin(d4);
            Double.isNaN(min);
            double cos = Math.cos(d4);
            Double.isNaN(min);
            circlePaint.setColor(Color.parseColor("#7d7c7c"));
            canvas.drawCircle(midx + ((float) (sin * min)), midy + ((float) (min * cos)), ((float) min) / 15.0f, circlePaint);
            max++;
        }
        int i2 = 3;
        while (true) {
            if (i2 <= min2) {
                double d6 = (double) (i2 / f);
                Double.isNaN(d6);
                double d7 = (1.0d - d6) * d;
                double sin2 = Math.sin(d7);
                Double.isNaN(min);
                double cos2 = Math.cos(d7);
                Double.isNaN(min);
                canvas.drawCircle(midx + ((float) (min * sin2)), midy + ((float) (min * cos2)), ((float) min) / 15.0f, circlePaint2);
                i2++;
                d = 6.283185307179586d;
                f = 24.0f;
            } else {
                double d8 = (double) (0.4f * min);
                double d9 = (double) (deg / 24.0f);
                Double.isNaN(d9);
                double d10 = (1.0d - d9) * 6.283185307179586d;
                double sin3 = Math.sin(d10);
                Double.isNaN(d8);
                float startX = ((float) (sin3 * d8)) + midx;
                double cos3 = Math.cos(d10);
                Double.isNaN(d8);
                float startY = midy + ((float) (d8 * cos3));
                double d11 = (double) (0.6f * min);
                double sin4 = Math.sin(d10);
                Double.isNaN(d11);
                float stopX = midx + ((float) (sin4 * d11));
                double cos4 = Math.cos(d10);
                Double.isNaN(d11);
                float stopY = ((float) (d11 * cos4)) + midy;
                circlePaint.setColor(Color.parseColor("#adadad"));
                canvas.drawCircle(midx, midy, 0.8666667f * min, circlePaint);
                circlePaint.setColor(Color.parseColor("#171829"));
                canvas.drawCircle(midx, midy, min * 0.73333335f, circlePaint);
                Double.isNaN(min);
                canvas.drawText(label, midx, midy + ((float) (min * 1.1d)), textPaint);
                canvas.drawLine(startX, startY, stopX, stopY, linePaint);
                return;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mListener.onProgressChanged((int) (deg - 2.0f));
        if (motionEvent.getAction() == 0) {
            downDeg = (float) ((Math.atan2((double) (motionEvent.getY() - midy), (double) (motionEvent.getX() - midx)) * 180.0d) / 3.141592653589793d);
            downDeg -= 90.0f;
            if (downDeg < 0.0f) {
                downDeg += 360.0f;
            }
            downDeg = (float) Math.floor((double) (downDeg / 15.0f));
            return true;
        } else if (motionEvent.getAction() == 2) {
            currDeg = (float) ((Math.atan2((double) (motionEvent.getY() - midy), (double) (motionEvent.getX() - midx)) * 180.0d) / 3.141592653589793d);
            currDeg -= 90.0f;
            if (currDeg < 0.0f) {
                currDeg += 360.0f;
            }
            currDeg = (float) Math.floor((double) (currDeg / 15.0f));
            if (currDeg == 0.0f && downDeg == 23.0f) {
                deg += 1.0f;
                if (deg > 21.0f) {
                    deg = 21.0f;
                }
                downDeg = currDeg;
            } else if (currDeg == 23.0f && downDeg == 0.0f) {
                deg -= 1.0f;
                if (deg < 3.0f) {
                    deg = 3.0f;
                }
                downDeg = currDeg;
            } else {
                deg += currDeg - downDeg;
                if (deg > 21.0f) {
                    deg = 21.0f;
                }
                if (deg < 3.0f) {
                    deg = 3.0f;
                }
                downDeg = currDeg;
            }
            angle = String.valueOf(String.valueOf(deg));
            invalidate();
            return true;
        } else if (motionEvent.getAction() == 1) {
            return true;
        } else {
            return super.onTouchEvent(motionEvent);
        }
    }

    public int getProgress() {
        return (int) (deg - 2.0f);
    }

    public void setProgress(int i) {
        deg = (float) (i + 2);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String str) {
        label = str;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int i) {
        lineColor = i;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int i) {
        progressColor = i;
    }
}
