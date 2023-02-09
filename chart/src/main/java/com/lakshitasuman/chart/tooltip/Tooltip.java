package com.lakshitasuman.chart.tooltip;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lakshitasuman.chart.listener.OnTooltipEventListener;
import java.text.DecimalFormat;

public class Tooltip extends RelativeLayout {
    private int mBottomMargin;
    private ObjectAnimator mEnterAnimator;
    private ObjectAnimator mExitAnimator;
    private int mHeight;
    private Alignment mHorizontalAlignment = Alignment.CENTER;
    private int mLeftMargin;
    private boolean mOn;
    private int mRightMargin;
    private OnTooltipEventListener mTooltipEventListener;
    private TextView mTooltipValue;
    private int mTopMargin;
    private DecimalFormat mValueFormat;
    private Alignment mVerticalAlignment = Alignment.CENTER;
    private int mWidth;

    public enum Alignment {
        BOTTOM_TOP,
        TOP_BOTTOM,
        TOP_TOP,
        CENTER,
        BOTTOM_BOTTOM,
        LEFT_LEFT,
        RIGHT_LEFT,
        RIGHT_RIGHT,
        LEFT_RIGHT
    }

    public Tooltip(Context context) {
        super(context);
        init();
    }

    public Tooltip(Context context, int i) {
        super(context);
        init();
        View inflate = inflate(getContext(), i, (ViewGroup) null);
        inflate.setLayoutParams(new LayoutParams(-1, -1));
        addView(inflate);
    }

    public Tooltip(Context context, int i, int i2) {
        super(context);
        init();
        View inflate = inflate(getContext(), i, (ViewGroup) null);
        inflate.setLayoutParams(new LayoutParams(-1, -1));
        addView(inflate);
        mTooltipValue = (TextView) findViewById(i2);
    }

    private void init() {
        mWidth = -1;
        mHeight = -1;
        mLeftMargin = 0;
        mTopMargin = 0;
        mRightMargin = 0;
        mBottomMargin = 0;
        mOn = false;
        mValueFormat = new DecimalFormat();
    }

    public void prepare(Rect rect, float f) {
        int width = mWidth == -1 ? rect.width() : mWidth;
        int height = mHeight == -1 ? rect.height() : mHeight;
        LayoutParams layoutParams = new LayoutParams(width, height);
        if (mHorizontalAlignment == Alignment.RIGHT_LEFT) {
            layoutParams.leftMargin = (rect.left - width) - mRightMargin;
        }
        if (mHorizontalAlignment == Alignment.LEFT_LEFT) {
            layoutParams.leftMargin = rect.left + mLeftMargin;
        }
        if (mHorizontalAlignment == Alignment.CENTER) {
            layoutParams.leftMargin = rect.centerX() - (width / 2);
        }
        if (mHorizontalAlignment == Alignment.RIGHT_RIGHT) {
            layoutParams.leftMargin = (rect.right - width) - mRightMargin;
        }
        if (mHorizontalAlignment == Alignment.LEFT_RIGHT) {
            layoutParams.leftMargin = rect.right + mLeftMargin;
        }
        if (mVerticalAlignment == Alignment.BOTTOM_TOP) {
            layoutParams.topMargin = (rect.top - height) - mBottomMargin;
        } else if (mVerticalAlignment == Alignment.TOP_TOP) {
            layoutParams.topMargin = rect.top + mTopMargin;
        } else if (mVerticalAlignment == Alignment.CENTER) {
            layoutParams.topMargin = rect.centerY() - (height / 2);
        } else if (mVerticalAlignment == Alignment.BOTTOM_BOTTOM) {
            layoutParams.topMargin = (rect.bottom - height) - mBottomMargin;
        } else if (mVerticalAlignment == Alignment.TOP_BOTTOM) {
            layoutParams.topMargin = rect.bottom + mTopMargin;
        }
        setLayoutParams(layoutParams);
        if (mTooltipValue != null) {
            mTooltipValue.setText(mValueFormat.format((double) f));
        }
    }

    public void correctPosition(int i, int i2, int i3, int i4) {
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (layoutParams.leftMargin < i) {
            layoutParams.leftMargin = i;
        }
        if (layoutParams.topMargin < i2) {
            layoutParams.topMargin = i2;
        }
        if (layoutParams.leftMargin + layoutParams.width > i3) {
            layoutParams.leftMargin = i3 - layoutParams.width;
        }
        if (layoutParams.topMargin + layoutParams.height > i4) {
            layoutParams.topMargin = i4 - layoutParams.height;
        }
        setLayoutParams(layoutParams);
    }

    @TargetApi(11)
    public void animateEnter() {
        mEnterAnimator.start();
    }

    @TargetApi(11)
    public void animateExit(final Runnable runnable) {
        mExitAnimator.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                runnable.run();
            }
        });
        mExitAnimator.start();
    }

    public boolean hasEnterAnimation() {
        return mEnterAnimator != null;
    }

    public boolean hasExitAnimation() {
        return mExitAnimator != null;
    }

    public boolean on() {
        return mOn;
    }

    public Tooltip setHorizontalAlignment(Alignment alignment) {
        mHorizontalAlignment = alignment;
        return this;
    }

    public Tooltip setVerticalAlignment(Alignment alignment) {
        mVerticalAlignment = alignment;
        return this;
    }

    public Tooltip setDimensions(int i, int i2) {
        mWidth = i;
        mHeight = i2;
        return this;
    }

    public Tooltip setMargins(int i, int i2, int i3, int i4) {
        mLeftMargin = i;
        mTopMargin = i2;
        mRightMargin = i3;
        mBottomMargin = i4;
        return this;
    }

    public void setOn(boolean on) {
        mOn = on;
    }

    public Tooltip setValueFormat(DecimalFormat decimalFormat) {
        mValueFormat = decimalFormat;
        return this;
    }

    @TargetApi(11)
    public ObjectAnimator setEnterAnimation(PropertyValuesHolder... propertyValuesHolderArr) {
        for (PropertyValuesHolder propertyValuesHolder : propertyValuesHolderArr) {
            if (propertyValuesHolder.getPropertyName().equals("alpha")) {
                setAlpha(0.0f);
            }
            if (propertyValuesHolder.getPropertyName().equals("rotation")) {
                setRotation(0.0f);
            }
            if (propertyValuesHolder.getPropertyName().equals("rotationX")) {
                setRotationX(0.0f);
            }
            if (propertyValuesHolder.getPropertyName().equals("rotationY")) {
                setRotationY(0.0f);
            }
            if (propertyValuesHolder.getPropertyName().equals("translationX")) {
                setTranslationX(0.0f);
            }
            if (propertyValuesHolder.getPropertyName().equals("translationY")) {
                setTranslationY(0.0f);
            }
            if (propertyValuesHolder.getPropertyName().equals("scaleX")) {
                setScaleX(0.0f);
            }
            if (propertyValuesHolder.getPropertyName().equals("scaleY")) {
                setScaleY(0.0f);
            }
        }
        mEnterAnimator = ObjectAnimator.ofPropertyValuesHolder(this, propertyValuesHolderArr);

        return mEnterAnimator;
    }

    @TargetApi(11)
    public ObjectAnimator setExitAnimation(PropertyValuesHolder... propertyValuesHolderArr) {
        mExitAnimator = ObjectAnimator.ofPropertyValuesHolder(this, propertyValuesHolderArr);
        return mExitAnimator;
    }
}
