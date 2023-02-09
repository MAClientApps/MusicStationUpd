package com.lakshitasuman.chart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import androidx.annotation.FloatRange;

import com.lakshitasuman.chart.R;
import com.lakshitasuman.chart.Tools;
import com.lakshitasuman.chart.model.ChartEntry;
import com.lakshitasuman.chart.model.ChartSet;
import com.lakshitasuman.chart.model.LineSet;
import com.lakshitasuman.chart.model.Point;

import java.util.ArrayList;
import java.util.Iterator;

public class LineChartView extends ChartView {
    private float mClickableRadius;
    private final Style mStyle;
    public static final int[] ChartAttrs = {R.attr.chart_axisBorderSpacing, R.attr.chart_axisColor, R.attr.chart_axisThickness, R.attr.chart_axisTopSpacing, R.attr.chart_fontSize, R.attr.chart_labelColor, R.attr.chart_labels, R.attr.chart_shadowColor, R.attr.chart_shadowDx, R.attr.chart_shadowDy, R.attr.chart_shadowRadius, R.attr.chart_typeface};


    private static int si(int i, int i2) {
        int i3 = i - 1;
        if (i2 > i3) {
            return i3;
        }
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    public LineChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOrientation(Orientation.VERTICAL);
        mStyle = new Style(context.getTheme().obtainStyledAttributes(attributeSet,ChartAttrs, 0, 0));
        mClickableRadius = context.getResources().getDimension(R.dimen.dot_region_radius);
    }

    public LineChartView(Context context) {
        super(context);
        setOrientation(Orientation.VERTICAL);
        mStyle = new Style();
        mClickableRadius = context.getResources().getDimension(R.dimen.dot_region_radius);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mStyle.init();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mStyle.clean();
    }

    @Override
    public void onDrawChart(Canvas canvas, ArrayList<ChartSet> arrayList) {
        Path path;
        Iterator<ChartSet> it = arrayList.iterator();
        while (it.hasNext()) {
            LineSet lineSet = (LineSet) it.next();
            if (lineSet.isVisible()) {
                mStyle.mLinePaint.setColor(lineSet.getColor());
                mStyle.mLinePaint.setStrokeWidth(lineSet.getThickness());
                applyShadow(mStyle.mLinePaint, lineSet.getAlpha(), lineSet.getShadowDx(), lineSet.getShadowDy(), lineSet.getShadowRadius(), lineSet.getShadowColor());
                if (lineSet.isDashed()) {
                    mStyle.mLinePaint.setPathEffect(new DashPathEffect(lineSet.getDashedIntervals(), (float) lineSet.getDashedPhase()));
                } else {
                    mStyle.mLinePaint.setPathEffect((PathEffect) null);
                }
                if (!lineSet.isSmooth()) {
                    path = createLinePath(lineSet);
                } else {
                    path = createSmoothLinePath(lineSet);
                }
                if (lineSet.hasFill() || lineSet.hasGradientFill()) {
                    canvas.drawPath(createBackgroundPath(new Path(path), lineSet), mStyle.mFillPaint);
                }
                canvas.drawPath(path, mStyle.mLinePaint);
                drawPoints(canvas, lineSet);
            }
        }
    }

    @Override
    public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> arrayList) {
        ArrayList<ArrayList<Region>> arrayList2 = new ArrayList<>(arrayList.size());
        Iterator<ChartSet> it = arrayList.iterator();
        while (it.hasNext()) {
            ChartSet next = it.next();
            ArrayList arrayList3 = new ArrayList(next.size());
            Iterator<ChartEntry> it2 = next.getEntries().iterator();
            while (it2.hasNext()) {
                ChartEntry next2 = it2.next();
                float x = next2.getX();
                float y = next2.getY();
                arrayList3.add(new Region((int) (x - mClickableRadius), (int) (y - mClickableRadius), (int) (x + mClickableRadius), (int) (y + mClickableRadius)));
            }
            arrayList2.add(arrayList3);
        }
        return arrayList2;
    }


    private void drawPoints(Canvas canvas, LineSet lineSet) {
        int end = lineSet.getEnd();
        for (int begin = lineSet.getBegin(); begin < end; begin++) {
            Point point = (Point) lineSet.getEntry(begin);
            if (point.isVisible()) {
                mStyle.mDotsPaint.setColor(point.getColor());
                mStyle.mDotsPaint.setAlpha((int) (lineSet.getAlpha() * 255.0f));
                applyShadow(mStyle.mDotsPaint, lineSet.getAlpha(), point.getShadowDx(), point.getShadowDy(), point.getShadowRadius(), point.getShadowColor());
                canvas.drawCircle(point.getX(), point.getY(), point.getRadius(), mStyle.mDotsPaint);
                if (point.hasStroke()) {
                    mStyle.mDotsStrokePaint.setStrokeWidth(point.getStrokeThickness());
                    mStyle.mDotsStrokePaint.setColor(point.getStrokeColor());
                    mStyle.mDotsStrokePaint.setAlpha((int) (lineSet.getAlpha() * 255.0f));
                    applyShadow(mStyle.mDotsStrokePaint, lineSet.getAlpha(), point.getShadowDx(), point.getShadowDy(), point.getShadowRadius(), point.getShadowColor());
                    canvas.drawCircle(point.getX(), point.getY(), point.getRadius(), mStyle.mDotsStrokePaint);
                }
                if (point.getDrawable() != null) {
                    Bitmap drawableToBitmap = Tools.drawableToBitmap(point.getDrawable());
                    canvas.drawBitmap(drawableToBitmap, point.getX() - ((float) (drawableToBitmap.getWidth() / 2)), point.getY() - ((float) (drawableToBitmap.getHeight() / 2)), mStyle.mDotsPaint);
                }
            }
        }
    }




    public Path createLinePath(LineSet lineSet) {
        Path path = new Path();
        int begin = lineSet.getBegin();
        int end = lineSet.getEnd();
        for (int i = begin; i < end; i++) {
            if (i == begin) {
                path.moveTo(lineSet.getEntry(i).getX(), lineSet.getEntry(i).getY());
            } else {
                path.lineTo(lineSet.getEntry(i).getX(), lineSet.getEntry(i).getY());
            }
        }
        return path;
    }



    public Path createSmoothLinePath(LineSet lineSet) {
        Path path = new Path();
        path.moveTo(lineSet.getEntry(lineSet.getBegin()).getX(), lineSet.getEntry(lineSet.getBegin()).getY());
        int begin = lineSet.getBegin();
        int end = lineSet.getEnd();
        while (begin < end - 1) {
            float x = lineSet.getEntry(begin).getX();
            float y = lineSet.getEntry(begin).getY();
            int i = begin + 1;
            float x2 = lineSet.getEntry(i).getX();
            float y2 = lineSet.getEntry(i).getY();
            int i2 = begin - 1;
            int i3 = begin + 2;
            float x3 = lineSet.getEntry(si(lineSet.size(), i3)).getX() - x;
            path.cubicTo(x + ((x2 - lineSet.getEntry(si(lineSet.size(), i2)).getX()) * 0.15f), y + ((y2 - lineSet.getEntry(si(lineSet.size(), i2)).getY()) * 0.15f), x2 - (x3 * 0.15f), y2 - ((lineSet.getEntry(si(lineSet.size(), i3)).getY() - y) * 0.15f), x2, y2);
            begin = i;
        }
        return path;
    }


    private Path createBackgroundPath(Path path, LineSet lineSet) {
        mStyle.mFillPaint.setAlpha((int) (lineSet.getAlpha() * 255.0f));
        if (lineSet.hasFill()) {
            mStyle.mFillPaint.setColor(lineSet.getFillColor());
        }
        if (lineSet.hasGradientFill()) {
            mStyle.mFillPaint.setShader(new LinearGradient(super.getInnerChartLeft(), super.getInnerChartTop(), super.getInnerChartLeft(), super.getInnerChartBottom(), lineSet.getGradientColors(), lineSet.getGradientPositions(), Shader.TileMode.MIRROR));
        }
        path.lineTo(lineSet.getEntry(lineSet.getEnd() - 1).getX(), super.getInnerChartBottom());
        path.lineTo(lineSet.getEntry(lineSet.getBegin()).getX(), super.getInnerChartBottom());
        path.close();
        return path;
    }

    public LineChartView setClickablePointRadius(@FloatRange(from = 0.0d) float f) {
        mClickableRadius = f;
        return this;
    }

    class Style {
        
        public Paint mDotsPaint;
        
        public Paint mDotsStrokePaint;
        
        public Paint mFillPaint;
        
        public Paint mLinePaint;

        Style() {
        }

        Style(TypedArray typedArray) {
        }

        
        public void init() {
            mDotsPaint = new Paint();
            mDotsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mDotsPaint.setAntiAlias(true);
            mDotsStrokePaint = new Paint();
            mDotsStrokePaint.setStyle(Paint.Style.STROKE);
            mDotsStrokePaint.setAntiAlias(true);
            mLinePaint = new Paint();
            mLinePaint.setStyle(Paint.Style.STROKE);
            mLinePaint.setAntiAlias(true);
            mFillPaint = new Paint();
            mFillPaint.setStyle(Paint.Style.FILL);
        }

        
        public void clean() {
            mLinePaint = null;
            mFillPaint = null;
            mDotsPaint = null;
        }
    }
}
