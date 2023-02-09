package com.lakshitasuman.musicstation.voice_change.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.lakshitasuman.musicstation.R;


public class CircularProgressBar extends ProgressBar {
    public static final String TAG = "CircularProgressBar";

    public CircularProgressBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public CircularProgressBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.cpbStyle);
    }


    public CircularProgressBar(Context context, AttributeSet attributeSet, int i) {

        super(context, attributeSet, i);
        if (isInEditMode()) {
            setIndeterminateDrawable(new CircularProgressDrawable.Builder(context).build());
            return;
        }
        Resources resources = context.getResources();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.circularProgressBar, i, 0);
        int color = obtainStyledAttributes.getColor( 1, resources.getColor(R.color.colorPrimary));
        float dimension = obtainStyledAttributes.getDimension(6, resources.getDimension(R.dimen.cpb_default_stroke_width));
        float f = obtainStyledAttributes.getFloat(7, Float.parseFloat(resources.getString(R.string.cpb_default_sweep_speed)));
        float f2 = obtainStyledAttributes.getFloat(5, Float.parseFloat(resources.getString(R.string.cpb_default_rotation_speed)));
        int resourceId = obtainStyledAttributes.getResourceId(2, 0);
        int integer = obtainStyledAttributes.getInteger(4, resources.getInteger(R.integer.cpb_default_min_sweep_angle));
        int integer2 = obtainStyledAttributes.getInteger(3, resources.getInteger(R.integer.cpb_default_max_sweep_angle));
        obtainStyledAttributes.recycle();
        int[] intArray = resourceId != 0 ? resources.getIntArray(resourceId) : null;
        CircularProgressDrawable.Builder maxSweepAngle = new CircularProgressDrawable.Builder(context).sweepSpeed(f).rotationSpeed(f2).strokeWidth(dimension).minSweepAngle(integer).maxSweepAngle(integer2);
        if (intArray == null || intArray.length <= 0) {
            maxSweepAngle.color(color);
        } else {
            maxSweepAngle.colors(intArray);
        }
        setIndeterminateDrawable(maxSweepAngle.build());
    }

    private CircularProgressDrawable checkIndeterminateDrawable() {
        Drawable indeterminateDrawable = getIndeterminateDrawable();
        if (indeterminateDrawable != null && (indeterminateDrawable instanceof CircularProgressDrawable)) {
            return (CircularProgressDrawable) indeterminateDrawable;
        }
        throw new RuntimeException("The drawable is not a CircularProgressDrawable");
    }


    public void progressiveStop() {
        checkIndeterminateDrawable().progressiveStop();
    }

    public void progressiveStop(CircularProgressDrawable.OnEndListener onEndListener) {
        checkIndeterminateDrawable().progressiveStop(onEndListener);
    }
}
