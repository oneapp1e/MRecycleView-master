package com.mlr.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.adapter.CommonListAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.demo.model.AppInfo;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.utils.LoadMoreListener;
import com.mlr.utils.LogUtils;

import java.util.List;

public class CommonListActivity extends BaseActivity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);

        MRecyclerView rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);


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

    }

    private View createHeadView(String headerText) {
        View inflate = inflate(R.layout.common_list_item);
        TextView textView = (TextView) inflate.findViewById(R.id.tweetText);
        textView.setText(headerText);
        return inflate;
    }

}
