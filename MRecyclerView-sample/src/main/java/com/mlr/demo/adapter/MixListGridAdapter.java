package com.mlr.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.demo.holder.CommonListHolder;
import com.mlr.demo.holder.CommonListHolder2;
import com.mlr.mrecycleview.BaseActivity;
import com.mlr.utils.LogUtils;

import java.util.List;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class MixListGridAdapter extends MRecyclerViewAdapter<String> {

    // ==========================================================================
    // Constants
    // ==========================================================================
    private int MaxCount = 3;//假数据 最多加载3次更多数据
    private int count = 0;
    // ==========================================================================
    // Fields
    // ==========================================================================

    private static final int VIEW_TYPE_LIST = VIEW_TYPE_ITEM;

    private static final int VIEW_TYPE_GRID = VIEW_TYPE_LIST + 1;
    // ==========================================================================
    // Constructors
    // ==========================================================================

    public MixListGridAdapter(BaseActivity activity, List<? extends String> items) {
        super(activity, items);
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    @Override
    protected int getSpanSize(int position, int viewType) {
        return viewType == VIEW_TYPE_LIST ? 1 : 2;
    }

    @Override
    protected int getItemType(int position) {
        return position % 3 == 0 ? VIEW_TYPE_GRID : VIEW_TYPE_LIST;
    }

    // ==========================================================================
    // Getters
    // ==========================================================================


    // ==========================================================================
    // Setters
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    @Override
    protected RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(getActivity());
        int padding = getActivity().dip2px(5);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getActivity().dip2px(10));
        textView.setPadding(padding, padding, padding, padding);
        if (viewType == VIEW_TYPE_LIST) {
            return new CommonListHolder(textView, getActivity());
        } else {
            return new CommonListHolder2(textView, getActivity());
        }
    }

    @Override
    protected void bindItemHolder(RecyclerView.ViewHolder holder, final int position, int viewType) {
        if (viewType == VIEW_TYPE_LIST) {
            ((CommonListHolder) holder).setData(getData().get(position));
            ((CommonListHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ((CommonListHolder2) holder).setData(getData().get(position));
            ((CommonListHolder2) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
