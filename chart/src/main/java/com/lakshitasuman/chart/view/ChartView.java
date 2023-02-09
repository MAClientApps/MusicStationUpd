package com.lakshitasuman.chart.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.lakshitasuman.chart.R;
import com.lakshitasuman.chart.animation.Animation;
import com.lakshitasuman.chart.animation.style.BaseStyleAnimation;
import com.lakshitasuman.chart.listener.OnEntryClickListener;
import com.lakshitasuman.chart.model.ChartSet;
import com.lakshitasuman.chart.renderer.AxisRenderer;
import com.lakshitasuman.chart.renderer.XRenderer;
import com.lakshitasuman.chart.renderer.YRenderer;
import com.lakshitasuman.chart.tooltip.Tooltip;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class ChartView extends RelativeLayout {
    private static final int DEFAULT_GRID_COLUMNS = 5;
    private static final int DEFAULT_GRID_ROWS = 5;
    public static final int[] ChartAttrs = {R.attr.chart_axisBorderSpacing, R.attr.chart_axisColor, R.attr.chart_axisThickness, R.attr.chart_axisTopSpacing, R.attr.chart_fontSize, R.attr.chart_labelColor, R.attr.chart_labels, R.attr.chart_shadowColor, R.attr.chart_shadowDx, R.attr.chart_shadowDy, R.attr.chart_shadowRadius, R.attr.chart_typeface};

    private static final String TAG = "chart.view.ChartView";
    Context ctx;
    ArrayList<ChartSet> data;
    private final ViewTreeObserver.OnPreDrawListener drawListener = new ViewTreeObserver.OnPreDrawListener() {
        @SuppressLint({"NewApi"})
        @Override
        public boolean onPreDraw() {
            getViewTreeObserver().removeOnPreDrawListener(this);
            style.init();
            yRndr.init(data, style);
            xRndr.init(data, style);
            mChartLeft = getPaddingLeft();
            mChartTop = getPaddingTop() + (style.fontMaxHeight / 2);
            mChartRight = getMeasuredWidth() - getPaddingRight();
             mChartBottom = getMeasuredHeight() - getPaddingBottom();
            yRndr.measure(mChartLeft, mChartTop, mChartRight, mChartBottom);
            xRndr.measure(mChartLeft, mChartTop, mChartRight, mChartBottom);
            float[] negotiateInnerChartBounds = negotiateInnerChartBounds(yRndr.getInnerChartBounds(), xRndr.getInnerChartBounds());
            yRndr.setInnerChartBounds(negotiateInnerChartBounds[0], negotiateInnerChartBounds[1], negotiateInnerChartBounds[2], negotiateInnerChartBounds[3]);
            xRndr.setInnerChartBounds(negotiateInnerChartBounds[0], negotiateInnerChartBounds[1], negotiateInnerChartBounds[2], negotiateInnerChartBounds[3]);
            yRndr.dispose();
            xRndr.dispose();
            if (mHasThresholdValue) {
                 mThresholdStartValue = yRndr.parsePos(0, (double) mThresholdStartValue);mThresholdEndValue = yRndr.parsePos(0, (double) mThresholdEndValue);
            }
            digestData();
            onPreDrawChart(data);
            mRegions = defineRegions(data);
            if (mAnim != null) {
                data = mAnim.prepareEnterAnimation(ChartView.this);
            }
            if (Build.VERSION.SDK_INT >= 11) {
                setLayerType(LAYER_TYPE_SOFTWARE, (Paint) null);
            }
            return mReadyToDraw = true;
        }
    };
    
    public Animation mAnim;
    
    public int mChartBottom;
    
    public int mChartLeft;
    
    public OnClickListener mChartListener;
    
    public int mChartRight;
    
    public int mChartTop;
    
    public OnEntryClickListener mEntryListener;
    private GestureDetector mGestureDetector;
    private int mGridNColumns;
    private int mGridNRows;
    private GridType mGridType;
    private boolean mHasThresholdLabel;
    
    public boolean mHasThresholdValue;
    private boolean mIsDrawing;
    private Orientation mOrientation;
    
    public boolean mReadyToDraw;
    
    public ArrayList<ArrayList<Region>> mRegions;
    private int mThresholdEndLabel;
    
    public float mThresholdEndValue;
    private int mThresholdStartLabel;
    
    public float mThresholdStartValue;
    
    public Tooltip mTooltip;
    final Style style;
    final XRenderer xRndr;
    final YRenderer yRndr;

    public enum GridType {
        FULL,
        VERTICAL,
        HORIZONTAL,
        NONE
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    
    public abstract void onDrawChart(Canvas canvas, ArrayList<ChartSet> arrayList);

    
    public void onPreDrawChart(ArrayList<ChartSet> arrayList) {
    }

    public ChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
        ctx = context;
        xRndr = new XRenderer();
        yRndr = new YRenderer();
        style = new Style(context.getTheme().obtainStyledAttributes(attributeSet,ChartAttrs, 0, 0));
    }

    public ChartView(Context context) {
        super(context);
        init();
        ctx = context;
        xRndr = new XRenderer();
        yRndr = new YRenderer();
        style = new Style();
    }

    private void init() {
        mReadyToDraw = false;
        mHasThresholdValue = false;
        mHasThresholdLabel = false;
        mIsDrawing = false;
        data = new ArrayList<>();
        mRegions = new ArrayList<>();
        mGridType = GridType.NONE;
        mGridNRows = 5;
        mGridNColumns = 5;
        mGestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setWillNotDraw(false);
        style.init();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        style.clean();
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        if (mode == Integer.MIN_VALUE) {
            i = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        }
        if (mode2 == Integer.MIN_VALUE) {
            i2 = 100;
        }
        setMeasuredDimension(i, i2);
    }
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent motionEvent) {
        return (mAnim != null && mAnim.isPlaying()) || mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public void onDraw(Canvas canvas) {
        mIsDrawing = true;
        super.onDraw(canvas);
        if (mReadyToDraw) {
            if (mGridType == GridType.FULL || mGridType == GridType.VERTICAL) {
                drawVerticalGrid(canvas);
            }
            if (mGridType == GridType.FULL || mGridType == GridType.HORIZONTAL) {
                drawHorizontalGrid(canvas);
            }
            if (mHasThresholdValue) {
                drawThreshold(canvas, getInnerChartLeft(), mThresholdStartValue, getInnerChartRight(), mThresholdEndValue);
            }
            if (mHasThresholdLabel) {
                drawThreshold(canvas, data.get(0).getEntry(mThresholdStartLabel).getX(), getInnerChartTop(), data.get(0).getEntry(mThresholdEndLabel).getX(), getInnerChartBottom());
            }
            if (!data.isEmpty()) {
                onDrawChart(canvas, data);
            }
            yRndr.draw(canvas);
            xRndr.draw(canvas);
        }
        mIsDrawing = false;
    }

    
    public void digestData() {
        int size = data.get(0).size();
        Iterator<ChartSet> it = data.iterator();
        while (it.hasNext()) {
            ChartSet next = it.next();
            for (int i = 0; i < size; i++) {
                next.getEntry(i).setCoordinates(xRndr.parsePos(i, (double) next.getValue(i)), yRndr.parsePos(i, (double) next.getValue(i)));
            }
        }
    }

    
    public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> arrayList) {
        return mRegions;
    }

    public void addData(ChartSet chartSet) {
        if (!data.isEmpty() && chartSet.size() != data.get(0).size()) {
            throw new IllegalArgumentException("The number of entries between sets doesn't match.");
        } else if (chartSet != null) {
            data.add(chartSet);
        } else {
            throw new IllegalArgumentException("Chart data set can't be null.");
        }
    }

    public void addData(ArrayList<ChartSet> arrayList) {
        data = arrayList;
    }

    private void display() {
        getViewTreeObserver().addOnPreDrawListener(drawListener);
        postInvalidate();
    }


    public void show() {
        Iterator<ChartSet> it = data.iterator();
        while (it.hasNext()) {
            it.next().setVisible(true);
        }
        display();
    }



    public void show(int i) {
        data.get(i).setVisible(true);
        display();
    }

    public void show(Animation animation) {
        mAnim = animation;
        show();
    }

    public void dismiss() {
        dismiss(mAnim);
    }

    public void dismiss(int i) {
        data.get(i).setVisible(false);
        invalidate();
    }


    public void dismiss(Animation animation) {
        if (animation != null) {
            mAnim = animation;
            final Runnable endAction = mAnim.getEndAction();
            mAnim.setEndAction(new Runnable() {
                public void run() {
                    if (endAction != null) {
                        endAction.run();
                    }
                    data.clear();
                    invalidate();
                }
            });
            data = mAnim.prepareExitAnimation(this);
        } else {
            data.clear();
        }
        invalidate();
    }

    public void reset() {
        if (mAnim != null && mAnim.isPlaying()) {
            mAnim.cancel();
        }
        init();
        if (xRndr.hasMandatoryBorderSpacing()) {
            xRndr.reset();
        }
        if (yRndr.hasMandatoryBorderSpacing()) {
            yRndr.reset();
        }
        mHasThresholdLabel = false;
        mHasThresholdValue = false;
        style.thresholdPaint = null;
        style.gridPaint = null;
    }

    public ChartView updateValues(int i, float[] fArr) {
        if (fArr.length == data.get(i).size()) {
            data.get(i).updateValues(fArr);
            return this;
        }
        throw new IllegalArgumentException("New values size doesn't match current dataset size.");
    }

    public void notifyDataUpdate() {
        if ((mAnim == null || mAnim.isPlaying() || !mReadyToDraw) && (mAnim != null || !mReadyToDraw)) {
            Log.w(TAG, "Unexpected data update notification. Chart is still not displayed or still displaying.");
            return;
        }
        ArrayList arrayList = new ArrayList(data.size());
        ArrayList arrayList2 = new ArrayList(data.size());
        Iterator<ChartSet> it = data.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getScreenPoints());
        }
        digestData();
        Iterator<ChartSet> it2 = data.iterator();
        while (it2.hasNext()) {
            arrayList2.add(it2.next().getScreenPoints());
        }
        mRegions = defineRegions(data);
        if (mAnim != null) {
            data = mAnim.prepareUpdateAnimation(this, arrayList, arrayList2);
        }
        invalidate();
    }





    public void toggleTooltip(Rect rect, float f) {
        if (!mTooltip.on()) {
            mTooltip.prepare(rect, f);
            showTooltip(mTooltip, true);
            return;
        }
        dismissTooltip(mTooltip, rect, f);
    }

    public void showTooltip(Tooltip tooltip, boolean bool) {
        if (bool) {
            tooltip.correctPosition(mChartLeft, mChartTop, mChartRight, mChartBottom);
        }
        if (tooltip.hasEnterAnimation()) {
            tooltip.animateEnter();
        }
        addTooltip(tooltip);
    }

    private void addTooltip(Tooltip tooltip) {
        addView(tooltip);
        tooltip.setOn(true);
    }

    
    public void removeTooltip(Tooltip tooltip) {
        removeView(tooltip);
        tooltip.setOn(false);
    }

    
    public void dismissTooltip(Tooltip tooltip) {
        dismissTooltip(tooltip, (Rect) null, 0.0f);
    }

    private void dismissTooltip(final Tooltip tooltip, final Rect rect, final float f) {
        if (tooltip.hasExitAnimation()) {
            tooltip.animateExit(new Runnable() {
                public void run() {
                    removeTooltip(tooltip);
                    if (rect != null) {
                        toggleTooltip(rect, f);
                    }
                }
            });
            return;
        }
        removeTooltip(tooltip);
        if (rect != null) {
            toggleTooltip(rect, f);
        }
    }

    public void dismissAllTooltips() {
        removeAllViews();
        if (mTooltip != null) {
            mTooltip.setOn(false);
        }
    }

    public void animateSet(int i, BaseStyleAnimation baseStyleAnimation) {
        baseStyleAnimation.play(this, data.get(i));
    }

    public boolean canIPleaseAskYouToDraw() {
        return !mIsDrawing;
    }

    
    public float[] negotiateInnerChartBounds(float[] fArr, float[] fArr2) {
        float[] fArr3 = new float[4];
        fArr3[0] = fArr[0] > fArr2[0] ? fArr[0] : fArr2[0];
        fArr3[1] = fArr[1] > fArr2[1] ? fArr[1] : fArr2[1];
        fArr3[2] = fArr[2] < fArr2[2] ? fArr[2] : fArr2[2];
        fArr3[3] = fArr[3] < fArr2[3] ? fArr[3] : fArr2[3];
        return fArr3;
    }

    private void drawThreshold(Canvas canvas, float f, float f2, float f3, float f4) {
        if (f == f3 || f2 == f4) {
            canvas.drawLine(f, f2, f3, f4, style.thresholdPaint);
            return;
        }
        canvas.drawRect(f, f2, f3, f4, style.thresholdPaint);
    }

    private void drawVerticalGrid(Canvas canvas) {
        float innerChartRight = (getInnerChartRight() - getInnerChartLeft()) / ((float) mGridNColumns);
        float innerChartLeft = getInnerChartLeft();
        if (style.hasYAxis) {
            innerChartLeft += innerChartRight;
        }
        while (innerChartLeft < getInnerChartRight()) {
            canvas.drawLine(innerChartLeft, getInnerChartTop(), innerChartLeft, getInnerChartBottom(), style.gridPaint);
            innerChartLeft += innerChartRight;
        }
        canvas.drawLine(getInnerChartRight(), getInnerChartTop(), getInnerChartRight(), getInnerChartBottom(), style.gridPaint);
    }

    private void drawHorizontalGrid(Canvas canvas) {
        float innerChartBottom = (getInnerChartBottom() - getInnerChartTop()) / ((float) mGridNRows);
        for (float innerChartTop = getInnerChartTop(); innerChartTop < getInnerChartBottom(); innerChartTop += innerChartBottom) {
            canvas.drawLine(getInnerChartLeft(), innerChartTop, getInnerChartRight(), innerChartTop, style.gridPaint);
        }
        if (!style.hasXAxis) {
            canvas.drawLine(getInnerChartLeft(), getInnerChartBottom(), getInnerChartRight(), getInnerChartBottom(), style.gridPaint);
        }
    }


    public Orientation getOrientation() {
        return mOrientation;
    }

    public float getInnerChartBottom() {
        return yRndr.getInnerChartBottom();
    }

    public float getInnerChartLeft() {
        return xRndr.getInnerChartLeft();
    }

    public float getInnerChartRight() {
        return xRndr.getInnerChartRight();
    }

    public float getInnerChartTop() {
        return yRndr.getInnerChartTop();
    }

    public float getZeroPosition() {
        AxisRenderer axisRenderer;
        if (mOrientation == Orientation.VERTICAL) {
            axisRenderer = yRndr;
        } else {
            axisRenderer = xRndr;
        }
        if (axisRenderer.getBorderMinimumValue() > 0) {
            return axisRenderer.parsePos(0, (double) axisRenderer.getBorderMinimumValue());
        }
        if (axisRenderer.getBorderMaximumValue() < 0) {
            return axisRenderer.parsePos(0, (double) axisRenderer.getBorderMaximumValue());
        }
        return axisRenderer.parsePos(0, 0.0d);
    }

    
    public int getStep() {
        if (mOrientation == Orientation.VERTICAL) {
            return yRndr.getStep();
        }
        return xRndr.getStep();
    }

    
    public float getBorderSpacing() {
        if (mOrientation == Orientation.VERTICAL) {
            return xRndr.getBorderSpacing();
        }
        return yRndr.getBorderSpacing();
    }

    public ArrayList<ChartSet> getData() {
        return data;
    }

    public ArrayList<Rect> getEntriesArea(int i) {
        ArrayList<Rect> arrayList = new ArrayList<>(mRegions.get(i).size());
        Iterator it = mRegions.get(i).iterator();
        while (it.hasNext()) {
            arrayList.add(getEntryRect((Region) it.next()));
        }
        return arrayList;
    }

    
    public Rect getEntryRect(Region region) {
        return new Rect(region.getBounds().left - getPaddingLeft(), region.getBounds().top - getPaddingTop(), region.getBounds().right - getPaddingLeft(), region.getBounds().bottom - getPaddingTop());
    }

    public Animation getChartAnimation() {
        return mAnim;
    }

    
    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
        if (mOrientation == Orientation.VERTICAL) {
            yRndr.setHandleValues(true);
        } else {
            xRndr.setHandleValues(true);
        }
    }

    public ChartView setYLabels(AxisRenderer.LabelPosition labelPosition) {
        yRndr.setLabelsPositioning(labelPosition);
        return this;
    }

    public ChartView setXLabels(AxisRenderer.LabelPosition labelPosition) {
        xRndr.setLabelsPositioning(labelPosition);
        return this;
    }

    public ChartView setLabelsFormat(DecimalFormat decimalFormat) {
        if (mOrientation == Orientation.VERTICAL) {
            yRndr.setLabelsFormat(decimalFormat);
        } else {
            xRndr.setLabelsFormat(decimalFormat);
        }
        return this;
    }

    public ChartView setLabelsColor(@ColorInt int i) {
        style.labelsColor = i;
        return this;
    }

    public ChartView setFontSize(@IntRange(from = 0) int i) {
        style.fontSize = (float) i;
        return this;
    }

    public ChartView setTypeface(Typeface typeface) {
         style.typeface = typeface;
        return this;
    }

    public ChartView setXAxis(boolean XAxis) {
         style.hasXAxis = XAxis;
        return this;
    }

    public ChartView setYAxis(boolean YAxis) {
         style.hasYAxis = YAxis;
        return this;
    }

    public ChartView setAxisBorderValues(int i, int i2, int i3) {
        if (mOrientation == Orientation.VERTICAL) {
            yRndr.setBorderValues(i, i2, i3);
        } else {
            xRndr.setBorderValues(i, i2, i3);
        }
        return this;
    }

    public ChartView setAxisBorderValues(int i, int i2) {
        if (mOrientation == Orientation.VERTICAL) {
            yRndr.setBorderValues(i, i2);
        } else {
            xRndr.setBorderValues(i, i2);
        }
        return this;
    }

    public ChartView setAxisThickness(@FloatRange(from = 0.0d) float f) {
        style.axisThickness = f;
        return this;
    }

    public ChartView setAxisColor(@ColorInt int i) {
        style.axisColor = i;
        return this;
    }

    public ChartView setStep(int i) {
        if (i > 0) {
            if (mOrientation == Orientation.VERTICAL) {
                yRndr.setStep(i);
            } else {
                xRndr.setStep(i);
            }
            return this;
        }
        throw new IllegalArgumentException("Step can't be lower or equal to 0");
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {
        mEntryListener = onEntryClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mChartListener = onClickListener;
    }

    public ChartView setBorderSpacing(float f) {
        if (mOrientation == Orientation.VERTICAL) {
            xRndr.setBorderSpacing(f);
        } else {
            yRndr.setBorderSpacing(f);
        }
        return this;
    }

    public ChartView setTopSpacing(float f) {
        if (mOrientation == Orientation.VERTICAL) {
            yRndr.setTopSpacing(f);
        } else {
            xRndr.setBorderSpacing(f);
        }
        return this;
    }

    public ChartView setGrid(GridType gridType, Paint paint) {
        mGridType = gridType;
        style.gridPaint = paint;
        return this;
    }

    public ChartView setGrid(GridType gridType, @IntRange(from = 1) int i, @IntRange(from = 1) int i2, Paint paint) {
        if (i < 1 || i2 < 1) {
            throw new IllegalArgumentException("Number of rows/columns can't be lesser than 1.");
        }
        mGridType = gridType;
        mGridNRows = i;
        mGridNColumns = i2;
        style.gridPaint = paint;
        return this;
    }

    public ChartView setValueThreshold(float f, float f2, Paint paint) {
        mHasThresholdValue = true;
        mThresholdStartValue = f;
        mThresholdEndValue = f2;
        style.thresholdPaint = paint;
        return this;
    }

    public ChartView setLabelThreshold(int i, int i2, Paint paint) {
        mHasThresholdLabel = true;
        mThresholdStartLabel = i;
        mThresholdEndLabel = i2;
        style.thresholdPaint = paint;
        return this;
    }

    public ChartView setAxisLabelsSpacing(float f) {
        xRndr.setAxisLabelsSpacing(f);
        yRndr.setAxisLabelsSpacing(f);
        return this;
    }

    
    public void setMandatoryBorderSpacing() {
        if (mOrientation == Orientation.VERTICAL) {
            xRndr.setMandatoryBorderSpacing(true);
        } else {
            yRndr.setMandatoryBorderSpacing(true);
        }
    }

    public ChartView setTooltips(Tooltip tooltip) {
        mTooltip = tooltip;
        return this;
    }

    
    public void setClickableRegions(ArrayList<ArrayList<Region>> arrayList) {
        mRegions = arrayList;
    }

    public void applyShadow(Paint paint, float f, float f2, float f3, float f4, int[] iArr) {
        int i = (int) (f * 255.0f);
        paint.setAlpha(i);
        if (i >= iArr[0]) {
            i = iArr[0];
        }
        paint.setShadowLayer(f4, f2, f3, Color.argb(i, iArr[1], iArr[2], iArr[3]));
    }



    public class Style {
        private static final int DEFAULT_COLOR = -16777216;
        
        public int axisColor;
        
        public float axisThickness;
        private Paint chartPaint;
        
        public int fontMaxHeight;
        
        public float fontSize;
        
        public Paint gridPaint;
        
        public boolean hasXAxis;
        
        public boolean hasYAxis;
        
        public int labelsColor;
        private Paint labelsPaint;
        
        public Paint thresholdPaint;
        
        public Typeface typeface;

        Style() {
            axisColor = -16777216;
            axisThickness = ctx.getResources().getDimension(R.dimen.grid_thickness);
            hasXAxis = true;
            hasYAxis = true;
            labelsColor = -16777216;
            fontSize = ctx.getResources().getDimension(R.dimen.font_size);
        }

        Style(TypedArray typedArray) {
            axisColor = typedArray.getColor(1, -16777216);
            axisThickness = typedArray.getDimension(2, getResources().getDimension(R.dimen.axis_thickness));
            hasXAxis = true;
            hasYAxis = true;
            labelsColor = typedArray.getColor(5, -16777216);
            fontSize = typedArray.getDimension(4, getResources().getDimension(R.dimen.font_size));
            String string = typedArray.getString(11);
            if (string != null) {
                typeface = Typeface.createFromAsset(getResources().getAssets(), string);
            }
        }

        
        public void init() {
            chartPaint = new Paint();
            chartPaint.setColor(axisColor);
            chartPaint.setStyle(Paint.Style.STROKE);
            chartPaint.setStrokeWidth(axisThickness);
            chartPaint.setAntiAlias(true);
            labelsPaint = new Paint();
            labelsPaint.setColor(labelsColor);
            labelsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            labelsPaint.setAntiAlias(true);
            labelsPaint.setTextSize(fontSize);
            labelsPaint.setTypeface(typeface);
            fontMaxHeight = (int) (style.labelsPaint.descent() - style.labelsPaint.ascent());
        }

        public void clean() {
            chartPaint = null;
            labelsPaint = null;
            gridPaint = null;
            thresholdPaint = null;
        }

        public int getLabelHeight(String str) {
            Rect rect = new Rect();
            style.labelsPaint.getTextBounds(str, 0, str.length(), rect);
            return rect.height();
        }

        public Paint getChartPaint() {
            return chartPaint;
        }

        public float getAxisThickness() {
            return axisThickness;
        }

        public boolean hasXAxis() {
            return hasXAxis;
        }

        public boolean hasYAxis() {
            return hasYAxis;
        }

        public Paint getLabelsPaint() {
            return labelsPaint;
        }

        public int getFontMaxHeight() {
            return fontMaxHeight;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        private GestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (!(mEntryListener == null && mTooltip == null)) {
                int size = mRegions.size();
                int size2 = ((ArrayList) mRegions.get(0)).size();
                for (int i = 0; i < size; i++) {
                    for (int i2 = 0; i2 < size2; i2++) {
                        if (((Region) ((ArrayList) mRegions.get(i)).get(i2)).contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            if (mEntryListener != null) {
                                mEntryListener.onClick(i, i2, getEntryRect((Region) ((ArrayList) mRegions.get(i)).get(i2)));
                            }
                            if (mTooltip != null) {
                                toggleTooltip(getEntryRect((Region) ((ArrayList) mRegions.get(i)).get(i2)), data.get(i).getValue(i2));
                            }
                            return true;
                        }
                    }
                }
            }
            if (mChartListener != null) {
                mChartListener.onClick(ChartView.this);
            }
            if (mTooltip != null && mTooltip.on()) {
                dismissTooltip(mTooltip);
            }
            return true;
        }
    }
}
