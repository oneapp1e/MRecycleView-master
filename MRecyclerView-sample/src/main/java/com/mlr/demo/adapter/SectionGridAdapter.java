package com.mlr.demo.adapter;

import android.support.v7.widget.RecyclerView;
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
import com.mlr.model.ViewTypeInfo;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.mrecyclerview.SectionMRecyclerView;
import com.mlr.utils.LogUtils;

import java.util.List;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class SectionGridAdapter extends MRecyclerViewAdapter<ViewTypeInfo> implements
        SectionMRecyclerView.OnPinnedHeaderChangeListener, SectionMRecyclerView.OnPinnedHeaderClickListenerWithEvent {

    // ==========================================================================
    // Constants
    // ==========================================================================
    private int count = 0;
    // ==========================================================================
    // Fields
    // ==========================================================================

    private static final int VIEW_TYPE_LIST = VIEW_TYPE_ITEM;

    private static final int VIEW_TYPE_SECTION = VIEW_TYPE_LIST + 1;
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
        if (viewType == VIEW_TYPE_SECTION) {
            return DataServer.spanCount;
        } else {
            return super.getSpanSize(position, viewType);
        }
    }

    @Override
    public int getSectionViewType() {
        return VIEW_TYPE_SECTION;
    }

    @Override
    protected int getItemType(int position) {
        if (getItem(position) instanceof TitleInfo) {
            return VIEW_TYPE_SECTION;
        } else {
            return VIEW_TYPE_LIST;
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
    protected RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View textView = getActivity().inflate(R.layout.common_list_item, parent, false);
        if (viewType == VIEW_TYPE_LIST) {
            return new AppInfoHolder(textView, getActivity());
        } else {
            textView.setBackgroundColor(getActivity().getColorRes(R.color.colorAccent));
            return new TitleInfoHolder(textView, getActivity());
        }
    }

    public TitleInfoHolder getPinnedHeaderHolder() {
        View textView = getActivity().inflate(R.layout.common_list_item);
        textView.setBackgroundColor(getActivity().getColorRes(R.color.colorAccent));
        TitleInfoHolder commonListHolder2 = new TitleInfoHolder(textView, getActivity());
        textView.setTag(commonListHolder2);
        return commonListHolder2;
    }

    @Override
    protected void bindItemHolder(RecyclerView.ViewHolder holder, final int position, int viewType) {
        if (viewType == VIEW_TYPE_LIST) {
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
    protected int getMoreData(List<ViewTypeInfo> out, int startPosition, int requestSize) {
        if (count >= DataServer.MaxCount) {
            LogUtils.e("mlr 没有更多数据");
        } else {
            LogUtils.e("mlr 请求更多数据");
            out.addAll(DataServer.getSectionMoreData(2));
            count++;
        }
        return 200;
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
