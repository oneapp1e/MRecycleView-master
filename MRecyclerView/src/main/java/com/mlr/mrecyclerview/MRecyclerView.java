package com.mlr.mrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.adapter.ShimmerAdapter;
import com.mlr.utils.DragAndMoveItemTouchHelperCallback;
import com.mlr.utils.ISpanSizeLookup;
import com.mlr.utils.LogUtil;


/**
 * MRecyclerView 基类
 * Created by mulinrui on 2015/12/2.
 */
public class MRecyclerView extends RecyclerView {

    // ==========================================================================
    // Constants
    // ==========================================================================
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private final int default_spanCount = 1;

    // ==========================================================================
    // Fields
    // ==========================================================================
    /**
     * 方向
     */
    private int mOrientation;
    /**
     * 布局管理
     */
    protected GridLayoutManager layoutManager;
    /**
     * 列数
     */
    private int mSpanCount;

    private Adapter mActualAdapter;
    private ShimmerAdapter mShimmerAdapter;
    private int mLayoutReference = R.layout.layout_sample_view;
    private boolean mCanScroll = true;

    // ==========================================================================
    // Constructors
    // ==========================================================================

    public MRecyclerView(Context context) {
        this(context, null);
    }

    public MRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置默认值
        mOrientation = VERTICAL;

        mShimmerAdapter = new ShimmerAdapter(context, mSpanCount);

        int mShimmerAngle;
        int mShimmerColor;
        int mShimmerDuration;
        Drawable mShimmerItemBackground;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MRecyclerView);

            try {
                mOrientation = a.getInteger(R.styleable.MRecyclerView_orientation, VERTICAL);

                if (a.hasValue(R.styleable.MRecyclerView_shimmer_demo_layout)) {
                    setDemoLayoutReference(a.getResourceId(R.styleable.MRecyclerView_shimmer_demo_layout, R.layout.layout_sample_view));
                }

                mShimmerAngle = a.getInteger(R.styleable.MRecyclerView_shimmer_demo_angle, 0);
                mShimmerColor = a.getColor(R.styleable.MRecyclerView_shimmer_demo_shimmer_color, getColor(R.color.default_shimmer_color));
                mShimmerItemBackground = a.getDrawable(R.styleable.MRecyclerView_shimmer_demo_view_holder_item_background);
                mShimmerDuration = a.getInteger(R.styleable.MRecyclerView_shimmer_demo_duration, 1500);
            } finally {
                a.recycle();
            }

            mShimmerAdapter.setShimmerAngle(mShimmerAngle);
            mShimmerAdapter.setShimmerColor(mShimmerColor);
            mShimmerAdapter.setShimmerItemBackground(mShimmerItemBackground);
            mShimmerAdapter.setShimmerDuration(mShimmerDuration);
        }


        initRecyclerView();

    }

    // ==========================================================================
    // Getters
    // ==========================================================================
    public int getOrientation() {
        return mOrientation;
    }

    public GridLayoutManager getLayoutManager() {
        return layoutManager;
    }

    @Override
    public MRecyclerViewAdapter getAdapter() {
        return (MRecyclerViewAdapter) super.getAdapter();
    }

// ==========================================================================
    // Setters
    // ==========================================================================

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("invalid orientation:" + orientation);
        }
        if (mOrientation == orientation) {
            return;
        }

        mOrientation = orientation;
        layoutManager.setOrientation(mOrientation);
    }

    private void setSpanCount(int spanCount) {
        if (spanCount > default_spanCount) {
            mSpanCount = spanCount;
            layoutManager.setSpanCount(spanCount);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {

        if (adapter == null) {
            mActualAdapter = null;
        } else if (adapter != mShimmerAdapter) {
            mActualAdapter = adapter;
        }

        super.setAdapter(adapter);
        //如果MRecyclerViewAdapter则设置SpanSizeLookup监听
        if (adapter instanceof MRecyclerViewAdapter) {
            setISpanSizeLookup((MRecyclerViewAdapter) adapter);

            if (((MRecyclerViewAdapter) adapter).isDefaultDrag()) {
                //注册拖拽
                LogUtil.e("mlr setAdapter 注册拖拽");
                ItemTouchHelper.Callback callback = new DragAndMoveItemTouchHelperCallback((MRecyclerViewAdapter) adapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(this);
            }

        }
        if (adapter instanceof ISpanSizeLookup) {
            setSpanCount(((ISpanSizeLookup) adapter).getSpanCount());
        }
    }

    public void setISpanSizeLookup(final ISpanSizeLookup l) {
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (l != null) {
                    int spanSize = l.getSpanSize2(position);
                    if (spanSize > mSpanCount) {
                        spanSize = mSpanCount;
                    }
                    return spanSize;
                }
                return 1;
            }
        });
    }

    // ==========================================================================
    // Methods
    // ==========================================================================
    private void initRecyclerView() {
        // 设置RecyclerView的布局管理
        mSpanCount = default_spanCount;
        layoutManager = new GridLayoutManager(getContext(), mSpanCount, mOrientation, false) {
            @Override
            public boolean canScrollHorizontally() {
                return mCanScroll;
            }

            @Override
            public boolean canScrollVertically() {
                return mCanScroll;
            }
        };
        setLayoutManager(layoutManager);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return super.dispatchKeyEvent(event);
        } catch (Throwable t) {
            LogUtil.e(t);
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        try {
            return super.dispatchTouchEvent(ev);
        } catch (Throwable t) {
            LogUtil.e(t);
            return false;
        }
    }

    @Override
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        try {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        } catch (Throwable t) {
            LogUtil.e(t);
        }
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

    /**
     * Sets the demo layout reference
     *
     * @param mLayoutReference layout resource id of the layout which should be shown as demo.
     */
    public void setDemoLayoutReference(int mLayoutReference) {
        this.mLayoutReference = mLayoutReference;
        mShimmerAdapter.setLayoutReference(getLayoutReference());
    }

    public int getLayoutReference() {
        return mLayoutReference;
    }

    /**
     * Sets the shimmer adapter and shows the loading screen.
     */
    public void showShimmerAdapter() {
        mCanScroll = false;
        setAdapter(mShimmerAdapter);

    }

    /**
     * Hides the shimmer adapter
     */
    public void hideShimmerAdapter() {
        mCanScroll = true;
        setAdapter(mActualAdapter);
    }

    private int getColor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getContext().getColor(id);
        } else {
            return getResources().getColor(id);
        }
    }
}
