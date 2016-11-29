package com.mlr.demo;

import android.os.Bundle;

import com.mlr.demo.adapter.CommonGridAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.mrecyclerview.MRecyclerView;

public class HorizontalCommonGridActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_common_list);

        MRecyclerView rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);

        CommonGridAdapter commonListAdapter = new CommonGridAdapter(this, DataServer.getCommonData(20));
        //启动到底了试图
        commonListAdapter.setToEndEnabled(true, rvCommonList);
        rvCommonList.setAdapter(commonListAdapter);
    }
}
