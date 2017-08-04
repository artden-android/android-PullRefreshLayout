package com.baoyz.pullrefreshlayout.sample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;

import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.RefreshDrawable;

/**
 * Created by yiminsun on 8/17/16.
 */
public class ArtdenDrawableV2 extends RefreshDrawable {

    private Bitmap[] layer;

    private static final int TRANSITION_STARTING = 0;
    private static final int TRANSITION_RUNNING = 1;
    private static final int TRANSITION_NONE = 2;
    private int mTransitionState = TRANSITION_NONE;

    private long mStartTimeMillis;
    private int mDuration = 250;
    private int mAlpha = 0;
    private int fromIndex = 0;
    private int toIndex = 1;
    int mTop;// 刷新部分距离上面的距离
    int mTopNormal;
    int drawHeightMax;

    private Paint mPaint;

    public ArtdenDrawableV2(Context context, PullRefreshLayout layout) {
        super(context, layout);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        Resources res = context.getResources();

        layer = new Bitmap[]{
                ((BitmapDrawable) res.getDrawable(R.drawable.list_refresh_2)).getBitmap(),
                ((BitmapDrawable) res.getDrawable(R.drawable.list_refresh_3)).getBitmap(),
                ((BitmapDrawable) res.getDrawable(R.drawable.list_refresh_4)).getBitmap(),
                ((BitmapDrawable) res.getDrawable(R.drawable.list_refresh_1)).getBitmap()
        };

        Matrix matrix = new Matrix();
        /*matrix.postScale(0.8f, 0.8f);*/
        layer[0] = Bitmap.createBitmap(layer[0],0,0,layer[0].getWidth(),layer[0].getHeight(), matrix, true);
        layer[1] = Bitmap.createBitmap(layer[1],0,0,layer[1].getWidth(),layer[1].getHeight(), matrix, true);
        layer[2] = Bitmap.createBitmap(layer[2],0,0,layer[2].getWidth(),layer[2].getHeight(), matrix, true);
        layer[3] = Bitmap.createBitmap(layer[3],0,0,layer[3].getWidth(),layer[3].getHeight(), matrix, true);

        //开始画的高度
        mTop =mTopNormal= -layer[0].getHeight()- (getRefreshLayout().getFinalOffset() - layer[0].getHeight()) / 2;
        drawHeightMax = (getRefreshLayout().getFinalOffset() - layer[0].getHeight()) / 2;
    }

    @Override
    public void setPercent(float percent) {

    }

    @Override
    public void setColorSchemeColors(int[] colorSchemeColors) {

    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mTop += offset;
        mTopNormal +=offset;
        mAlpha = 0;
        invalidateSelf();
    }

    @Override
    public void start() {
        startTransition();
    }

    @Override
    public void stop() {
        stopTransition();
    }

    private void startTransition() {
        mAlpha = 0;
        mTransitionState = TRANSITION_STARTING;
        invalidateSelf();
    }

    private void stopTransition() {
        fromIndex = 0;
        toIndex = 1;
        mTransitionState = TRANSITION_NONE;
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return mTransitionState == TRANSITION_RUNNING;
    }

    @Override
    public void draw(Canvas canvas) {
        //canvas.drawColor(getContext().getResources().getColor(R.color.lightestgray));
        boolean done = true;
        switch (mTransitionState) {
            case TRANSITION_STARTING:
                mStartTimeMillis = SystemClock.uptimeMillis();
                done = false;
                mTransitionState = TRANSITION_RUNNING;
                break;
            case TRANSITION_RUNNING:
                if (mStartTimeMillis >= 0) {
                    float normalized = (float) (SystemClock.uptimeMillis() - mStartTimeMillis) / mDuration;
                    done = normalized >= 1.0f;
                    normalized = Math.min(normalized, 1.0f);
                    mAlpha = (int) (255 * normalized);
                }
                break;
        }

        final int alpha = mAlpha;
        if (mTop >= drawHeightMax) {
            mTop = drawHeightMax;
        } else {
            if (mTopNormal >=drawHeightMax) {
                mTop = drawHeightMax;
            }
        }
        if (done) {
            if (alpha == 0) {
                canvas.drawBitmap(layer[fromIndex], canvas.getWidth() / 2 - layer[fromIndex].getWidth() / 2, mTop, mPaint);
            }
            if (alpha == 0xFF) {
                canvas.drawBitmap(layer[toIndex], canvas.getWidth() / 2 - layer[toIndex].getWidth() / 2, mTop, mPaint);
            }

            if (mTransitionState == TRANSITION_RUNNING) {
                mTransitionState = TRANSITION_NONE;
                if (toIndex == 0) {
                    fromIndex = 0;
                    toIndex++;
                    startTransition();
                } else if (toIndex < layer.length - 1) {
                    toIndex++;
                    fromIndex++;
                    startTransition();
                } else {
                    fromIndex++;
                    toIndex = 0;
                    startTransition();
                }
            }
            return;
        }
        canvas.drawBitmap(layer[fromIndex], canvas.getWidth() / 2 - layer[fromIndex].getWidth() / 2, mTop, mPaint);

        if (alpha > 0) {
            mPaint.setAlpha(alpha);
            canvas.drawBitmap(layer[toIndex], canvas.getWidth() / 2 - layer[toIndex].getWidth() / 2, mTop, mPaint);
            mPaint.setAlpha(0xFF);
        }
        if (!done) {
            invalidateSelf();
        }
    }
}