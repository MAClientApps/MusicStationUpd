package com.lakshitasuman.musicstation.ringtone.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import java.math.BigDecimal;

public class SeekbarVC extends SeekBar implements SeekBar.OnSeekBarChangeListener {
    public float Max = 100.0f;
    public float Min = 0.0f;
    public float Steps = 1.0f;
    public OnMySeekChange onMySeekChange;
    public SeekBar seekbar;

    public interface OnMySeekChange {
        void onChange(int i, float f);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public SeekbarVC(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setMaxVal(100.0f);
        setOnSeekBarChangeListener(this);
    }

    private float formatFloat(float f) {
        return BigDecimal.valueOf((double) f).setScale(1, 4).floatValue();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if (onMySeekChange != null) {
            float formatFloat = formatFloat((((float) i) * Steps) + Min);
            onMySeekChange.onChange((int) formatFloat, formatFloat);
        }
    }

    public void setMaxVal(float f) {
        Max = f;
        setMax((int) ((f - Min) / Steps));
    }

    public void setMinVal(float f) {
        Min = f;
        setMax((int) ((Max - f) / Steps));
    }




    public void setOnSeekBarChangeListener(OnMySeekChange onMySeekChange2) {
        onMySeekChange = onMySeekChange2;
    }

    public void setProgress(float f) {
        super.setProgress((int) ((f - Min) / Steps));
    }

    public void setSteps(float f) {
        Steps = f;
        setMax((int) ((Max - Min) / f));
    }
}
