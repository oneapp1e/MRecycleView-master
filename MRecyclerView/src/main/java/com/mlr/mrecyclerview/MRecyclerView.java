package com.mlr.mrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.utils.DragAndMoveItemTouchHelperCallback;
import com.mlr.utils.ISpanSizeLookup;
import com.mlr.utils.LogUtils;


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
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MRecyclerView);
            mOrientation = a.getInteger(R.styleable.MRecyclerView_orientation, VERTICAL);
            a.recycle();
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
        super.setAdapter(adapter);
        //如果MRecyclerViewAdapter则设置SpanSizeLookup监听
        if (adapter instanceof MRecyclerViewAdapter) {
            setISpanSizeLookup((MRecyclerViewAdapter) adapter);

            //注册拖拽
            LogUtils.e("mlr setAdapter 注册拖拽");
            ItemTouchHelper.Callback callback = new DragAndMoveItemTouchHelperCallback((MRecyclerViewAdapter) adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(this);
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
        layoutManager = new GridLayoutManager(getContext(), mSpanCount, mOrientation, false);
        setLayoutManager(layoutManager);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return super.dispatchKeyEvent(event);
        } catch (Throwable t) {
            LogUtils.e(t);
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        try {
            return super.dispatchTouchEvent(ev);
        } catch (Throwable t) {
            LogUtils.e(t);
            return false;
        }
    }

    @Override
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        try {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        } catch (Throwable t) {
            LogUtils.e(t);
        }
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
