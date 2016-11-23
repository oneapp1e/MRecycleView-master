package com.mlr.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mlr.mrecyclerview.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}
