package com.mlr.mvp.entity;

import java.util.List;

/**
 * 结果
 */
public class DataResult {

    private String stat;
    private List<NewsSummary> data;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public List<NewsSummary> getData() {
        return data;
    }

    public void setData(List<NewsSummary> data) {
        this.data = data;
    }
}
