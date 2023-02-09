package com.lakshitasuman.musicstation.musicplayer;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class HeeboRegular extends TextView {
    public HeeboRegular(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public HeeboRegular(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public HeeboRegular(Context context) {
        super(context);
        init();
    }




    public void init() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Heebo-Regular.ttf"), Typeface.BOLD);
    }
}
