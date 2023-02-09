package com.lakshitasuman.chart.view;

import android.content.Context;
import android.util.AttributeSet;
import com.lakshitasuman.chart.model.Bar;
import com.lakshitasuman.chart.model.BarSet;
import com.lakshitasuman.chart.model.ChartSet;
import java.util.ArrayList;

public abstract class BaseStackBarChartView extends BaseBarChartView {
    private boolean mCalcMaxValue = true;

    public BaseStackBarChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BaseStackBarChartView(Context context) {
        super(context);
    }

    static int discoverBottomSet(int i, ArrayList<ChartSet> arrayList) {
        boolean b;
        int size = arrayList.size();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i3 >= size) {
                b = false;
                break;
            } else if (arrayList.get(i3).getEntry(i).getValue() < 0.0f) {
                b = true;
                break;
            } else {
                i3++;
            }
        }
        if (b) {
            int i4 = size - 1;
            while (i4 >= 0 && arrayList.get(i4).getEntry(i).getValue() >= 0.0f) {
                i4--;
            }
            return i4;
        }
        while (i2 < size && arrayList.get(i2).getEntry(i).getValue() == 0.0f) {
            i2++;
        }
        return i2;
    }

    static int discoverTopSet(int i, ArrayList<ChartSet> chartSets) {
        boolean bool;
        int size = chartSets.size();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i3 >= size) {
                bool = false;
                break;
            } else if (chartSets.get(i3).getEntry(i).getValue() > 0.0f) {
                bool = true;
                break;
            } else {
                i3++;
            }
        }
        if (bool) {
            int i4 = size - 1;
            while (i4 >= 0 && chartSets.get(i4).getEntry(i).getValue() <= 0.0f) {
                i4--;
            }
            return i4;
        }
        while (i2 < size && chartSets.get(i2).getEntry(i).getValue() == 0.0f) {
            i2++;
        }
        return i2;
    }

    
    public void calculateBarsWidth(int i, float f, float f2) {
        barWidth = (f2 - f) - style.barSpacing;
    }

    private void calculateMaxStackBarValue() {
        int size = data.size();
        int size2 = ((ChartSet) data.get(0)).size();
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < size2; i3++) {
            float f = 0.0f;
            float f2 = 0.0f;
            for (int i4 = 0; i4 < size; i4++) {
                Bar bar = (Bar) ((BarSet) data.get(i4)).getEntry(i3);
                if (bar.getValue() >= 0.0f) {
                    f += bar.getValue();
                } else {
                    f2 += bar.getValue();
                }
            }
            double d = (double) f;
            if (i < ((int) Math.ceil(d))) {
                i = (int) Math.ceil(d);
            }
            double d2 = (double) (f2 * -1.0f);
            if (i2 > ((int) Math.ceil(d2)) * -1) {
                i2 = ((int) Math.ceil(d2)) * -1;
            }
        }
        while (i % getStep() != 0) {
            i++;
        }
        while (i2 % getStep() != 0) {
            i2--;
        }
        super.setAxisBorderValues(i2, i, getStep());
    }

    public void show() {
        if (mCalcMaxValue) {
            calculateMaxStackBarValue();
        }
        super.show();
    }

    public ChartView setAxisBorderValues(int i, int i2, int i3) {
        mCalcMaxValue = false;
        return super.setAxisBorderValues(i, i2, i3);
    }
}
