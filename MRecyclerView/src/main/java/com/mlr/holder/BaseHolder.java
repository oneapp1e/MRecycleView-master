package com.mlr.holder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import com.mlr.model.ViewTypeInfo;

public abstract class BaseHolder<Data extends ViewTypeInfo> extends ViewHolder {
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================
    protected Context mContext;

    private Data mData;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public BaseHolder(View itemView, Context context) {
        this(itemView, context, null);
    }

    public BaseHolder(View itemView, Context context, Data data) {
        super(itemView);
        mContext = context;
        mData = data;
    }

    // ==========================================================================
    // Getters
    // ==========================================================================
    public Context getContext() {
        return mContext;
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
        return mContext.getResources();
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
