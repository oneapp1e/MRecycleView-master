package com.mlr.demo;

import android.os.Bundle;
import android.view.View;

import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.mvp.View.NewsListView;
import com.mlr.demo.adapter.NewsListAdapter;
import com.mlr.mvp.entity.NewsSummary;
import com.mlr.mvp.presenter.NewsListPresenter;
import com.mlr.mvp.presenter.impl.NewsListPresenterImpl;
import com.mlr.utils.BaseActivity;
import com.mlr.utils.LoadMoreListener;
import com.mlr.utils.LogUtils;

import java.util.List;

/**
 * Created by mulinrui on 12/6 0006.
 */
public class Retrofit2Activity extends BaseActivity implements NewsListView {

    private NewsListPresenter mNewsListPresenter;

    NewsListAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);

        initLoadingAndRetryManager();

        mNewsListPresenter = new NewsListPresenterImpl(this);


        MRecyclerView mRecyclerView = (MRecyclerView) findViewById(R.id.rv_common_list);

        mRecyclerViewAdapter = new NewsListAdapter(getActivity(), null);
        mRecyclerViewAdapter.setLoadMoreListener(new LoadMoreListener<NewsSummary>() {
            @Override
            public int onLoadMoreRequested(List<NewsSummary> out, int startPosition, int requestSize) {
                return mNewsListPresenter.loadMore(out, startPosition, requestSize);
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        //启动到底了试图
        mRecyclerViewAdapter.setToEndEnabled(true, mRecyclerView);
        showProgress();
        mNewsListPresenter.refreshData();
    }


    @Override
    public void setNewList(List<NewsSummary> lists) {
        hideProgress();
        mRecyclerViewAdapter.setData(lists);
    }

    @Override
    public void setEmptyView() {
        mLoadingAndRetryManager.showEmpty();
    }

    @Override
    public void setRetryView() {
        mLoadingAndRetryManager.showRetry();
    }

    @Override
    public void setRetryEvent(View retryView) {
        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                LogUtils.e("testbbs setRetryEvent 需要重新请求");
                mNewsListPresenter.refreshData();
            }
        });
    }

    @Override
    public void setEmptyEvent(View emptyView) {
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                LogUtils.e("testbbs setEmptyEvent 需要重新请求");
                mNewsListPresenter.refreshData();
            }
        });
    }

}
