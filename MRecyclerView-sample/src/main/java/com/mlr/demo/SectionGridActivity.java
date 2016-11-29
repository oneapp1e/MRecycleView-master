package com.mlr.demo;

import android.os.Bundle;

import com.mlr.demo.adapter.SectionGridAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.mrecyclerview.SectionMRecyclerView;

public class SectionGridActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_list);

        SectionMRecyclerView rvCommonList = (SectionMRecyclerView) findViewById(R.id.rv_common_list);

        SectionGridAdapter sectionGridAdapter = new SectionGridAdapter(this, DataServer.getSectionData(5),rvCommonList);
        //启动到底了试图
        sectionGridAdapter.setToEndEnabled(true, rvCommonList);

        rvCommonList.setAdapter(sectionGridAdapter);


    }

}
