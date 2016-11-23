package com.mlr.demo;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.adapter.CommonGridAdapter;
import com.mlr.mrecyclerview.BaseActivity;
import com.mlr.mrecyclerview.MRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CommonGridActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);

        MRecyclerView rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);

        List<String> list = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            list.add("common" + i);
        }

        CommonGridAdapter commonListAdapter = new CommonGridAdapter(this, list);
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
        TextView textView = new TextView(this);
        int padding = dip2px(5);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dip2px(10));
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(headerText);
        return textView;
    }

}
