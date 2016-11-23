package com.mlr.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.demo.holder.CommonListHolder;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.utils.LogUtils;

import java.util.List;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class DragMoveGridAdapter extends MRecyclerViewAdapter<String> {

    // ==========================================================================
    // Constants
    // ==========================================================================
    private int MaxCount = 3;//假数据 最多加载3次更多数据
    private int count = 0;
    // ==========================================================================
    // Fields
    // ==========================================================================


    // ==========================================================================
    // Constructors
    // ==========================================================================

    public DragMoveGridAdapter(BaseActivity activity, List<? extends String> items) {
        super(activity, items);
    }


    // ==========================================================================
    // Getters
    // ==========================================================================


    @Override
    public int getSpanCount() {
        return 2;
    }

    @Override
    public boolean isLongPressDragEnabled(int position) {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled(int position) {
        return true;
    }

    // ==========================================================================
    // Setters
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Override
    protected RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(getActivity());
        int padding = getActivity().dip2px(10);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getActivity().dip2px(8));
        textView.setPadding(padding, padding, padding, padding);
        return new CommonListHolder(textView, getActivity());
    }

    @Override
    protected void bindItemHolder(final RecyclerView.ViewHolder holder, final int position, int viewType) {
        ((CommonListHolder) holder).setData(getData().get(position) + "  position:" + position);
        ((CommonListHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getMoreData(List<String> out, int startPosition, int requestSize) {
        if (count >= MaxCount) {
            LogUtils.e("mlr 没有更多数据");
        } else {
            LogUtils.e("mlr 请求更多数据");
            for (int i = 0; i < requestSize; i++) {
                out.add("more data " + i);
            }
            count++;
        }
        return 200;
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

}
