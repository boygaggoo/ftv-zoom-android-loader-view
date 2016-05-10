/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 France Télévisions
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fr.francetv.zoom.share.loader;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class ZoomLoaderView extends View {

    private static final String LOG_TAG = ZoomLoaderView.class.getSimpleName();

    private static final long ANIMATION_IN_DURATION_MS = 1800L;
    private static final long ANIMATION_PAUSE_MS = 250L;
    private static final long ANIMATION_OUT_DURATION_MS = 1800L;

    private static final int VIEW_WIDTH_DP = 63;
    private static final int VIEW_HEIGHT_DP = 36;
    private static final int LEFT_START_DEGRE = -45;
    private static final int RIGHT_START_DEGRE = 180 + LEFT_START_DEGRE;

    private DelayHandler mDelayHandler;

    private int mViewWidthPx = 0;
    private int mViewHeightPx = 0;

    private int mRadiusPx = 0;

    private Bitmap mMaskBitmap;
    private Paint mMaskPaint;
    private Paint mBackgroundPaint;

    private Paint mAnimPaint;
    private RectF mLeftCircleArc;
    private RectF mRightCircleArc;

    private ValueAnimator mAnimationIn;
    private ValueAnimator mAnimationOut;

    private boolean mIsRunning;

    public ZoomLoaderView(Context context) {
        super(context);
        init();
    }

    public ZoomLoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomLoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void start() {
        Log.v(LOG_TAG, "start: ");
        if (mIsRunning) {
            return;
        }
        stopRunningAnimation();
        mIsRunning = true;
        mAnimationIn.start();
    }

    public void stop() {
        Log.v(LOG_TAG, "stop: ");
        mIsRunning = false;
        stopRunningAnimation();
    }

    public void setThemeColor(final int color) {
        mAnimPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int sc = canvas.saveLayer(0, 0, mViewWidthPx, mViewHeightPx, null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        int mAnimationInValue = (int)mAnimationIn.getAnimatedValue();
        int mAnimationOutValue = mAnimationOut.isRunning() ? (int)mAnimationOut.getAnimatedValue() : 0;

        canvas.drawPaint(mBackgroundPaint);
        canvas.drawArc(mLeftCircleArc, LEFT_START_DEGRE - mAnimationOutValue, -mAnimationInValue + mAnimationOutValue, true, mAnimPaint);
        canvas.drawArc(mRightCircleArc, RIGHT_START_DEGRE - mAnimationOutValue, -mAnimationInValue + mAnimationOutValue, true, mAnimPaint);
        canvas.drawBitmap(mMaskBitmap, 0, 0, mMaskPaint);
        canvas.restoreToCount(sc);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(mViewWidthPx, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mViewHeightPx, MeasureSpec.EXACTLY));
        if(mRadiusPx == 0) {
            mRadiusPx = MeasureSpec.getSize(heightMeasureSpec);
        }
    }

    private void init() {
        mIsRunning = false;

        mViewWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, VIEW_WIDTH_DP, getResources().getDisplayMetrics());
        mViewHeightPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, VIEW_HEIGHT_DP, getResources().getDisplayMetrics());

        mRadiusPx = mViewHeightPx / 2;

        mMaskBitmap = Bitmap.createBitmap(mViewWidthPx, mViewHeightPx, Bitmap.Config.ARGB_8888);
        new Canvas(mMaskBitmap).drawBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.img_loader_mask), 0, 0, null);

        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setFilterBitmap(false);
        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setFilterBitmap(false);
        mBackgroundPaint.setXfermode(null);
        mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));

        // Circles
        mAnimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAnimPaint.setXfermode(null);
        mAnimPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));

        mLeftCircleArc = new RectF(0, 0, 2 * mRadiusPx, 2 * mRadiusPx);
        mRightCircleArc = new RectF(mViewWidthPx - 2 * mRadiusPx, 0, mViewWidthPx, 2 * mRadiusPx);

        //Anims
        mAnimationIn = ValueAnimator.ofInt(0, 361 + LEFT_START_DEGRE);
        mAnimationIn.setInterpolator(new FastOutSlowInInterpolator());
        mAnimationIn.addUpdateListener(mAnimationInUpdateListener);
        mAnimationIn.addListener(mAnimationInListener);
        mAnimationIn.setDuration(ANIMATION_IN_DURATION_MS);

        mAnimationOut = ValueAnimator.ofInt(0, 361 + LEFT_START_DEGRE);
        mAnimationOut.setInterpolator(new FastOutSlowInInterpolator());
        mAnimationOut.addUpdateListener(mAnimationOutUpdateListener);
        mAnimationOut.addListener(mAnimationOutListener);
        mAnimationOut.setDuration(ANIMATION_OUT_DURATION_MS);

        mDelayHandler = new DelayHandler(mAnimationOut);
    }

    private void stopRunningAnimation() {
        mDelayHandler.removeCallbacks(null);
        if (mAnimationIn.isRunning()) {
            mAnimationIn.cancel();
        }
        if (mAnimationOut.isRunning()) {
            mAnimationOut.cancel();
        }
    }

    private final ValueAnimator.AnimatorUpdateListener mAnimationInUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            invalidate();
        }
    };

    private final Animator.AnimatorListener mAnimationInListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animation) {
            if (mIsRunning) {
                mDelayHandler.delay(ANIMATION_PAUSE_MS);
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private final ValueAnimator.AnimatorUpdateListener mAnimationOutUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            invalidate();
        }
    };

    private final Animator.AnimatorListener mAnimationOutListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animation) {
            if (mIsRunning) {
                mAnimationIn.start();
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private static class DelayHandler extends Handler {

        private static final int MESSAGE_DELAY = 0;

        private final ValueAnimator mAnimationOut;

        public DelayHandler(final ValueAnimator animationOut) {
            super(Looper.myLooper());
            mAnimationOut = animationOut;
        }

        protected void delay(long delay) {
            removeMessages(MESSAGE_DELAY);
            sendMessageDelayed(obtainMessage(MESSAGE_DELAY), delay);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_DELAY) {
                mAnimationOut.start();
            }
        }

    }

}
