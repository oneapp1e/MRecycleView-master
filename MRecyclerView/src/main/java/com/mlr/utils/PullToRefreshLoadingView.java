package com.mlr.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.mlr.mrecyclerview.R;


/** 用于展示下拉刷新时的头View */
public class PullToRefreshLoadingView extends BasePullToRefreshLoadingView {
    // 翻转动画的时间
    private static final int FLIP_ANIMATION_DURATION = 150;
    // 动画插入器
    private Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

    private FrameLayout mInnerLayout;
    private ImageView mHeaderImage;
    private ImageView mHeaderProgress;
    private TextView mHeaderText;
    private TextView mSubHeaderText;

    private String mPullText;
    private String mRefreshingText;
    private String mReleaseText;
    private Animation mRotateAnimation, mResetRotateAnimation;
    private Animation mProgressAnimation;

    public PullToRefreshLoadingView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
        mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
        mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
        mHeaderProgress = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_progress);
        mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
        mHeaderImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_image);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();
        lp.gravity = Gravity.BOTTOM;
        mHeaderProgress.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.loading));
        mProgressAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.spinner_ani);

        // 设置下拉的图片，就是箭头
        Drawable imageDrawable = context.getResources().getDrawable(R.mipmap.ic_pulldown);
        setLoadingDrawable(imageDrawable);
        reset();

        final int rotateAngle = -180;
        // 初始化动画
        mRotateAnimation = new RotateAnimation(0, rotateAngle, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
        mRotateAnimation.setFillAfter(true);

        mResetRotateAnimation = new RotateAnimation(rotateAngle, 0, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mResetRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mResetRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
        mResetRotateAnimation.setFillAfter(true);
    }

    public int getContentSize() {
        return mInnerLayout.getHeight();
    }

    /** 回复下拉刷新的状态 */
    public void reset() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullText);
            mHeaderText.setVisibility(View.VISIBLE);
        }
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderImage.clearAnimation();
        mHeaderProgress.clearAnimation();
        mHeaderProgress.setVisibility(View.GONE);
        mHeaderImage.setVisibility(View.VISIBLE);

    }

    /** 隐藏所有的View */
    public void hideAllViews() {
        if (View.VISIBLE == mHeaderText.getVisibility()) {
            mHeaderText.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mHeaderProgress.getVisibility()) {
            mHeaderProgress.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mHeaderImage.getVisibility()) {
            mHeaderImage.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mSubHeaderText.getVisibility()) {
            mSubHeaderText.setVisibility(View.INVISIBLE);
        }
    }

    /** 显示所有的INVISIBLE状态的View */
    public final void showInvisibleViews() {
        if (View.INVISIBLE == mHeaderText.getVisibility()) {
            mHeaderText.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mHeaderProgress.getVisibility()) {
            mHeaderProgress.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mHeaderImage.getVisibility()) {
            mHeaderImage.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mSubHeaderText.getVisibility()) {
            mSubHeaderText.setVisibility(View.VISIBLE);
        }
    }

    /** 下拉刷新 */
    public void pullToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullText);
            mHeaderText.setVisibility(View.VISIBLE);
        }
        if (mRotateAnimation == mHeaderImage.getAnimation()) {
            mHeaderImage.startAnimation(mResetRotateAnimation);
        }

        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(mSubHeaderText.getText())) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setVisibility(View.VISIBLE);
            }
        }
        mHeaderProgress.clearAnimation();
        mHeaderProgress.setVisibility(View.GONE);
    }

    /** 释放刷新 */
    public void releaseToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseText);
        }
        mHeaderImage.startAnimation(mRotateAnimation);
        mHeaderProgress.clearAnimation();
        mHeaderProgress.setVisibility(View.GONE);
    }

    /** 刷新中 */
    public void refreshing() {
        refreshing(true);
    }

    /** 刷新中 */
    public void refreshing(boolean isAnim) {
        if (null != mHeaderText) {
            mHeaderText.setText(mRefreshingText);
            mHeaderText.setVisibility(View.VISIBLE);
        }
        mHeaderImage.clearAnimation();
        mHeaderImage.setVisibility(View.INVISIBLE);
        mHeaderProgress.clearAnimation();
        if (isAnim) {
            mHeaderProgress.startAnimation(mProgressAnimation);
        } else {
            mHeaderProgress.clearAnimation();
        }

        mHeaderProgress.setVisibility(View.VISIBLE);
        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(mSubHeaderText.getText())) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setVisibility(View.VISIBLE);
            }
        }
    }

    /** 设置刷新的图片 */
    public void setLoadingDrawable(Drawable imageDrawable) {
        mHeaderImage.setImageDrawable(imageDrawable);
        // 如果有图片，需要设置ImageView的宽高
        if (null != imageDrawable) {
            final int dHeight = imageDrawable.getIntrinsicHeight();
            final int dWidth = imageDrawable.getIntrinsicWidth();
            // 宽和高都要设置成较大的一边，因为图片需要翻转
            ViewGroup.LayoutParams lp = mHeaderImage.getLayoutParams();
            lp.width = lp.height = Math.max(dHeight, dWidth);
            mHeaderImage.requestLayout();
            // 设置imageView的矩阵
            mHeaderImage.setScaleType(ScaleType.MATRIX);
            Matrix matrix = new Matrix();
            matrix.postTranslate((lp.width - dWidth) / 2f, (lp.height - dHeight) / 2f);// 移动图片的位置
            matrix.postRotate(0f, lp.width / 2f, lp.height / 2f);// 设置翻转
            mHeaderImage.setImageMatrix(matrix);
        }
    }

    /** 设置最后更新时间 */
    public void setLastUpdatedText(String text) {
        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(text)) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setText(text);
                if (View.GONE == mSubHeaderText.getVisibility()) {
                    mSubHeaderText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /** 设置下拉中的文字 */
    public void setPullText(String text) {
        mPullText = text;
    }

    /** 设置刷新中的文字 */
    public void setRefreshingText(String text) {
        mRefreshingText = text;
    }

    /** 设置释放刷新的文字 */
    public void setReleaseText(String text) {
        mReleaseText = text;
    }

    /** 设置文字类型 */
    public void setTextTypeface(Typeface tf) {
        mHeaderText.setTypeface(tf);
    }

    public TextView getSubHeaderText() {
        return mSubHeaderText;
    }
}