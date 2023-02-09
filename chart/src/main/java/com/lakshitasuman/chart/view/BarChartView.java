package com.lakshitasuman.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import com.lakshitasuman.chart.model.Bar;
import com.lakshitasuman.chart.model.BarSet;
import com.lakshitasuman.chart.model.ChartSet;

import java.util.ArrayList;

public class BarChartView extends BaseBarChartView {
    public BarChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
    }

    public BarChartView(Context context) {
        super(context);
        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
    }

    @Override
    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> arrayList) {
        ArrayList<ChartSet> arrayList2 = arrayList;
        int size = arrayList.size();
        int size2 = arrayList2.get(0).size();
        for (int i = 0; i < size2; i++) {
            float x = arrayList2.get(0).getEntry(i).getX() - drawingOffset;
            for (int i2 = 0; i2 < size; i2++) {
                BarSet barSet = (BarSet) arrayList2.get(i2);
                Bar bar = (Bar) barSet.getEntry(i);
                if (barSet.isVisible() && bar.getValue() != 0.0f) {
                    if (!bar.hasGradientColor()) {
                        style.barPaint.setColor(bar.getColor());
                    } else {
                        style.barPaint.setShader(new LinearGradient(bar.getX(), getZeroPosition(), bar.getX(), bar.getY(), bar.getGradientColors(), bar.getGradientPositions(), Shader.TileMode.MIRROR));
                    }
                    applyShadow(style.barPaint, barSet.getAlpha(), bar.getShadowDx(), bar.getShadowDy(), bar.getShadowRadius(), bar.getShadowColor());
                    if (style.hasBarBackground) {
                        drawBarBackground(canvas, x, getInnerChartTop(), x + barWidth, getInnerChartBottom());
                    }
                    if (bar.getValue() > 0.0f) {
                        drawBar(canvas, x, bar.getY(), x + barWidth, getZeroPosition());
                    } else {
                        drawBar(canvas, x, getZeroPosition(), x + barWidth, bar.getY());
                    }
                    x += barWidth;
                    if (i2 != size - 1) {
                        x += style.setSpacing;
                    }
                }
            }
        }
    }


    @Override
    public void onPreDrawChart(ArrayList<ChartSet> arrayList) {
        if (arrayList.get(0).size() == 1) {
            style.barSpacing = 0.0f;
            calculateBarsWidth(arrayList.size(), 0.0f, (getInnerChartRight() - getInnerChartLeft()) - (getBorderSpacing() * 2.0f));
        } else {
            calculateBarsWidth(arrayList.size(), arrayList.get(0).getEntry(0).getX(), arrayList.get(0).getEntry(1).getX());
        }
        calculatePositionOffset(arrayList.size());
    }

    @Override
    public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> arrayList) {
        int size = arrayList.size();
        int size2 = arrayList.get(0).size();
        ArrayList<ArrayList<Region>> arrayList2 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            arrayList2.add(new ArrayList(size2));
        }
        for (int i2 = 0; i2 < size2; i2++) {
            float x = arrayList.get(0).getEntry(i2).getX() - drawingOffset;
            for (int i3 = 0; i3 < size; i3++) {
                Bar bar = (Bar) ((BarSet) arrayList.get(i3)).getEntry(i2);
                if (bar.getValue() > 0.0f) {
                    x += barWidth;
                    arrayList2.get(i3).add(new Region((int) x, (int) bar.getY(), (int) x, (int) getZeroPosition()));
                } else if (bar.getValue() < 0.0f) {
                    x += barWidth;
                    arrayList2.get(i3).add(new Region((int) x, (int) getZeroPosition(), (int) x, (int) bar.getY()));
                } else {
                    x += barWidth;
                    arrayList2.get(i3).add(new Region((int) x, (int) getZeroPosition(), (int) x, ((int) getZeroPosition()) + 1));
                }
                if (i3 != size - 1) {
                    x += style.setSpacing;
                }
            }
        }
        return arrayList2;
    }
}
