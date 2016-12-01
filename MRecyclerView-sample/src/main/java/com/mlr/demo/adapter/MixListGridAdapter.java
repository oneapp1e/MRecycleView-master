package com.mlr.demo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.demo.R;
import com.mlr.demo.data.DataServer;
import com.mlr.demo.holder.AppInfoHolder;
import com.mlr.demo.holder.TitleInfoHolder;
import com.mlr.demo.model.AppInfo;
import com.mlr.demo.model.TitleInfo;
import com.mlr.holder.BaseHolder;
import com.mlr.model.ViewTypeInfo;
import com.mlr.mrecyclerview.BaseActivity;

import java.util.List;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class MixListGridAdapter extends MRecyclerViewAdapter<ViewTypeInfo, BaseHolder> {

    // ==========================================================================
    // Constants
    // ==========================================================================
    // ==========================================================================
    // Fields
    // ==========================================================================

    // ==========================================================================
    // Constructors
    // ==========================================================================

    public MixListGridAdapter(BaseActivity activity, List<ViewTypeInfo> items) {
        super(activity, items);
    }

    @Override
    public int getSpanCount() {
        return DataServer.spanCount;
    }


    @Override
    protected int getSpanSize(int position, int viewType) {
        if (viewType == DataServer.VIEW_TYPE_TITLE) {
            return DataServer.spanCount;
        } else {
            return super.getSpanSize(position, viewType);
        }
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
    protected BaseHolder createItemHolder(ViewGroup parent, int viewType) {
        View textView = getActivity().inflate(R.layout.common_list_item, parent, false);
        if (viewType == DataServer.VIEW_TYPE_LIST) {
            return new AppInfoHolder(textView, getActivity());
        } else {
            return new TitleInfoHolder(textView, getActivity());
        }
    }


    @Override
    protected void bindItemHolder(BaseHolder holder, final int position, int viewType) {
        if (viewType == DataServer.VIEW_TYPE_LIST) {
            ((AppInfoHolder) holder).setData((AppInfo) getData().get(position));
            ((AppInfoHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ((TitleInfoHolder) holder).setData((TitleInfo) getData().get(position));
            ((TitleInfoHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

}
