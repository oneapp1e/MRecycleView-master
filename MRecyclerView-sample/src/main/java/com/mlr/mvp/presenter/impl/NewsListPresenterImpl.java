package com.mlr.mvp.presenter.impl;

import com.mlr.demo.adapter.NewsListAdapter;
import com.mlr.mvp.View.NewsListView;
import com.mlr.mvp.entity.NewsSummary;
import com.mlr.mvp.presenter.NewsListPresenter;
import com.mlr.mvp.retrofit2.RetrofitManager;
import com.mlr.utils.LogUtils;
import com.mlr.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mulinrui on 12/16 0016.
 */
public class NewsListPresenterImpl extends BasePresenterImpl<NewsListView> implements NewsListPresenter {

    /**
     * 请求网易新闻头条的id
     */
    String id = "T1348647853363";

    public NewsListPresenterImpl(NewsListView view) {
        super(view);
    }

    @Override
    public void refreshData() {

        //重新加载数据
        final List<NewsSummary> newsList = new ArrayList<>();
        Call<Map<String, List<NewsSummary>>> call = RetrofitManager.getInstance().getNewsList(id, 0, 20);
        call.enqueue(new Callback<Map<String, List<NewsSummary>>>() {
            @Override
            public void onResponse(Call<Map<String, List<NewsSummary>>> call, retrofit2.Response<Map<String, List<NewsSummary>>> response) {
                if (response != null && response.body() != null) {
                    List<NewsSummary> t1348647909107 = response.body().get(id);
                    LogUtils.e("testbbs refreshData 请求成功了  code:" + response.code());
                    for (NewsSummary newsSummary : t1348647909107) {
                        if (StringUtils.isEmpty(newsSummary.getDigest())) {
                            newsSummary.setViewType(NewsListAdapter.VIEW_TYPE_COMMON_ITEM);
                        } else {
                            newsSummary.setViewType(NewsListAdapter.VIEW_TYPE_PHONE_ITEM);
                        }
                    }
                    newsList.addAll(t1348647909107);
                }

                if (newsList.size() > 0) {
                    mView.setNewList(newsList);
                } else {
                    mView.setEmptyView();
                }
            }

            @Override
            public void onFailure(Call<Map<String, List<NewsSummary>>> call, Throwable t) {
                LogUtils.e("testbbs refreshData 请求失败了   t:" + t.toString());
                mView.setRetryView();
            }
        });
    }

    @Override
    public int loadMore(final List<NewsSummary> out, int startPosition, int requestSize) {
        //加载更多数据
        Call<Map<String, List<NewsSummary>>> call = RetrofitManager.getInstance().getNewsList(id, 20, requestSize);
        Response<Map<String, List<NewsSummary>>> response = null;
        try {
            response = call.execute();
        } catch (Exception t) {
            LogUtils.e("testbbs loadMore 请求失败了   t:" + t.toString());
        }
        if (response != null && response.body() != null) {
            List<NewsSummary> t1348647909107 = response.body().get(id);
            LogUtils.e("testbbs loadMore 请求成功了  code:" + response.code());
            for (NewsSummary newsSummary : t1348647909107) {
                if (StringUtils.isEmpty(newsSummary.getDigest())) {
                    newsSummary.setViewType(NewsListAdapter.VIEW_TYPE_COMMON_ITEM);
                } else {
                    newsSummary.setViewType(NewsListAdapter.VIEW_TYPE_PHONE_ITEM);
                }
            }
            out.addAll(t1348647909107);
            return response.code();
        }

        return -1;
    }
}
