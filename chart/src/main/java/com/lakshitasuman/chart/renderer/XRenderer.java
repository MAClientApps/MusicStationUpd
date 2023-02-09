package com.lakshitasuman.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

public class XRenderer extends AxisRenderer {
    
    public float measureInnerChartTop(int i) {
        return (float) i;
    }

    public void dispose() {
        super.dispose();
        defineMandatoryBorderSpacing(mInnerChartLeft, mInnerChartRight);
        defineLabelsPosition(mInnerChartLeft, mInnerChartRight);
    }

    public void measure(int i, int i2, int i3, int i4) {
        mInnerChartLeft = measureInnerChartLeft(i);
        mInnerChartTop = measureInnerChartTop(i2);
        mInnerChartRight = measureInnerChartRight(i3);
        mInnerChartBottom = measureInnerChartBottom(i4);
    }

    
    public float defineAxisPosition() {
        float f = mInnerChartBottom;
        return style.hasXAxis() ? f + (style.getAxisThickness() / 2.0f) : f;
    }

    
    public float defineStaticLabelsPosition(float f, int i) {
        if (labelsPositioning == LabelPosition.INSIDE) {
            float descent = (f - ((float) i)) - style.getLabelsPaint().descent();
            if (style.hasXAxis()) {
                return descent - (style.getAxisThickness() / 2.0f);
            }
            return descent;
        } else if (labelsPositioning != LabelPosition.OUTSIDE) {
            return f;
        } else {
            float fontMaxHeight = f + ((float) i) + (((float) style.getFontMaxHeight()) - style.getLabelsPaint().descent());
            return style.hasXAxis() ? fontMaxHeight + (style.getAxisThickness() / 2.0f) : fontMaxHeight;
        }
    }

    public void draw(Canvas canvas) {
        if (style.hasXAxis()) {
            canvas.drawLine(mInnerChartLeft, axisPosition, mInnerChartRight, axisPosition, style.getChartPaint());
        }
        if (labelsPositioning != LabelPosition.NONE) {
            style.getLabelsPaint().setTextAlign(Paint.Align.CENTER);
            for (int i = 0; i < nLabels; i++) {
                canvas.drawText((String) labels.get(i), ((Float) labelsPos.get(i)).floatValue(), labelsStaticPos, style.getLabelsPaint());
            }
        }
    }

    public float parsePos(int i, double d) {
        if (!handleValues) {
            return ((Float) labelsPos.get(i)).floatValue();
        }
        double d2 = (double) mInnerChartLeft;
        double d3 = (double) minLabelValue;
        Double.isNaN(d3);
        double d4 = d - d3;
        double d5 = (double) screenStep;
        Double.isNaN(d5);
        double d6 = d4 * d5;
        double intValue = (double) (((Integer) labelsValues.get(1)).intValue() - minLabelValue);
        Double.isNaN(intValue);
        Double.isNaN(d2);
        return (float) (d2 + (d6 / intValue));
    }

    
    public float measureInnerChartLeft(int i) {
        return labelsPositioning != LabelPosition.NONE ? style.getLabelsPaint().measureText((String) labels.get(0)) / 2.0f : (float) i;
    }

    
    public float measureInnerChartRight(int i) {
        float f = 0.0f;
        float measureText = nLabels > 0 ? style.getLabelsPaint().measureText((String) labels.get(nLabels - 1)) : 0.0f;
        if (labelsPositioning != LabelPosition.NONE) {
            float f2 = measureText / 2.0f;
            if (borderSpacing + mandatoryBorderSpacing < f2) {
                f = f2 - (borderSpacing + mandatoryBorderSpacing);
            }
        }
        return ((float) i) - f;
    }

    
    public float measureInnerChartBottom(int i) {
        float f = (float) i;
        if (style.hasXAxis()) {
            f -= style.getAxisThickness();
        }
        return labelsPositioning == LabelPosition.OUTSIDE ? f - ((float) (style.getFontMaxHeight() + distLabelToAxis)) : f;
    }
}
