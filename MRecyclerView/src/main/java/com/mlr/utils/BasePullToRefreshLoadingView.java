package com.mlr.utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/** 用于展示下拉刷新时的头View */
public abstract class BasePullToRefreshLoadingView extends FrameLayout {

    public BasePullToRefreshLoadingView(Context context) {
        super(context);
    }

    /** 设置高度，在wrapper的size改变时调用 */
    public final void setHeight(int height) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.height = height;
        requestLayout();
    }

    /** 设置宽度，在wrapper的size改变时调用 */
    public final void setWidth(int width) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.width = width;
        requestLayout();
    }

    public  abstract void pullToRefresh();

    public abstract void releaseToRefresh();

    public abstract void reset();// 头View和尾view都重置

    public abstract void refreshing();

    public abstract void hideAllViews();

    public abstract int getContentSize();

    public abstract void setLastUpdatedText(String label);

    public abstract void setPullText(String text);

    public abstract void setReleaseText(String text);

    public abstract void setRefreshingText(String text);

    public abstract TextView getSubHeaderText();

}