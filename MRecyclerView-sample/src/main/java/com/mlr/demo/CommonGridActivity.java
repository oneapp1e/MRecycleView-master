package com.mlr.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.adapter.CommonGridAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.utils.BaseActivity;
import com.mlr.utils.LoadMoreListener;
import com.mlr.utils.LogUtils;

import java.util.List;

public class CommonGridActivity extends BaseActivity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);

        final MRecyclerView rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);

        final CommonGridAdapter commonListAdapter = new CommonGridAdapter(this, DataServer.getCommonData(20));
        //启动到底了试图
        commonListAdapter.setToEndEnabled(true);
        //添加headerView
        View headerView1 = createHeadView("headerView1");
        commonListAdapter.addHeaderView(headerView1);
        View headerView2 = createHeadView("headerView2");
        commonListAdapter.addHeaderView(headerView2);

        //加载更多数据
        commonListAdapter.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public int onLoadMoreRequested(List out, int startPosition, int requestSize) {
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

        rvCommonList.showShimmerAdapter();
        rvCommonList.postDelayed(new Runnable() {
            @Override
            public void run() {
                commonListAdapter.setData(DataServer.getCommonData(20));
                rvCommonList.hideShimmerAdapter();
            }
        }, 30000);
    }

    private View createHeadView(String headerText) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.common_list_item, null, false);
        TextView textView = (TextView) inflate.findViewById(R.id.tweetText);
        textView.setText(headerText);
        return inflate;
    }

}
