package com.mlr.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.adapter.CommonGridAdapter;
import com.mlr.demo.data.DataServer;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.mrecyclerview.MRecyclerView;

public class CommonGridActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);

        MRecyclerView rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);

        CommonGridAdapter commonListAdapter = new CommonGridAdapter(this, DataServer.getCommonData(20));
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
    }

    private View createHeadView(String headerText) {
        View inflate = inflate(R.layout.common_list_item);
        TextView textView = (TextView) inflate.findViewById(R.id.tweetText);
        textView.setText(headerText);
        return inflate;
    }

}
