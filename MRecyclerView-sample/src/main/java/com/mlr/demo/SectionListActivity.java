package com.mlr.demo;

import android.os.Bundle;

import com.mlr.demo.adapter.SectionListAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.mrecyclerview.SectionMRecyclerView;
import com.mlr.utils.BaseActivity;
import com.mlr.utils.LoadMoreListener;
import com.mlr.utils.LogUtils;

import java.util.List;

public class SectionListActivity extends BaseActivity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_list);

        SectionMRecyclerView rvCommonList = (SectionMRecyclerView) findViewById(R.id.rv_common_list);

        SectionListAdapter sectionListAdapter = new SectionListAdapter(this, DataServer.getSectionData(5), rvCommonList);
        //启动到底了试图
        sectionListAdapter.setToEndEnabled(true);

        //加载更多数据
        sectionListAdapter.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public int onLoadMoreRequested(List out, int startPosition, int requestSize) {
                if (count >= DataServer.MaxCount) {
                    LogUtils.e("mlr 没有更多数据");
                } else {
                    LogUtils.e("mlr 请求更多数据");
                    out.addAll(DataServer.getSectionMoreData(requestSize));
                    count++;
                }
                return 200;
            }
        });

        rvCommonList.setAdapter(sectionListAdapter);
    }

}
