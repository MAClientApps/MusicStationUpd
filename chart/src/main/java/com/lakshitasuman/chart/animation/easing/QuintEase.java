package com.lakshitasuman.chart.animation.easing;

public class QuintEase extends BaseEasingMethod {
    
    public float easeIn(float f) {
        return f * f * f * f * f;
    }

    
    public float easeInOut(float f) {
        float f2 = f / 0.5f;
        if (f2 < 1.0f) {
            return 0.5f * f2 * f2 * f2 * f2 * f2;
        }
        float f3 = f2 - 2.0f;
        return ((f3 * f3 * f3 * f3 * f3) + 2.0f) * 0.5f;
    }

    
    public float easeOut(float f) {
        return ((float) Math.pow((double) (f - 1.0f), 5.0d)) + 1.0f;
    }
}
