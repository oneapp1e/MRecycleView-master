package com.mlr.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.demo.R;
import com.mlr.demo.data.DataServer;
import com.mlr.demo.holder.AppInfoHolder;
import com.mlr.demo.model.AppInfo;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.utils.LogUtils;

import java.util.List;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class CommonGridAdapter extends MRecyclerViewAdapter<AppInfo> {

    // ==========================================================================
    // Constants
    // ==========================================================================
    private int count = 0;
    // ==========================================================================
    // Fields
    // ==========================================================================


    // ==========================================================================
    // Constructors
    // ==========================================================================

    public CommonGridAdapter(BaseActivity activity, List<? extends AppInfo> items) {
        super(activity, items);
    }

    @Override
    public int getSpanCount() {
        return DataServer.spanCount;
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
        View textView = getActivity().inflate(R.layout.common_list_item, parent, false);
        return new AppInfoHolder(textView, getActivity());
    }

    @Override
    protected void bindItemHolder(RecyclerView.ViewHolder holder, final int position, int viewType) {
        ((AppInfoHolder) holder).setData(getData().get(position));
        ((AppInfoHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getMoreData(List<AppInfo> out, int startPosition, int requestSize) {
        if (count >= DataServer.MaxCount) {
            LogUtils.e("mlr 没有更多数据");
        } else {
            LogUtils.e("mlr 请求更多数据");
            out.addAll(DataServer.getCommonMoreData(requestSize));
            count++;
        }
        return 200;
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

}
