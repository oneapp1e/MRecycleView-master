package com.mlr.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.mlr.utils.BaseActivity;
import com.taobao.sophix.SophixManager;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLoadingAndRetryManager();
        hideProgress();
        SophixManager.getInstance().queryAndLoadNewPatch();

        View btnCommonList = findViewById(R.id.btn_common_list);
        btnCommonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入普通list
                Intent intent = new Intent(MainActivity.this, CommonListActivity.class);
                startActivity(intent);
            }
        });

        View btnHorizontalCommonList = findViewById(R.id.btn_horizontal_common_List);
        btnHorizontalCommonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入水平list
                Intent intent = new Intent(MainActivity.this, HorizontalCommonListActivity.class);
                startActivity(intent);
            }
        });

        View btnCommonGrid = findViewById(R.id.btn_common_grid);
        btnCommonGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入普通grid
                Intent intent = new Intent(MainActivity.this, CommonGridActivity.class);
                startActivity(intent);
            }
        });

        View btnHorizontalCommonGrid = findViewById(R.id.btn_horizontal_common_grid);
        btnHorizontalCommonGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入水平grid
                Intent intent = new Intent(MainActivity.this, HorizontalCommonGridActivity.class);
                startActivity(intent);
            }
        });

        View btnMixListGrid = findViewById(R.id.btn_mix_list_grid);
        btnMixListGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入混合
                Intent intent = new Intent(MainActivity.this, MixListGridActivity.class);
                startActivity(intent);
            }
        });

        View btnSectionList = findViewById(R.id.btn_section_list);
        btnSectionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入SectionList
                Intent intent = new Intent(MainActivity.this, SectionListActivity.class);
                startActivity(intent);
            }
        });

        View btnSectionGrid = findViewById(R.id.btn_section_grid);
        btnSectionGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入SectionGrid
                Intent intent = new Intent(MainActivity.this, SectionGridActivity.class);
                startActivity(intent);
            }
        });

        View btnDragMoveList = findViewById(R.id.btn_drag_move_list);
        btnDragMoveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DragMoveListActivity.class);
                startActivity(intent);
            }
        });

        View btnDragMoveGrid = findViewById(R.id.btn_drag_move_grid);
        btnDragMoveGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DragMoveGridActivity.class);
                startActivity(intent);
            }
        });

        View btnPullToRefreshListView = findViewById(R.id.btn_pull_to_refresh_list);
        btnPullToRefreshListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PullToRefreshListActivity.class);
                startActivity(intent);
            }
        });

        View btnSwipeRefreshList = findViewById(R.id.btn_Swipe_refresh_list);
        btnSwipeRefreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SwipeRefreshListActivity.class);
                startActivity(intent);
            }
        });

        final View btnAnimationList = findViewById(R.id.btn_animation_list);
        btnAnimationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AnimationListActivity.class, btnAnimationList);
            }
        });

        final View btnRetrofit2 = findViewById(R.id.btn_retrofit2);
        btnRetrofit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Retrofit2Activity.class);
                startActivity(intent);
            }
        });
    }


    private void startActivity(Class cls, View view) {
        Intent intent = new Intent(MainActivity.this, cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, view, getString(R.string.app_name));
            startActivity(intent, options.toBundle());
        } else {

            startActivity(intent);
        }
    }
}
