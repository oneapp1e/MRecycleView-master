package com.mlr.demo;

import android.os.Bundle;

import com.mlr.demo.adapter.CommonGridAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.utils.BaseActivity;
import com.mlr.utils.LoadMoreListener;
import com.mlr.utils.LogUtils;

import java.util.List;

public class HorizontalCommonGridActivity extends BaseActivity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_common_list);

        MRecyclerView rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);

        CommonGridAdapter commonListAdapter = new CommonGridAdapter(this, DataServer.getCommonData(20));
        //启动到底了试图
        commonListAdapter.setToEndEnabled(true);

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
    }
}
