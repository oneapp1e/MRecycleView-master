package com.mlr.demo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.adapter.CommonListAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.demo.model.AppInfo;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.utils.BaseActivity;
import com.mlr.utils.LoadMoreListener;
import com.mlr.utils.LogUtils;

import java.util.List;

public class SwipeRefreshListActivity extends BaseActivity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe_refresh_list);

        MRecyclerView rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);

        final CommonListAdapter commonListAdapter = new CommonListAdapter(this, DataServer.getCommonData(20));
        //启动到底了试图
        commonListAdapter.setToEndEnabled(true);
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

        //加载更多数据
        commonListAdapter.setLoadMoreListener(new LoadMoreListener<AppInfo>() {

            @Override
            public int onLoadMoreRequested(List<AppInfo> out, int startPosition, int requestSize) {
                if (count >= DataServer.MaxCount) {
                    LogUtils.e("mlr 没有更多数据");
                } else {
                    LogUtils.e("mlr 请求更多数据");
                    out.addAll(DataServer.getCommonMoreData(requestSize));
                    count++;
                }
                return 200;
            }

        });

        rvCommonList.setAdapter(commonListAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                LogUtils.e("testbbs setOnRefreshListener  onRefresh");
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        commonListAdapter.getData().clear();
                        commonListAdapter.setData(DataServer.getCommonData(20));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    private View createHeadView(String headerText) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.common_list_item, null, false);
        TextView textView = (TextView) inflate.findViewById(R.id.tweetText);
        textView.setText(headerText);
        return inflate;
    }

}
