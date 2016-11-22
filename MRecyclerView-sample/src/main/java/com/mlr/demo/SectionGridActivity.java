package com.mlr.demo;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.adapter.SectionGridAdapter;
import com.mlr.demo.model.AppInfo;
import com.mlr.demo.model.TitleInfo;
import com.mlr.model.ViewTypeInfo;
import com.mlr.mrecycleview.BaseActivity;
import com.mlr.mrecycleview.SectionMRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SectionGridActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_list);

        SectionMRecyclerView rvCommonList = (SectionMRecyclerView) findViewById(R.id.rv_common_list);

        List<ViewTypeInfo> list = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            TitleInfo titleInfo = new TitleInfo();
            titleInfo.setTitle("title" + j);
            list.add(titleInfo);
            for (int i = 0; i < 5; i++) {
                AppInfo appInfo = new AppInfo();
                appInfo.setAppName("title" + j + " appName" + i);
                list.add(appInfo);
            }
        }


        SectionGridAdapter sectionGridAdapter = new SectionGridAdapter(this, list,rvCommonList);
        //启动到底了试图
        sectionGridAdapter.setToEndEnabled(true, rvCommonList);

        rvCommonList.setAdapter(sectionGridAdapter);


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
