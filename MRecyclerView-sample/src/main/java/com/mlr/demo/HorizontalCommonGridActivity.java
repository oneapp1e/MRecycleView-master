package com.mlr.demo;

import android.os.Bundle;

import com.mlr.demo.adapter.CommonGridAdapter;
import com.mlr.mrecycleview.BaseActivity;
import com.mlr.mrecycleview.MRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HorizontalCommonGridActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_common_list);

        MRecyclerView rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);

        List<String> list = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            list.add("common" + i);
        }

        CommonGridAdapter commonListAdapter = new CommonGridAdapter(this, list);
        //启动到底了试图
        commonListAdapter.setToEndEnabled(true, rvCommonList);
        rvCommonList.setAdapter(commonListAdapter);
    }
}
