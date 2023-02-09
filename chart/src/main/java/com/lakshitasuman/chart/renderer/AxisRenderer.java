package com.lakshitasuman.chart.renderer;

import android.graphics.Canvas;
import com.lakshitasuman.chart.Tools;
import com.lakshitasuman.chart.model.ChartEntry;
import com.lakshitasuman.chart.model.ChartSet;
import com.lakshitasuman.chart.view.ChartView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class AxisRenderer {
    float axisPosition;
    float borderSpacing;
    int distLabelToAxis;
    boolean handleValues;
    DecimalFormat labelFormat;
    ArrayList<String> labels;
    ArrayList<Float> labelsPos;
    LabelPosition labelsPositioning;
    float labelsStaticPos;
    ArrayList<Integer> labelsValues;
    float mInnerChartBottom;
    float mInnerChartLeft;
    float mInnerChartRight;
    float mInnerChartTop;
    float mandatoryBorderSpacing;
    int maxLabelValue;
    int minLabelValue;
    int nLabels;
    float screenStep;
    int step;
    ChartView.Style style;
    float topSpacing;

    public enum LabelPosition {
        NONE,
        OUTSIDE,
        INSIDE
    }

    
    public abstract float defineAxisPosition();

    
    public abstract float defineStaticLabelsPosition(float f, int i);

    
    public abstract void draw(Canvas canvas);

    
    public abstract void measure(int i, int i2, int i3, int i4);

    public abstract float parsePos(int i, double d);

    AxisRenderer() {
        reset();
    }

    public void init(ArrayList<ChartSet> arrayList, ChartView.Style style2) {
        int[] iArr;
        if (handleValues) {
            if (minLabelValue == 0 && maxLabelValue == 0) {
                if (hasStep()) {
                    iArr = findBorders(arrayList, step);
                } else {
                    iArr = findBorders(arrayList, 1);
                }
                minLabelValue = iArr[0];
                maxLabelValue = iArr[1];
            }
            if (!hasStep()) {
                setBorderValues(minLabelValue, maxLabelValue);
            }
            labelsValues = calculateValues(minLabelValue, maxLabelValue, step);
            labels = convertToLabelsFormat(labelsValues, labelFormat);
        } else {
            labels = extractLabels(arrayList);
        }
        nLabels = labels.size();
        style = style2;
    }

    
    public void dispose() {
        axisPosition = defineAxisPosition();
        labelsStaticPos = defineStaticLabelsPosition(axisPosition, distLabelToAxis);
    }

    public void reset() {
        distLabelToAxis = (int) Tools.fromDpToPx(5.0f);
        mandatoryBorderSpacing = 0.0f;
        borderSpacing = 0.0f;
        topSpacing = 0.0f;
        step = -1;
        labelsStaticPos = 0.0f;
        labelsPositioning = LabelPosition.OUTSIDE;
        labelFormat = new DecimalFormat();
        axisPosition = 0.0f;
        minLabelValue = 0;
        maxLabelValue = 0;
        handleValues = false;
    }

    
    public void defineMandatoryBorderSpacing(float f, float f2) {
        if (mandatoryBorderSpacing == 1.0f) {
            mandatoryBorderSpacing = (((f2 - f) - (borderSpacing * 2.0f)) / ((float) nLabels)) / 2.0f;
        }
    }

    
    public void defineLabelsPosition(float f, float f2) {
        screenStep = ((((f2 - f) - topSpacing) - (borderSpacing * 2.0f)) - (mandatoryBorderSpacing * 2.0f)) / ((float) (nLabels - 1));
        labelsPos = new ArrayList<>(nLabels);
        float f3 = f + borderSpacing + mandatoryBorderSpacing;
        for (int i = 0; i < nLabels; i++) {
            labelsPos.add(Float.valueOf(f3));
            f3 += screenStep;
        }
    }

    
    public ArrayList<String> convertToLabelsFormat(ArrayList<Integer> arrayList, DecimalFormat decimalFormat) {
        int size = arrayList.size();
        ArrayList<String> arrayList2 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            arrayList2.add(decimalFormat.format(arrayList.get(i)));
        }
        return arrayList2;
    }

    
    public ArrayList<String> extractLabels(ArrayList<ChartSet> arrayList) {
        int size = arrayList.get(0).size();
        ArrayList<String> arrayList2 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            arrayList2.add(arrayList.get(0).getLabel(i));
        }
        return arrayList2;
    }

    
    public int[] findBorders(ArrayList<ChartSet> arrayList, int i) {
        float f;
        float f2;
        float f3;
        Iterator<ChartSet> it = arrayList.iterator();
        float f4 = -2.14748365E9f;
        float f5 = 2.14748365E9f;
        while (it.hasNext()) {
            Iterator<ChartEntry> it2 = it.next().getEntries().iterator();
            while (it2.hasNext()) {
                ChartEntry next = it2.next();
                if (next.getValue() >= f4) {
                    f4 = next.getValue();
                }
                if (next.getValue() <= f5) {
                    f5 = next.getValue();
                }
            }
        }
        if (f4 < 0.0f) {
            f = 0.0f;
        } else {
            f = (float) ((int) Math.ceil((double) f4));
        }
        if (f5 > 0.0f) {
            f2 = 0.0f;
        } else {
            f2 = (float) ((int) Math.floor((double) f5));
        }
        while (true) {
            f3 = (float) i;
            if ((f - f2) % f3 == 0.0f) {
                break;
            }
            f += 1.0f;
        }
        if (f2 == f) {
            f += f3;
        }
        return new int[]{(int) f2, (int) f};
    }

    
    public ArrayList<Integer> calculateValues(int i, int i2, int i3) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        while (i <= i2) {
            arrayList.add(Integer.valueOf(i));
            i += i3;
        }
        if (arrayList.get(arrayList.size() - 1).intValue() < i2) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }

    public float getInnerChartLeft() {
        return mInnerChartLeft;
    }

    public float getInnerChartTop() {
        return mInnerChartTop;
    }

    public float getInnerChartRight() {
        return mInnerChartRight;
    }

    public float getInnerChartBottom() {
        return mInnerChartBottom;
    }

    public float[] getInnerChartBounds() {
        return new float[]{mInnerChartLeft, mInnerChartTop, mInnerChartRight, mInnerChartBottom};
    }

    public int getStep() {
        return step;
    }

    public float getBorderSpacing() {
        return borderSpacing;
    }

    public int getBorderMaximumValue() {
        return maxLabelValue;
    }

    public int getBorderMinimumValue() {
        return minLabelValue;
    }

    public boolean hasMandatoryBorderSpacing() {
        return mandatoryBorderSpacing == 1.0f;
    }

    public boolean hasStep() {
        return step != -1;
    }

    public void setHandleValues(boolean handleValues) {
        this.handleValues = handleValues;
    }

    public void setLabelsPositioning(LabelPosition labelPosition) {
        labelsPositioning = labelPosition;
    }

    public void setLabelsFormat(DecimalFormat decimalFormat) {
        labelFormat = decimalFormat;
    }

    public void setStep(int i) {
        step = i;
    }

    public void setBorderSpacing(float f) {
        borderSpacing = f;
    }

    public void setTopSpacing(float f) {
        topSpacing = f;
    }

    public void setMandatoryBorderSpacing(boolean mandatoryBorderSpacing) {
        this.mandatoryBorderSpacing = mandatoryBorderSpacing ? 1.0f : 0.0f;
    }

    public void setLabelToAxisDistance(int i) {
        distLabelToAxis = i;
    }

    public void setInnerChartBounds(float f, float f2, float f3, float f4) {
        mInnerChartLeft = f;
        mInnerChartTop = f2;
        mInnerChartRight = f3;
        mInnerChartBottom = f4;
    }

    public void setAxisLabelsSpacing(float f) {
        distLabelToAxis = (int) f;
    }

    public void setBorderValues(int i, int i2, int i3) {
        if (i >= i2) {
            throw new IllegalArgumentException("Minimum border value must be greater than maximum values");
        } else if ((i2 - i) % i3 == 0) {
            step = i3;
            maxLabelValue = i2;
            minLabelValue = i;
        } else {
            throw new IllegalArgumentException("Step value must be a divisor of distance between minimum border value and maximum border value");
        }
    }

    public void setBorderValues(int i, int i2) {
        if (!hasStep()) {
            step = Tools.largestDivisor(i2 - i);
        }
        setBorderValues(i, i2, step);
    }
}
