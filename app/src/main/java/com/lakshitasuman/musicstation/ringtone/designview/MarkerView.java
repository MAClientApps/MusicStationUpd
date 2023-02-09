package com.lakshitasuman.musicstation.ringtone.designview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MarkerView extends ImageView {
    private MarkerListener mListener = null;
    private int mTouchLast;
    private float mTouchStart;
    private int mVelocity = 0;

    public interface MarkerListener {
        void markerDraw();

        void markerEnter(MarkerView markerView);

        void markerFocus(MarkerView markerView);

        void markerKeyUp();

        void markerLeft(MarkerView markerView, int i);

        void markerRight(MarkerView markerView, int i);

        void markerTouchEnd(MarkerView markerView);

        void markerTouchMove(MarkerView markerView, float f);

        void markerTouchStart(MarkerView markerView, float f);
    }

    public MarkerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
    }

    public void setListener(MarkerListener markerListener) {
        mListener = markerListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            requestFocus();
            mListener.markerTouchStart(this, motionEvent.getRawX());
            return true;
        } else if (action == 1) {
            mListener.markerTouchEnd(this);
            return true;
        } else if (action != 2) {
            return true;
        } else {
            mListener.markerTouchMove(this, motionEvent.getRawX());
            return true;
        }
    }

    @Override
    public void onFocusChanged(boolean gainFocus, int i, Rect rect) {
        MarkerListener markerListener;
        if (gainFocus && (markerListener = mListener) != null) {
            markerListener.markerFocus(this);
        }
        super.onFocusChanged(gainFocus, i, rect);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mListener != null) {
            mListener.markerDraw();
        }
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        mVelocity = mVelocity + 1;
        int sqrt = (int) Math.sqrt((double) ((mVelocity / 2) + 1));
        if (mListener != null) {
            if (i == 21) {
                mListener.markerLeft(this, sqrt);
                return true;
            } else if (i == 22) {
                mListener.markerRight(this, sqrt);
                return true;
            } else if (i == 23) {
                mListener.markerEnter(this);
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        mVelocity = 0;

        if (mListener != null) {
            mListener.markerKeyUp();
        }
        return super.onKeyDown(i, keyEvent);
    }
}
