package com.lakshitasuman.chart.animation.easing;

public abstract class BaseEasingMethod {
    public static final int ENTER = 0;
    public static final int EXIT = 2;
    public static final int UPDATE = 1;
    private static int mState;

    
    public abstract float easeIn(float f);

    
    public abstract float easeInOut(float f);

    
    public abstract float easeOut(float f);

    public float next(float f) {
        if (mState == 0) {
            return easeOut(f);
        }
        if (mState == 1) {
            return easeInOut(f);
        }
        if (mState == 2) {
            return easeIn(f);
        }
        return 1.0f;
    }

    public int getState() {
        return mState;
    }

    public void setState(int i) {
        mState = i;
    }
}
