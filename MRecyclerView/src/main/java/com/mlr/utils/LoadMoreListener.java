package com.mlr.utils;

import com.mlr.model.ViewTypeInfo;

import java.util.List;

/**
 * 加载更多监听
 * Created by mulinrui on 2016/12/1.
 */
public interface LoadMoreListener<Data extends ViewTypeInfo> {

    /**
     * @param out           得到的数据列表
     * @param startPosition 起始位置
     * @param requestSize   请求数量
     * @return Status code
     */
    int onLoadMoreRequested(List<Data> out, int startPosition, int requestSize);
}
