package com.mlr.demo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.demo.R;
import com.mlr.demo.holder.AppInfoHolder;
import com.mlr.demo.model.AppInfo;
import com.mlr.utils.BaseActivity;

import java.util.List;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class CommonListAdapter extends MRecyclerViewAdapter<AppInfo,AppInfoHolder> {

    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================


    // ==========================================================================
    // Constructors
    // ==========================================================================

    public CommonListAdapter(BaseActivity activity, List<AppInfo> items) {
        super(activity, items);
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
    protected AppInfoHolder createItemHolder(ViewGroup parent, int viewType) {
        View textView = getInflater().inflate(R.layout.common_list_item, parent, false);
        return new AppInfoHolder(textView, getContext());
    }

    @Override
    protected void bindItemHolder(AppInfoHolder holder, final int position, int viewType) {
        holder.setData(getData().get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

}
