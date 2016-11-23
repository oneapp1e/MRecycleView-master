package com.mlr.demo;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.adapter.CommonListAdapter;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.utils.LogUtils;
import com.mlr.utils.PullToRefreshRecyclerViewWrapper;

import java.util.ArrayList;
import java.util.List;

public class PullToRefreshListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MRecyclerView rvCommonList = new MRecyclerView(this);

        List<String> list = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            list.add("common" + i);
        }

        CommonListAdapter commonListAdapter = new CommonListAdapter(this, list);
        //启动到底了试图
        commonListAdapter.setToEndEnabled(true, rvCommonList);
        //添加headerView
        View headerView1 = createHeadView("headerView1");
        commonListAdapter.addHeaderView(headerView1);
        View headerView2 = createHeadView("headerView2");
        commonListAdapter.addHeaderView(headerView2);
        View headerView0 = createHeadView("headerView0");
        commonListAdapter.addFirstHeaderView(headerView0);
        View headerView3 = createHeadView("headerView3");
        commonListAdapter.addHeaderView(headerView3, 4);
        View headerView4 = createHeadView("headerView4");
        commonListAdapter.addHeaderView(headerView4, 4);
        commonListAdapter.removeHeaderView(headerView2);
        commonListAdapter.removeHeaderView(headerView1);

        rvCommonList.setAdapter(commonListAdapter);

        PullToRefreshRecyclerViewWrapper pullToRefresh = new PullToRefreshRecyclerViewWrapper(this, rvCommonList);
        pullToRefresh.setPullToRefreshMode(PullToRefreshRecyclerViewWrapper.Mode.BOTH);
        pullToRefresh.setOnRefreshListener(new PullToRefreshRecyclerViewWrapper.OnRefreshListener() {
            @Override
            public void onRefresh(final PullToRefreshRecyclerViewWrapper refreshView) {
                LogUtils.e("testbbs setOnRefreshListener  onRefresh");
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.onRefreshComplete(true);
                    }
                },200);
//                refreshView.onRefreshComplete(true);
            }
        });
        setContentView(pullToRefresh);
    }

    private View createHeadView(String headerText) {
        TextView textView = new TextView(this);
        int padding = dip2px(5);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dip2px(10));
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(headerText);
        return textView;
    }

}
