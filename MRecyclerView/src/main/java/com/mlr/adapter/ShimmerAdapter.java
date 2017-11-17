package com.mlr.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mlr.holder.SimpleHolder;
import com.mlr.model.ViewTypeInfo;
import com.mlr.mrecyclerview.R;
import com.mlr.utils.ISpanSizeLookup;

import java.util.ArrayList;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * 微光demo适配器
 * Created by mulinrui on 2017/11/15.
 */
public class ShimmerAdapter extends MRecyclerViewAdapter<ViewTypeInfo, SimpleHolder> {


    private int mItemCount = 20;
    private int spanCount = 1;

    private int mLayoutReference = R.layout.layout_sample_view;
    private int mShimmerAngle;
    private int mShimmerColor;
    private int mShimmerDuration;
    private Drawable mShimmerItemBackground;

    public ShimmerAdapter(Context context, int spanCount) {
        super(context, null);
        List<ViewTypeInfo> list = new ArrayList<>(mItemCount);
        for (int i = 0; i < mItemCount; i++) {
            ViewTypeInfo viewTypeInfo = new ViewTypeInfo();
            viewTypeInfo.setViewType(VIEW_TYPE_ITEM);
            list.add(viewTypeInfo);
        }
        setData(list);

        this.spanCount = spanCount;
    }

    @Override
    public int getSpanCount() {
        return spanCount;
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    public void setShimmerAngle(int shimmerAngle) {
        this.mShimmerAngle = shimmerAngle;
    }

    public void setShimmerColor(int shimmerColor) {
        this.mShimmerColor = shimmerColor;
    }

    public void setShimmerItemBackground(Drawable shimmerItemBackground) {
        this.mShimmerItemBackground = shimmerItemBackground;
    }

    public void setShimmerDuration(int mShimmerDuration) {
        this.mShimmerDuration = mShimmerDuration;
    }

    public void setLayoutReference(int layoutReference) {
        this.mLayoutReference = layoutReference;
    }


    @Override
    protected SimpleHolder createItemHolder(ViewGroup parent, int viewType) {
        ShimmerLayout mShimmerLayout = (ShimmerLayout) getInflater().inflate(R.layout.viewholder_shimmer, parent, false);
        //添加itemview
        getInflater().inflate(mLayoutReference, mShimmerLayout, true);
        mShimmerLayout.setShimmerColor(mShimmerColor);
        mShimmerLayout.setShimmerAngle(mShimmerAngle);
        mShimmerLayout.setBackgroundDrawable(mShimmerItemBackground);
        mShimmerLayout.setShimmerAnimationDuration(mShimmerDuration);

        return new SimpleHolder(mShimmerLayout, getContext());
    }

    @Override
    protected void bindItemHolder(SimpleHolder holder, int position, int viewType) {
        ShimmerLayout mShimmerLayout = (ShimmerLayout) holder.itemView;
        mShimmerLayout.startShimmerAnimation();
    }


}
