package com.lakshitasuman.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Collections;
import java.util.Iterator;

public class YRenderer extends AxisRenderer {
    private float measureInnerChartRight(int i) {
        return (float) i;
    }

    private float measureInnerChartTop(int i) {
        return (float) i;
    }

    public void dispose() {
        super.dispose();
        defineMandatoryBorderSpacing(mInnerChartTop, mInnerChartBottom);
        defineLabelsPosition(mInnerChartTop, mInnerChartBottom);
    }

    public void measure(int i, int i2, int i3, int i4) {
        mInnerChartLeft = measureInnerChartLeft(i);
        mInnerChartTop = measureInnerChartTop(i2);
        mInnerChartRight = measureInnerChartRight(i3);
        mInnerChartBottom = measureInnerChartBottom(i4);
    }

    
    public float defineAxisPosition() {
        float f = mInnerChartLeft;
        return style.hasYAxis() ? f - (style.getAxisThickness() / 2.0f) : f;
    }

    
    public float defineStaticLabelsPosition(float f, int i) {
        if (labelsPositioning == LabelPosition.INSIDE) {
            float f2 = f + ((float) i);
            if (style.hasYAxis()) {
                return f2 + (style.getAxisThickness() / 2.0f);
            }
            return f2;
        } else if (labelsPositioning != LabelPosition.OUTSIDE) {
            return f;
        } else {
            float f3 = f - ((float) i);
            return style.hasYAxis() ? f3 - (style.getAxisThickness() / 2.0f) : f3;
        }
    }

    public void draw(Canvas canvas) {
        if (style.hasYAxis()) {
            float f = mInnerChartBottom;
            if (style.hasXAxis()) {
                f += style.getAxisThickness();
            }
            canvas.drawLine(axisPosition, mInnerChartTop, axisPosition, f, style.getChartPaint());
        }
        if (labelsPositioning != LabelPosition.NONE) {
            style.getLabelsPaint().setTextAlign(labelsPositioning == LabelPosition.OUTSIDE ? Paint.Align.RIGHT : Paint.Align.LEFT);
            for (int i = 0; i < nLabels; i++) {
                canvas.drawText((String) labels.get(i), labelsStaticPos, ((Float) labelsPos.get(i)).floatValue() + ((float) (style.getLabelHeight((String) labels.get(i)) / 2)), style.getLabelsPaint());
            }
        }
    }

    
    public void defineLabelsPosition(float f, float f2) {
        super.defineLabelsPosition(f, f2);
        Collections.reverse(labelsPos);
    }

    public float parsePos(int i, double d) {
        if (!handleValues) {
            return ((Float) labelsPos.get(i)).floatValue();
        }
        double d2 = (double) mInnerChartBottom;
        double d3 = (double) minLabelValue;
        Double.isNaN(d3);
        double d4 = d - d3;
        double d5 = (double) screenStep;
        Double.isNaN(d5);
        double d6 = d4 * d5;
        double intValue = (double) (((Integer) labelsValues.get(1)).intValue() - minLabelValue);
        Double.isNaN(intValue);
        Double.isNaN(d2);
        return (float) (d2 - (d6 / intValue));
    }

    public float measureInnerChartLeft(int i) {
        float f = (float) i;
        if (style.hasYAxis()) {
            f += style.getAxisThickness();
        }
        if (labelsPositioning != LabelPosition.OUTSIDE) {
            return f;
        }
        float f2 = 0.0f;
        Iterator it = labels.iterator();
        while (it.hasNext()) {
            float measureText = style.getLabelsPaint().measureText((String) it.next());
            if (measureText > f2) {
                f2 = measureText;
            }
        }
        return f + f2 + ((float) distLabelToAxis);
    }

    public float measureInnerChartBottom(int i) {
        return (labelsPositioning == LabelPosition.NONE || borderSpacing >= ((float) (style.getFontMaxHeight() / 2))) ? (float) i : (float) (i - (style.getFontMaxHeight() / 2));
    }
}
