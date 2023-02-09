package com.lakshitasuman.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import com.lakshitasuman.chart.model.Bar;
import com.lakshitasuman.chart.model.BarSet;
import com.lakshitasuman.chart.model.ChartSet;

import java.util.ArrayList;

public class StackBarChartView extends BaseStackBarChartView {
    public StackBarChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
    }

    public StackBarChartView(Context context) {
        super(context);
        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
    }

    @Override
    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {

        float verticalOffset;
        float currBottomY;

        float negVerticalOffset;
        float negCurrBottomY;

        float x0;
        float x1;
        float y1;
        float barSize;
        int bottomSetIndex;
        int topSetIndex;
        float cornersPatch;
        BarSet barSet;
        Bar bar;
        int dataSize = data.size();
        int setSize = data.get(0).size();
        float zeroPosition = this.getZeroPosition();

        for (int i = 0; i < setSize; i++) {

            // If bar needs background
            if(style.hasBarBackground) {
                drawBarBackground(canvas,
                        (int) (data.get(0).getEntry(i).getX() - barWidth / 2),
                        (int) this.getInnerChartTop(),
                        (int) (data.get(0).getEntry(i).getX() + barWidth / 2),
                        (int) this.getInnerChartBottom());
            }

            // Vertical offset to keep drawing bars on top of the others
            verticalOffset = 0;
            negVerticalOffset = 0;

            // Bottom of the next bar to be drawn
            currBottomY = zeroPosition;
            negCurrBottomY = zeroPosition;

            // Unfortunately necessary to discover which set is the bottom and top in case there
            // are entries with value 0. To better understand check one of the methods.
            bottomSetIndex = discoverBottomSet(i, data);
            topSetIndex = discoverTopSet(i, data);

            for(int j = 0; j < dataSize; j++){

                barSet = (BarSet) data.get(j);
                bar = (Bar) barSet.getEntry(i);

                barSize = Math.abs(zeroPosition - bar.getY());

                // If:
                // Bar not visible OR
                // Bar value equal to 0 OR
                // Size of bar < 2 (Due to the loss of precision)
                // Then no need to draw
                if(!barSet.isVisible() || bar.getValue() == 0 || barSize < 2)
                    continue;

                style.barPaint.setColor(bar.getColor());
                style.barPaint.setAlpha((int)(barSet.getAlpha() * 255));
                applyShadow(style.barPaint,barSet.getAlpha(), bar.getShadowDx(), bar.getShadowDy(), bar.getShadowRadius(), bar.getShadowColor());

                x0 = (bar.getX() - barWidth / 2);
                x1 = (bar.getX() + barWidth / 2);


                if(bar.getValue() > 0) {

                    y1 = zeroPosition - (barSize + verticalOffset);

                    // Draw bar
                    if (j == bottomSetIndex) {
                        drawBar(canvas, (int) x0, (int) y1, (int) x1, (int) currBottomY);
                        if (bottomSetIndex != topSetIndex && style.cornerRadius != 0) {
                            // Patch top corners of bar
                            cornersPatch = (currBottomY - y1) / 2;
                            canvas.drawRect(new Rect((int) x0, (int) y1,
                                            (int) x1, (int) (y1 + cornersPatch)),
                                    style.barPaint);
                        }

                    } else if (j == topSetIndex) {
                        drawBar(canvas, (int) x0, (int) y1, (int) x1, (int) currBottomY);
                        // Patch bottom corners of bar
                        cornersPatch = (currBottomY - y1) / 2;
                        canvas.drawRect(new Rect((int) x0, (int) (currBottomY - cornersPatch),
                                        (int) x1, (int) currBottomY),
                                style.barPaint);

                    } else {// if(j != bottomSetIndex && j != topSetIndex){ // Middle sets
                        canvas.drawRect(new Rect((int) x0, (int) y1,
                                        (int) x1, (int) currBottomY),
                                style.barPaint);
                    }

                    currBottomY = y1;

                    // Increase the vertical offset to be used by the next bar
                    if (barSize != 0)
                        // Sum 1 to compensate the loss of precision in float
                        verticalOffset += barSize + 2;


                }else{ // if(bar.getValue() < 0)

                    y1 = zeroPosition + (barSize - negVerticalOffset);

                    if (j == bottomSetIndex) {
                        drawBar(canvas, (int) x0, (int) negCurrBottomY, (int) x1, (int) y1);
                        if (bottomSetIndex != topSetIndex && style.cornerRadius != 0) {
                            // Patch top corners of bar
                            cornersPatch = (y1 - negCurrBottomY) / 2;
                            canvas.drawRect(new Rect((int) x0, (int) negCurrBottomY,
                                            (int) x1, (int) (negCurrBottomY + cornersPatch)),
                                    style.barPaint);
                        }

                    } else if (j == topSetIndex) {
                        drawBar(canvas, (int) x0, (int) negCurrBottomY, (int) x1, (int) y1);
                        // Patch bottom corners of bar
                        cornersPatch = (y1 - negCurrBottomY) / 2;
                        canvas.drawRect(new Rect((int) x0, (int) (y1 - cornersPatch),
                                        (int) x1, (int) y1),
                                style.barPaint);

                    } else {// if(j != bottomSetIndex && j != topSetIndex){ // Middle sets
                        canvas.drawRect(new Rect((int) x0, (int) negCurrBottomY,
                                        (int) x1, (int) y1),
                                style.barPaint);
                    }

                    negCurrBottomY = y1;

                    // Increase the vertical offset to be used by the next bar
                    if (barSize != 0)
                        negVerticalOffset -= barSize;

                }
            }
        }
    }

    public void onPreDrawChart(ArrayList<ChartSet> arrayList) {
        if (arrayList.get(0).size() == 1) {
            this.barWidth = (getInnerChartRight() - getInnerChartLeft()) - (getBorderSpacing() * 2.0f);
        } else {
            calculateBarsWidth(-1, arrayList.get(0).getEntry(0).getX(), arrayList.get(0).getEntry(1).getX());
        }
    }

    public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> arrayList) {
        int i;
        int i2;
        ArrayList<ChartSet> arrayList2 = arrayList;
        int size = arrayList.size();
        int size2 = arrayList2.get(0).size();
        ArrayList<ArrayList<Region>> arrayList3 = new ArrayList<>(size);
        for (int i3 = 0; i3 < size; i3++) {
            arrayList3.add(new ArrayList(size2));
        }
        float zeroPosition = getZeroPosition();
        int i4 = 0;
        while (i4 < size2) {
            float f = 0.0f;
            float f2 = zeroPosition;
            float f3 = f2;
            int i5 = 0;
            float f4 = 0.0f;
            float f5 = 0.0f;
            while (i5 < size) {
                BarSet barSet = (BarSet) arrayList2.get(i5);
                Bar bar = (Bar) barSet.getEntry(i4);
                float abs = Math.abs(zeroPosition - bar.getY());
                if (!barSet.isVisible()) {
                    i2 = size;
                    i = size2;
                } else if (bar.getValue() > f) {
                    float f6 = zeroPosition - (abs + f4);
                    i2 = size;
                    i = size2;
                    arrayList3.get(i5).add(new Region((int) (bar.getX() - (this.barWidth / 2.0f)), (int) f6, (int) (bar.getX() + (this.barWidth / 2.0f)), (int) f2));
                    f4 += abs + 2.0f;
                    f2 = f6;
                } else {
                    i2 = size;
                    i = size2;
                    if (bar.getValue() < 0.0f) {
                        float f7 = (abs - f5) + zeroPosition;
                        arrayList3.get(i5).add(new Region((int) (bar.getX() - (this.barWidth / 2.0f)), (int) f3, (int) (bar.getX() + (this.barWidth / 2.0f)), (int) f7));
                        f5 -= abs;
                        f3 = f7;
                    } else {
                        arrayList3.get(i5).add(new Region((int) (bar.getX() - (this.barWidth / 2.0f)), (int) (zeroPosition - (1.0f + f4)), (int) (bar.getX() + (this.barWidth / 2.0f)), (int) f2));
                    }
                    i5++;
                    size = i2;
                    size2 = i;
                    arrayList2 = arrayList;
                    f = 0.0f;
                }
                i5++;
                size = i2;
                size2 = i;
                arrayList2 = arrayList;
                f = 0.0f;
            }
            int i6 = size;
            int i7 = size2;
            i4++;
            arrayList2 = arrayList;
        }
        return arrayList3;
    }
}
