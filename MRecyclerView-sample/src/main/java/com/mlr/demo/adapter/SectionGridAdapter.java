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
import com.mlr.mrecyclerview.SectionMRecyclerView;
import com.mlr.utils.BaseActivity;

import java.util.List;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class SectionGridAdapter extends MRecyclerViewAdapter<ViewTypeInfo, BaseHolder> implements
        SectionMRecyclerView.OnPinnedHeaderChangeListener, SectionMRecyclerView.OnPinnedHeaderClickListenerWithEvent {

    // ==========================================================================
    // Constants
    // ==========================================================================
    // ==========================================================================
    // Fields
    // ==========================================================================

    // ==========================================================================
    // Constructors
    // ==========================================================================

    public SectionGridAdapter(BaseActivity activity, List<ViewTypeInfo> items, SectionMRecyclerView mRecyclerView) {
        super(activity, items);
        //设置section
        mRecyclerView.setSectionViewType(getSectionViewType());
        TitleInfoHolder pinnedHeaderHolder = getPinnedHeaderHolder();
        mRecyclerView.setPinnedHeaderView(pinnedHeaderHolder.itemView);
        mRecyclerView.setOnPinnedHeaderChangeListener(this);
        mRecyclerView.setOnPinnedHeaderClickListenerWithEvent(this);
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

    @Override
    public int getSectionViewType() {
        return DataServer.VIEW_TYPE_TITLE;
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
            textView.setBackgroundColor(getActivity().getResColor(R.color.colorAccent));
            return new TitleInfoHolder(textView, getActivity());
        }
    }

    public TitleInfoHolder getPinnedHeaderHolder() {
        View textView = getActivity().inflate(R.layout.common_list_item);
        textView.setBackgroundColor(getActivity().getResColor(R.color.colorAccent));
        TitleInfoHolder commonListHolder2 = new TitleInfoHolder(textView, getActivity());
        textView.setTag(commonListHolder2);
        return commonListHolder2;
    }

    @Override
    protected void bindItemHolder(BaseHolder holder, final int position, int viewType) {
        if (viewType == DataServer.VIEW_TYPE_LIST) {
            AppInfo appInfo = (AppInfo) getData().get(position);
            ((AppInfoHolder) holder).setData(appInfo);
            ((AppInfoHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            TitleInfo titleInfo = (TitleInfo) getData().get(position);
            ((TitleInfoHolder) holder).setData(titleInfo);
            ((TitleInfoHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onConfigurePinnedHeader(View header, int prevSectionViewTypePosition, int alpha) {
        if (null != header && header.getTag() instanceof TitleInfoHolder) {
            TitleInfoHolder holder = (TitleInfoHolder) header.getTag();
            Object obj = getItem(prevSectionViewTypePosition);
            if (!(obj instanceof TitleInfo)) {
                return;
            }
            TitleInfo titleInfo = (TitleInfo) obj;
            holder.setData(titleInfo);
        }
    }

    @Override
    public void onPinnedHeaderClick(View header, int prevSectionViewTypePosition, float eventX, float eventY) {
        if (null != header && header.getTag() instanceof TitleInfoHolder) {

            Object obj = getItem(prevSectionViewTypePosition);
            if (!(obj instanceof TitleInfo)) {
                return;
            }

            Toast.makeText(getActivity(), "onPinnedHeaderClick  title:" + ((TitleInfo) obj).getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

// ==========================================================================
// Inner/Nested Classes
// ==========================================================================

}
