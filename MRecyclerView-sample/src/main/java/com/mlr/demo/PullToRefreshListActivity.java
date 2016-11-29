package com.mlr.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.adapter.CommonListAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.utils.LogUtils;
import com.mlr.utils.PullToRefreshRecyclerViewWrapper;

public class PullToRefreshListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MRecyclerView rvCommonList = new MRecyclerView(this);

        CommonListAdapter commonListAdapter = new CommonListAdapter(this, DataServer.getCommonData(20));
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
        View inflate = inflate(R.layout.common_list_item);
        TextView textView = (TextView) inflate.findViewById(R.id.tweetText);
        textView.setText(headerText);
        return inflate;
    }

}
