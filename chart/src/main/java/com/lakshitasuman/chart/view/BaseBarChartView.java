package com.lakshitasuman.chart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;

import com.lakshitasuman.chart.R;
import com.lakshitasuman.chart.model.ChartSet;

import java.util.ArrayList;

public abstract class BaseBarChartView extends ChartView {
    float barWidth;
    float drawingOffset;
    final Style style;

    public static final int[] ChartAttrs = {R.attr.chart_axisBorderSpacing, R.attr.chart_axisColor, R.attr.chart_axisThickness, R.attr.chart_axisTopSpacing, R.attr.chart_fontSize, R.attr.chart_labelColor, R.attr.chart_labels, R.attr.chart_shadowColor, R.attr.chart_shadowDx, R.attr.chart_shadowDy, R.attr.chart_shadowRadius, R.attr.chart_typeface};


    
    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> arrayList) {
    }

    public BaseBarChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        style = new Style(context.getTheme().obtainStyledAttributes(attributeSet,ChartAttrs, 0, 0));
    }

    public BaseBarChartView(Context context) {
        super(context);
        style = new Style();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        style.init();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        style.clean();
    }

    public void reset() {
        super.reset();
        setMandatoryBorderSpacing();
    }

    
    public void drawBar(Canvas canvas, float f, float f2, float f3, float f4) {
        canvas.drawRoundRect(new RectF((float) ((int) f), (float) ((int) f2), (float) ((int) f3), (float) ((int) f4)), style.cornerRadius, style.cornerRadius, style.barPaint);
    }

    
    public void drawBarBackground(Canvas canvas, float f, float f2, float f3, float f4) {
        canvas.drawRoundRect(new RectF((float) ((int) f), (float) ((int) f2), (float) ((int) f3), (float) ((int) f4)), style.cornerRadius, style.cornerRadius, style.barBackgroundPaint);
    }

    
    public void calculateBarsWidth(int i, float f, float f2) {
        barWidth = (((f2 - f) - (style.barSpacing / 2.0f)) - (style.setSpacing * ((float) (i - 1)))) / ((float) i);
    }

    
    public void calculatePositionOffset(int i) {
        if (i % 2 == 0) {
            drawingOffset = ((((float) i) * barWidth) / 2.0f) + (((float) (i - 1)) * (style.setSpacing / 2.0f));
        } else {
            drawingOffset = ((((float) i) * barWidth) / 2.0f) + (((float) ((i - 1) / 2)) * style.setSpacing);
        }
    }

    public void setBarSpacing(float f) {
        style.barSpacing = f;
    }

    public void setSetSpacing(float f) {
        style.setSpacing = f;
    }

    public void setBarBackgroundColor(@ColorInt int i) {
        style.hasBarBackground = true;
        style.mBarBackgroundColor = i;
        if (style.barBackgroundPaint != null) {
            style.barBackgroundPaint.setColor(style.mBarBackgroundColor);
        }
    }

    public void setRoundCorners(@FloatRange(from = 0.0d) float f) {
        style.cornerRadius = f;
    }

    public class Style {
        private static final int DEFAULT_COLOR = -16777216;
        Paint barBackgroundPaint;
        Paint barPaint;
        float barSpacing;
        float cornerRadius;
        boolean hasBarBackground = false;
        
        public int mBarBackgroundColor = -16777216;
        float setSpacing;

        Style() {
            barSpacing = getResources().getDimension(R.dimen.bar_spacing);
            setSpacing = getResources().getDimension(R.dimen.set_spacing);
        }

        Style(TypedArray typedArray) {
            barSpacing = typedArray.getDimension(0, getResources().getDimension(R.dimen.bar_spacing));
            setSpacing = typedArray.getDimension(0, getResources().getDimension(R.dimen.set_spacing));
        }

        
        public void init() {
            barPaint = new Paint();
            barPaint.setStyle(Paint.Style.FILL);
            barBackgroundPaint = new Paint();
            barBackgroundPaint.setColor(mBarBackgroundColor);
            barBackgroundPaint.setStyle(Paint.Style.FILL);
        }

        
        public void clean() {
            barPaint = null;
            barBackgroundPaint = null;
        }
    }
}
