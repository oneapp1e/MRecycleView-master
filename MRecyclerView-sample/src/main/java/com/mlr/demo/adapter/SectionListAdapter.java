package com.mlr.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.demo.R;
import com.mlr.demo.holder.CommonListHolder;
import com.mlr.demo.holder.CommonListHolder2;
import com.mlr.demo.model.AppInfo;
import com.mlr.demo.model.TitleInfo;
import com.mlr.model.ViewTypeInfo;
import com.mlr.mrecycleview.BaseActivity;
import com.mlr.mrecycleview.SectionMRecyclerView;
import com.mlr.utils.LogUtils;

import java.util.List;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class SectionListAdapter extends MRecyclerViewAdapter<ViewTypeInfo> implements
        SectionMRecyclerView.OnPinnedHeaderChangeListener, SectionMRecyclerView.OnPinnedHeaderClickListenerWithEvent {

    // ==========================================================================
    // Constants
    // ==========================================================================
    private int MaxCount = 3;//假数据 最多加载3次更多数据
    private int count = 0;
    // ==========================================================================
    // Fields
    // ==========================================================================

    private static final int VIEW_TYPE_LIST = VIEW_TYPE_ITEM;

    private static final int VIEW_TYPE_SECTION = VIEW_TYPE_LIST + 1;
    // ==========================================================================
    // Constructors
    // ==========================================================================

    public SectionListAdapter(BaseActivity activity, List<ViewTypeInfo> items, SectionMRecyclerView mRecyclerView) {
        super(activity, items);
        //设置section
        mRecyclerView.setSectionViewType(getSectionViewType());
        CommonListHolder2 pinnedHeaderHolder = getPinnedHeaderHolder();
        mRecyclerView.setPinnedHeaderView(pinnedHeaderHolder.itemView);
        mRecyclerView.setOnPinnedHeaderChangeListener(this);
        mRecyclerView.setOnPinnedHeaderClickListenerWithEvent(this);
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
        TextView textView = new TextView(getActivity());
        int padding = getActivity().dip2px(5);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getActivity().dip2px(10));
        textView.setPadding(padding, padding, padding, padding);
        if (viewType == VIEW_TYPE_LIST) {
            return new CommonListHolder(textView, getActivity());
        } else {
            textView.setBackgroundColor(getActivity().getColorRes(R.color.colorAccent));
            return new CommonListHolder2(textView, getActivity());
        }
    }

    public CommonListHolder2 getPinnedHeaderHolder() {
        TextView textView = new TextView(getActivity());
        int padding = getActivity().dip2px(5);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getActivity().dip2px(10));
        textView.setPadding(padding, padding, padding, padding);
        textView.setBackgroundColor(getActivity().getColorRes(R.color.colorAccent));
        CommonListHolder2 commonListHolder2 = new CommonListHolder2(textView, getActivity());
        textView.setTag(commonListHolder2);
        return commonListHolder2;
    }

    @Override
    protected void bindItemHolder(RecyclerView.ViewHolder holder, final int position, int viewType) {
        if (viewType == VIEW_TYPE_LIST) {
            AppInfo appInfo = (AppInfo) getData().get(position);
            ((CommonListHolder) holder).setData(appInfo.getAppName());
            ((CommonListHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            TitleInfo titleInfo = (TitleInfo) getData().get(position);
            ((CommonListHolder2) holder).setData(titleInfo.getTitle());
            ((CommonListHolder2) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getData().get(position) + "  position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected int getMoreData(List<ViewTypeInfo> out, int startPosition, int requestSize) {
        if (count >= MaxCount) {
            LogUtils.e("mlr 没有更多数据");
        } else {
            LogUtils.e("mlr 请求更多数据");
            for (int j = 0; j < 2; j++) {
                TitleInfo titleInfo = new TitleInfo();
                titleInfo.setTitle("more title" + j);
                out.add(titleInfo);
                for (int i = 0; i < 5; i++) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppName("more title" + j + " appName" + i);
                    out.add(appInfo);
                }
            }
            count++;
        }
        return 200;
    }


    @Override
    public void onConfigurePinnedHeader(View header, int prevSectionViewTypePosition, int alpha) {
        if (null != header && header.getTag() instanceof CommonListHolder2) {
            CommonListHolder2 holder = (CommonListHolder2) header.getTag();
            Object obj = getItem(prevSectionViewTypePosition);
            if (!(obj instanceof TitleInfo)) {
                return;
            }
            TitleInfo titleInfo = (TitleInfo) obj;
            holder.setData(titleInfo.getTitle());
        }
    }

    @Override
    public void onPinnedHeaderClick(View header, int prevSectionViewTypePosition, float eventX, float eventY) {
        if (null != header && header.getTag() instanceof CommonListHolder2) {

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
