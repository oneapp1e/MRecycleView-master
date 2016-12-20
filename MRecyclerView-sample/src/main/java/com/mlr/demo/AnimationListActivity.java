package com.mlr.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.demo.adapter.CommonGridAdapter;
import com.mlr.demo.animation.CustomAnimation;
import com.mlr.demo.data.DataServer;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.utils.BaseActivity;
import com.mlr.utils.LoadMoreListener;
import com.mlr.utils.LogUtils;

import java.util.List;

public class AnimationListActivity extends BaseActivity {

    private int count = 0;
    private CommonGridAdapter commonListAdapter;
    private MRecyclerView rvCommonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_list);

        rvCommonList = (MRecyclerView) findViewById(R.id.rv_common_list);


        commonListAdapter = new CommonGridAdapter(this, DataServer.getCommonData(20));
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
        //打开动画
        commonListAdapter.openLoadAnimation();

        rvCommonList.setAdapter(commonListAdapter);

        initMenu();

    }

    private View createHeadView(String headerText) {
        View inflate = inflate(R.layout.common_list_item);
        TextView textView = (TextView) inflate.findViewById(R.id.tweetText);
        textView.setText(headerText);
        return inflate;
    }

    private void initMenu() {
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("AlphaIn", "ScaleIn", "SlideInBottom", "SlideInLeft", "SlideInRight", "Custom");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                switch (position) {
                    case 0:
                        commonListAdapter.openLoadAnimation(MRecyclerViewAdapter.ALPHAIN);
                        break;
                    case 1:
                        commonListAdapter.openLoadAnimation(MRecyclerViewAdapter.SCALEIN);
                        break;
                    case 2:
                        commonListAdapter.openLoadAnimation(MRecyclerViewAdapter.SLIDEIN_BOTTOM);
                        break;
                    case 3:
                        commonListAdapter.openLoadAnimation(MRecyclerViewAdapter.SLIDEIN_LEFT);
                        break;
                    case 4:
                        commonListAdapter.openLoadAnimation(MRecyclerViewAdapter.SLIDEIN_RIGHT);
                        break;
                    case 5:
                        commonListAdapter.openLoadAnimation(new CustomAnimation());
                        break;
                    default:
                        break;
                }
                Toast.makeText(AnimationListActivity.this, "切换动画完成", Toast.LENGTH_LONG).show();
                commonListAdapter.notifyDataSetChanged();
            }
        });
        MaterialSpinner spinnerFirstOnly = (MaterialSpinner) findViewById(R.id.spinner_first_only);
        spinnerFirstOnly.setItems("isFirstOnly(true)", "isFirstOnly(false)");
        spinnerFirstOnly.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                switch (position) {
                    case 0:
                        commonListAdapter.isFirstOnly(true);
                        break;
                    case 1:
                        commonListAdapter.isFirstOnly(false);
                        break;
                    default:
                        break;
                }
                commonListAdapter.notifyDataSetChanged();
            }
        });
    }

}
