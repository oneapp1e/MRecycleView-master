package com.mlr.mvp.presenter.impl;

import com.mlr.demo.adapter.NewsListAdapter;
import com.mlr.mvp.View.NewsListView;
import com.mlr.mvp.entity.NewsSummary;
import com.mlr.mvp.entity.ReasonResult;
import com.mlr.mvp.presenter.NewsListPresenter;
import com.mlr.mvp.retrofit2.RetrofitManager;
import com.mlr.utils.LogUtils;
import com.mlr.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mulinrui on 12/16 0016.
 */
public class NewsListPresenterImpl extends BasePresenterImpl<NewsListView> implements NewsListPresenter {

    String key = "3c89f27b420ebe71416b55b6aab6b71c";

    public NewsListPresenterImpl(NewsListView view) {
        super(view);
    }

    @Override
    public void refreshData() {

        //重新加载数据
        final List<NewsSummary> newsList = new ArrayList<>();
        Call<ReasonResult> call = RetrofitManager.getInstance().getNewsList(key, 1, 20);
        call.enqueue(new Callback<ReasonResult>() {
            @Override
            public void onResponse(Call<ReasonResult> call,
                                   retrofit2.Response<ReasonResult> response) {
                if (response != null && response.body() != null) {
                    List<NewsSummary> result = response.body().getResult().getData();
                    LogUtils.e("testbbs refreshData 请求成功了  code:" + response);
                    for (NewsSummary newsSummary : result) {
                        if (StringUtils.isEmpty(newsSummary.getThumbnail_pic_s02())) {
                            newsSummary.setViewType(NewsListAdapter.VIEW_TYPE_COMMON_ITEM);
                        } else {
                            newsSummary.setViewType(NewsListAdapter.VIEW_TYPE_PHONE_ITEM);
                        }
                    }
                    newsList.addAll(result);
                }

                if (newsList.size() > 0) {
                    mView.setNewList(newsList);
                } else {
                    mView.setEmptyView();
                }
            }

            @Override
            public void onFailure(Call<ReasonResult> call, Throwable t) {
                LogUtils.e("testbbs refreshData 请求失败了   t:" + t.toString());
                mView.setRetryView();
            }
        });
    }

    @Override
    public int loadMore(final List<NewsSummary> out, int startPosition, int requestSize) {
        int page = startPosition / requestSize + 1;
        //加载更多数据
        Call<ReasonResult> call = RetrofitManager.getInstance().getNewsList(key, page, requestSize);
        Response<ReasonResult> response = null;
        try {
            response = call.execute();
        } catch (Exception t) {
            LogUtils.e("testbbs loadMore 请求失败了   t:" + t.toString());
        }
        if (response != null && response.body() != null) {
            List<NewsSummary> result = response.body().getResult().getData();
            LogUtils.e("testbbs loadMore 请求成功了  code:" + response.body());
            for (NewsSummary newsSummary : result) {
                if (StringUtils.isEmpty(newsSummary.getThumbnail_pic_s02())) {
                    newsSummary.setViewType(NewsListAdapter.VIEW_TYPE_COMMON_ITEM);
                } else {
                    newsSummary.setViewType(NewsListAdapter.VIEW_TYPE_PHONE_ITEM);
                }
            }
            out.addAll(result);
            return response.code();
        }

        return -1;
    }
}
