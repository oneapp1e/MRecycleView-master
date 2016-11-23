package com.mlr.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.nineoldandroids.view.ViewHelper;

/**
 * recyclerView支持的item拖拽和滑动删除(针对普通列表和网格  不支持section)
 * Created by mulinrui on 2015/11/25.
 */
public class DragAndMoveItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private MRecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;
    /**
     * 拖动的时候放大的倍数
     */
    private static final float dragScale = 1.1F;

    public DragAndMoveItemTouchHelperCallback(MRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        this.recyclerView = recyclerView;
        boolean isSwipe = true;
        boolean isDrag = true;

        int dragFlags = 0;
        int swipeFlags = 0;

        if (mAdapter != null) {
            isSwipe = mAdapter.isItemViewSwipeEnabled2(viewHolder.getAdapterPosition());
            isDrag = mAdapter.isLongPressDragEnabled2(viewHolder.getAdapterPosition());
        }

        //如果是网格布局
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            if (isSwipe) {
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            }
            if (isDrag) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
        } else {
            //列表布局
            if (isSwipe) {
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            }
            if (isDrag) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source,
                          RecyclerView.ViewHolder target) {
        return source.getItemViewType() == target.getItemViewType();
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder source, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                && !recyclerView.isComputingLayout() && mAdapter != null) {
            mAdapter.onItemMoved(fromPos, toPos);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                && !recyclerView.isComputingLayout() && mAdapter != null) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //当选中Item时候会调用该方法，重写此方法可以实现选中时候的一些动画逻辑
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            ViewHelper.setScaleX(viewHolder.itemView, dragScale);
            ViewHelper.setScaleY(viewHolder.itemView, dragScale);
        } else {
            super.onSelectedChanged(viewHolder, actionState);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //当动画已经结束的时候调用该方法，重写此方法可以实现恢复Item的初始状态
        ViewHelper.setScaleX(viewHolder.itemView, 1);
        ViewHelper.setScaleY(viewHolder.itemView, 1);
        super.clearView(recyclerView, viewHolder);
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                && !recyclerView.isComputingLayout() && mAdapter != null) {
            mAdapter.clearView();
        }
    }

}
