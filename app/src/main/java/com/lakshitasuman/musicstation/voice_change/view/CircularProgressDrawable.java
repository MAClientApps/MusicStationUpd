package com.lakshitasuman.musicstation.voice_change.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.lakshitasuman.musicstation.R;


public class CircularProgressDrawable extends Drawable implements Animatable {
    public static final ArgbEvaluator COLOR_EVALUATOR = new ArgbEvaluator();
    public static final Interpolator DEFAULT_ROTATION_INTERPOLATOR = new LinearInterpolator();
    public static final Interpolator DEFAULT_SWEEP_INTERPOLATOR = new DecelerateInterpolator();
    private static final int END_ANIMATOR_DURATION = 200;
    public static final Interpolator END_INTERPOLATOR = new LinearInterpolator();
    private static final int ROTATION_ANIMATOR_DURATION = 2000;
    private static final int SWEEP_ANIMATOR_DURATION = 600;
    public static final String TAG = CircularProgressDrawable.class.getSimpleName();
    private final RectF fBounds;
    private Interpolator mAngleInterpolator;
    private float mBorderWidth;
    public int[] mColors;
    public int mCurrentColor;
    private float mCurrentEndRatio;
    public int mCurrentIndexColor;
    private float mCurrentRotationAngle;
    private float mCurrentRotationAngleOffset;
    private float mCurrentSweepAngle;
    public ValueAnimator mEndAnimator;
    public boolean mFirstSweepAnimation;
    public int mMaxSweepAngle;
    public int mMinSweepAngle;
    public boolean mModeAppearing;
    public OnEndListener mOnEndListener;
    public Paint mPaint;
    private ValueAnimator mRotationAnimator;
    private float mRotationSpeed;
    private boolean mRunning;
    public ValueAnimator mSweepAppearingAnimator;
    public ValueAnimator mSweepDisappearingAnimator;
    private Interpolator mSweepInterpolator;
    private float mSweepSpeed;
    public interface OnEndListener {
        void onEnd(CircularProgressDrawable circularProgressDrawable);
    }

    public enum Style {
        NORMAL,
        ROUNDED
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    private CircularProgressDrawable(int[] iArr, float f, float f2, float f3, int i, int i2, Style style, Interpolator interpolator, Interpolator interpolator2) {
        fBounds = new RectF();
        mCurrentRotationAngleOffset = 0.0f;
        mCurrentRotationAngle = 0.0f;
        mCurrentEndRatio = 1.0f;
        mSweepInterpolator = interpolator2;
        mAngleInterpolator = interpolator;
        mBorderWidth = f;
        mCurrentIndexColor = 0;
        mColors = iArr;
        mCurrentColor = iArr[0];
        mSweepSpeed = f2;
        mRotationSpeed = f3;
        mMinSweepAngle = i;
        mMaxSweepAngle = i2;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(f);
        mPaint.setStrokeCap(style == Style.ROUNDED ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        mPaint.setColor(mColors[0]);
        setupAnimations();
    }

    private void reInitValues() {
        mFirstSweepAnimation = true;
        mPaint.setColor(mCurrentColor);
    }

    public void draw(Canvas canvas) {
        float f;
        float f2;
        if (isRunning()) {
            float f3 = mCurrentRotationAngle - mCurrentRotationAngleOffset;

            if (!mModeAppearing) {
                f3 += 360.0f - mCurrentSweepAngle;
            }
            float f5 = f3 % 360.0f;

            if (mCurrentEndRatio < 1.0f) {
                float f7 = mCurrentEndRatio * mCurrentSweepAngle;
                f2 = (f5 + (mCurrentSweepAngle - f7)) % 360.0f;
                f = f7;
            } else {
                f2 = f5;
                f = mCurrentSweepAngle;
            }
            canvas.drawArc(fBounds, f2, f, false, mPaint);
        }
    }

    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }


    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        fBounds.left = ((float) rect.left) + (mBorderWidth / 2.0f) + 0.5f;
        fBounds.right = (((float) rect.right) - (mBorderWidth / 2.0f)) - 0.5f;
        fBounds.top = ((float) rect.top) + (mBorderWidth / 2.0f) + 0.5f;
        fBounds.bottom = (((float) rect.bottom) - (mBorderWidth / 2.0f)) - 0.5f;
    }


    public void setAppearing() {
        mModeAppearing = true;
        mCurrentRotationAngleOffset += (float) mMinSweepAngle;
    }


    public void setDisappearing() {
        mModeAppearing = false;
        mCurrentRotationAngleOffset += (float) (360 - mMaxSweepAngle);
    }

    private void setupAnimations() {
        mRotationAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 360.0f});
        mRotationAnimator.setInterpolator(mAngleInterpolator);
        mRotationAnimator.setDuration((long) (2000.0f / mRotationSpeed));
        mRotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setCurrentRotationAngle(valueAnimator.getAnimatedFraction() * 360.0f);
            }
        });
        mRotationAnimator.setRepeatCount(-1);
        mRotationAnimator.setRepeatMode(ValueAnimator.RESTART);
        mSweepAppearingAnimator = ValueAnimator.ofFloat(new float[]{(float) mMinSweepAngle, (float) mMaxSweepAngle});
        mSweepAppearingAnimator.setInterpolator(mSweepInterpolator);
        mSweepAppearingAnimator.setDuration((long) (600.0f / mSweepSpeed));
        mSweepAppearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
         @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float f;
                float animatedFraction = valueAnimator.getAnimatedFraction();
                if (mFirstSweepAnimation) {
                    f = animatedFraction * ((float) mMaxSweepAngle);
                } else {
                    f = (animatedFraction * ((float) (mMaxSweepAngle - mMinSweepAngle))) + ((float) mMinSweepAngle);
                }
                setCurrentSweepAngle(f);
            }
        });
        mSweepAppearingAnimator.addListener(new Animator.AnimatorListener() {
            boolean cancelled = false;

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                cancelled = false;
               mModeAppearing = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!cancelled) {
                     mFirstSweepAnimation = false;
                    setDisappearing();
                    mSweepDisappearingAnimator.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                cancelled = true;
            }
        });
        mSweepDisappearingAnimator = ValueAnimator.ofFloat(new float[]{(float) mMaxSweepAngle, (float) mMinSweepAngle});
        mSweepDisappearingAnimator.setInterpolator(mSweepInterpolator);
        mSweepDisappearingAnimator.setDuration((long) (600.0f / mSweepSpeed));
        mSweepDisappearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();

                setCurrentSweepAngle(((float) mMaxSweepAngle) - (animatedFraction * ((float) (mMaxSweepAngle - mMinSweepAngle))));
                float currentPlayTime = ((float) valueAnimator.getCurrentPlayTime()) / ((float) valueAnimator.getDuration());
                if (mColors.length > 1 && currentPlayTime > 0.7f) {
                    mPaint.setColor(((Integer) CircularProgressDrawable.COLOR_EVALUATOR.evaluate((currentPlayTime - 0.7f) / 0.3f, Integer.valueOf(mCurrentColor), Integer.valueOf(mColors[(mCurrentIndexColor + 1) % mColors.length]))).intValue());
                }
            }
        });
        mSweepDisappearingAnimator.addListener(new Animator.AnimatorListener() {
            boolean cancelled;

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                cancelled = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!cancelled) {
                    setAppearing();
                    mCurrentIndexColor = (mCurrentIndexColor + 1) % mColors.length;
                    mCurrentColor = mColors[mCurrentIndexColor];
                    mPaint.setColor(mCurrentColor);
                    mSweepAppearingAnimator.start();
                }
            }

            public void onAnimationCancel(Animator animator) {
                cancelled = true;
            }
        });
        mEndAnimator= ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        mEndAnimator.setInterpolator(END_INTERPOLATOR);
        mEndAnimator.setDuration(200);
        mEndAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
           @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setEndRatio(1.0f - valueAnimator.getAnimatedFraction());
            }
        });
        mEndAnimator.addListener(new Animator.AnimatorListener() {
            private boolean cancelled;

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                cancelled = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setEndRatio(1.0f);
                if (!cancelled) {
                    stop();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                cancelled = true;
            }
        });
    }

    @Override
    public void start() {
        if (!isRunning()) {
            mRunning = true;
            reInitValues();
            mRotationAnimator.start();
            mSweepAppearingAnimator.start();
            invalidateSelf();
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            mRunning = false;
            stopAnimators();
            invalidateSelf();
        }
    }


    private void stopAnimators() {
        mRotationAnimator.cancel();
        mSweepAppearingAnimator.cancel();
        mSweepDisappearingAnimator.cancel();
        mEndAnimator.cancel();
    }


    public void progressiveStop(OnEndListener onEndListener) {
        if (isRunning() && !mEndAnimator.isRunning()) {
            mOnEndListener = onEndListener;
            mEndAnimator.addListener(new Animator.AnimatorListener() {
               @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mEndAnimator.removeListener(this);
                    if (mOnEndListener != null) {
                        mOnEndListener.onEnd(CircularProgressDrawable.this);
                    }
                }
            });
            mEndAnimator.start();
        }
    }


    public void progressiveStop() {
        progressiveStop((OnEndListener) null);
    }

    public boolean isRunning() {
        return mRunning;
    }


    public void setCurrentRotationAngle(float f) {
        mCurrentRotationAngle = f;
        invalidateSelf();
    }

    public void setCurrentSweepAngle(float f) {
        mCurrentSweepAngle = f;
        invalidateSelf();
    }


    public void setEndRatio(float f) {
        mCurrentEndRatio = f;
        invalidateSelf();
    }


    public static class Builder {
        private Interpolator mAngleInterpolator = CircularProgressDrawable.DEFAULT_ROTATION_INTERPOLATOR;
        private int[] mColors;
        private int mMaxSweepAngle;
        private int mMinSweepAngle;
        private float mRotationSpeed;
        private float mStrokeWidth;
        private Style mStyle;
        private Interpolator mSweepInterpolator = CircularProgressDrawable.DEFAULT_SWEEP_INTERPOLATOR;
        private float mSweepSpeed;

        public Builder(Context context) {
            initValues(context);
        }

        private void initValues(Context context) {
            mStrokeWidth = context.getResources().getDimension(R.dimen.cpb_default_stroke_width);
            mSweepSpeed = 1.0f;
            mRotationSpeed = 1.0f;
            mColors = new int[]{context.getResources().getColor(R.color.cpb_default_color)};
            mMinSweepAngle = context.getResources().getInteger(R.integer.cpb_default_min_sweep_angle);
            mMaxSweepAngle = context.getResources().getInteger(R.integer.cpb_default_max_sweep_angle);
            mStyle = Style.ROUNDED;
        }

        public Builder color(int i) {
            mColors = new int[]{i};
            return this;
        }

        public Builder colors(int[] iArr) {
            CircularProgressBarUtils.checkColors(iArr);
            mColors = iArr;
            return this;
        }

        public Builder sweepSpeed(float f) {
            CircularProgressBarUtils.checkSpeed(f);
            mSweepSpeed = f;
            return this;
        }

        public Builder rotationSpeed(float f) {
            CircularProgressBarUtils.checkSpeed(f);
            mRotationSpeed = f;
            return this;
        }

        public Builder minSweepAngle(int i) {
            CircularProgressBarUtils.checkAngle(i);
            mMinSweepAngle = i;
            return this;
        }

        public Builder maxSweepAngle(int i) {
            CircularProgressBarUtils.checkAngle(i);
            mMaxSweepAngle = i;
            return this;
        }

        public Builder strokeWidth(float f) {
            CircularProgressBarUtils.checkPositiveOrZero(f, "StrokeWidth");
            mStrokeWidth = f;
            return this;
        }

        public Builder style(Style style) {
            CircularProgressBarUtils.checkNotNull(style, "Style");
            mStyle = style;
            return this;
        }

        public Builder sweepInterpolator(Interpolator interpolator) {
            CircularProgressBarUtils.checkNotNull(interpolator, "Sweep interpolator");
            mSweepInterpolator = interpolator;
            return this;
        }

        public Builder angleInterpolator(Interpolator interpolator) {
            CircularProgressBarUtils.checkNotNull(interpolator, "Angle interpolator");
            mAngleInterpolator = interpolator;
            return this;
        }

        public CircularProgressDrawable build() {
            return new CircularProgressDrawable(mColors, mStrokeWidth, mSweepSpeed, mRotationSpeed, mMinSweepAngle, mMaxSweepAngle, mStyle, mAngleInterpolator, mSweepInterpolator);
        }
    }
}
