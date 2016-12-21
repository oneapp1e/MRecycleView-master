package com.mlr.holder;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import com.mlr.model.ViewTypeInfo;
import com.mlr.utils.BaseActivity;

public abstract class BaseHolder<Data extends ViewTypeInfo> extends ViewHolder {
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================
    protected BaseActivity mActivity;

    private Data mData;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public BaseHolder(View itemView, BaseActivity activity) {
        this(itemView, activity, null);
    }

    public BaseHolder(View itemView, BaseActivity activity, Data data) {
        super(itemView);
        mActivity = activity;
        mData = data;
    }

    // ==========================================================================
    // Getters
    // ==========================================================================
    public BaseActivity getActivity() {
        return mActivity;
    }

    // ==========================================================================
    // Setters
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    public Resources getThemeResources() {
        return mActivity.getResources();
    }


    /**
     * 根据全局控制器，判断是否需要加载图片
     *
     * @return
     */
    protected boolean isLoadImage() {
        // 大图模式和智能模式下，加载图片
        return true;
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
