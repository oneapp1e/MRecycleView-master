package com.mlr.demo.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.demo.R;
import com.mlr.demo.holder.ItemViewHolder;
import com.mlr.demo.holder.PhotoViewHolder;
import com.mlr.holder.BaseHolder;
import com.mlr.model.ViewTypeInfo;
import com.mlr.utils.BaseActivity;

import java.util.List;

/**
 * Created by mulinrui on 12/16 0016.
 */
public class NewsListAdapter extends MRecyclerViewAdapter {

    public final static int VIEW_TYPE_COMMON_ITEM = VIEW_TYPE_ITEM;
    public final static int VIEW_TYPE_PHONE_ITEM = VIEW_TYPE_ITEM + 1;

    public NewsListAdapter(BaseActivity activity, List items) {
        super(activity, items);
    }

    @Override
    public int getPreloadCount() {
        return 0;
    }

    @Override
    protected BaseHolder createItemHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_COMMON_ITEM) {
            View view = getInflater().inflate(R.layout.item_news, parent, false);
            return new ItemViewHolder(view, getContext());
        } else {
            View view = getInflater().inflate(R.layout.item_news_photo, parent, false);
            return new PhotoViewHolder(view, getContext());
        }
    }

    @Override
    protected void bindItemHolder(BaseHolder holder, int position, int viewType) {
        List<ViewTypeInfo> data = getData();
        holder.setData(data.get(position));
    }

}
