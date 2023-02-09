package com.lakshitasuman.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import com.lakshitasuman.chart.model.Bar;
import com.lakshitasuman.chart.model.BarSet;
import com.lakshitasuman.chart.model.ChartSet;

import java.util.ArrayList;

public class HorizontalStackBarChartView extends BaseStackBarChartView {
    public HorizontalStackBarChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOrientation(Orientation.HORIZONTAL);
        setMandatoryBorderSpacing();
    }

    public HorizontalStackBarChartView(Context context) {
        super(context);
        setOrientation(Orientation.HORIZONTAL);
        setMandatoryBorderSpacing();
    }

    @Override
    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> arrayList) {
        float f;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        float f2;
        int i7;
        Canvas canvas2 = canvas;
        ArrayList<ChartSet> arrayList2 = arrayList;
        int size = arrayList.size();
        int i8 = 0;
        int size2 = arrayList2.get(0).size();
        float zeroPosition = getZeroPosition();
        int i9 = 0;
        while (i9 < size2) {
            float f3 = 2.0f;
            if (this.style.hasBarBackground) {
                drawBarBackground(canvas, (float) ((int) getInnerChartLeft()), (float) ((int) (arrayList2.get(i8).getEntry(i9).getY() - (this.barWidth / 2.0f))), (float) ((int) getInnerChartRight()), (float) ((int) (arrayList2.get(i8).getEntry(i9).getY() + (this.barWidth / 2.0f))));
            }
            int discoverBottomSet = discoverBottomSet(i9, arrayList2);
            int discoverTopSet = discoverTopSet(i9, arrayList2);
            float f4 = zeroPosition;
            float f5 = f4;
            int i10 = 0;
            float f6 = 0.0f;
            float f7 = 0.0f;
            while (i10 < size) {
                BarSet barSet = (BarSet) arrayList2.get(i10);
                Bar bar = (Bar) barSet.getEntry(i9);
                float abs = Math.abs(zeroPosition - bar.getX());
                if (!barSet.isVisible() || bar.getValue() == 0.0f || abs < f3) {
                    f = f5;
                    i4 = discoverTopSet;
                    i3 = size;
                    i2 = size2;
                    i = i9;
                    i6 = i10;
                    i5 = discoverBottomSet;
                    f4 = f4;
                } else {
                    this.style.barPaint.setColor(bar.getColor());
                    Paint paint = this.style.barPaint;
                    float alpha = barSet.getAlpha();
                    float f8 = f4;
                    float f9 = alpha;
                    float f10 = f5;
                    int i11 = i10;
                    int i12 = discoverTopSet;
                    int i13 = discoverBottomSet;
                    applyShadow(paint, f9, bar.getShadowDx(), bar.getShadowDy(), bar.getShadowRadius(), bar.getShadowColor());
                    float y = bar.getY() - (this.barWidth / f3);
                    float y2 = bar.getY() + (this.barWidth / f3);
                    if (bar.getValue() > 0.0f) {
                        float f11 = zeroPosition + (abs - f6);
                        int i14 = i11;
                        int i15 = i13;
                        if (i14 == i15) {
                            int i16 = (int) y;
                            int i17 = (int) f11;
                            int i18 = (int) y2;
                            int i19 = i15;
                            float f12 = (float) i18;
                            int i20 = i18;
                            i3 = size;
                            int i21 = i16;
                            i2 = size2;
                            i6 = i14;
                            i = i9;
                            i5 = i19;
                            drawBar(canvas, (float) ((int) f10), (float) i16, (float) i17, f12);
                            int i22 = i12;
                            if (!(i5 == i22 || this.style.cornerRadius == 0.0f)) {
                                canvas2.drawRect(new Rect((int) (f11 - ((f11 - f10) / 2.0f)), i21, i17, i20), this.style.barPaint);
                            }
                            i7 = i22;
                        } else {
                            i3 = size;
                            i2 = size2;
                            i = i9;
                            i6 = i14;
                            i5 = i15;
                            int i23 = i12;
                            if (i6 == i23) {
                                int i24 = (int) f10;
                                int i25 = (int) y;
                                int i26 = (int) y2;
                                Canvas canvas3 = canvas;
                                i7 = i23;
                                drawBar(canvas3, (float) i24, (float) i25, (float) ((int) f11), (float) i26);
                                canvas2.drawRect(new Rect(i24, i25, (int) (f10 + ((f11 - f10) / 2.0f)), i26), this.style.barPaint);
                            } else {
                                i7 = i23;
                                canvas2.drawRect(new Rect((int) f10, (int) y, (int) f11, (int) y2), this.style.barPaint);
                            }
                        }
                        if (abs != 0.0f) {
                            f6 -= abs - 0.0f;
                        }
                        f = f11;
                        f4 = f8;
                        i4 = i7;
                    } else {
                        i3 = size;
                        i2 = size2;
                        i = i9;
                        i6 = i11;
                        int i27 = i12;
                        i5 = i13;
                        float f13 = abs + f7;
                        float f14 = zeroPosition - f13;
                        if (i6 == i5) {
                            int i28 = (int) y;
                            float f15 = f8;
                            int i29 = (int) f15;
                            int i30 = (int) y2;
                            float f16 = (float) i30;
                            f2 = f13;
                            int i31 = i30;
                            f = f10;
                            int i32 = i29;
                            drawBar(canvas, (float) ((int) f14), (float) i28, (float) i29, f16);
                            int i33 = i27;
                            if (!(i5 == i33 || this.style.cornerRadius == 0.0f)) {
                                canvas2.drawRect(new Rect((int) (f15 - ((f15 - f14) / 2.0f)), i28, i32, i31), this.style.barPaint);
                            }
                            i4 = i33;
                        } else {
                            f2 = f13;
                            f = f10;
                            float f17 = f8;
                            int i34 = i27;
                            if (i6 == i34) {
                                int i35 = (int) f14;
                                int i36 = (int) y;
                                int i37 = (int) y2;
                                Canvas canvas4 = canvas;
                                i4 = i34;
                                drawBar(canvas4, (float) i35, (float) i36, (float) ((int) f17), (float) i37);
                                canvas2.drawRect(new Rect(i35, i36, (int) (((f17 - f14) / 2.0f) + f14), i37), this.style.barPaint);
                            } else {
                                i4 = i34;
                                canvas2.drawRect(new Rect((int) f14, (int) y, (int) f17, (int) y2), this.style.barPaint);
                            }
                        }
                        if (abs != 0.0f) {
                            f4 = f14;
                            f7 = f2;
                        } else {
                            f4 = f14;
                        }
                    }
                }
                i10 = i6 + 1;
                discoverBottomSet = i5;
                discoverTopSet = i4;
                size = i3;
                size2 = i2;
                i9 = i;
                f5 = f;
                arrayList2 = arrayList;
                f3 = 2.0f;
            }
            int i38 = size;
            int i39 = size2;
            i9++;
            arrayList2 = arrayList;
            i8 = 0;
        }
    }

    @Override
    public void onPreDrawChart(ArrayList<ChartSet> arrayList) {
        if (arrayList.get(0).size() == 1) {
            this.barWidth = (getInnerChartBottom() - getInnerChartTop()) - (getBorderSpacing() * 2.0f);
        } else {
            calculateBarsWidth(-1, arrayList.get(0).getEntry(1).getY(), arrayList.get(0).getEntry(0).getY());
        }
    }

    @Override
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
                float abs = Math.abs(zeroPosition - bar.getX());
                if (!barSet.isVisible()) {
                    i2 = size;
                    i = size2;
                } else if (bar.getValue() > f) {
                    float f6 = (abs - f4) + zeroPosition;
                    i2 = size;
                    i = size2;
                    arrayList3.get(i5).add(new Region((int) f2, (int) (bar.getY() - (this.barWidth / 2.0f)), (int) f6, (int) (bar.getY() + (this.barWidth / 2.0f))));
                    f4 -= abs - 2.0f;
                    f2 = f6;
                } else {
                    i2 = size;
                    i = size2;
                    if (bar.getValue() < 0.0f) {
                        float f7 = abs + f5;
                        float f8 = zeroPosition - f7;
                        arrayList3.get(i5).add(new Region((int) f8, (int) (bar.getY() - (this.barWidth / 2.0f)), (int) f3, (int) (bar.getY() + (this.barWidth / 2.0f))));
                        f3 = f8;
                        f5 = f7;
                    } else {
                        arrayList3.get(i5).add(new Region((int) f2, (int) (bar.getY() - (this.barWidth / 2.0f)), (int) ((1.0f - f4) + zeroPosition), (int) (bar.getY() + (this.barWidth / 2.0f))));
                    }
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
