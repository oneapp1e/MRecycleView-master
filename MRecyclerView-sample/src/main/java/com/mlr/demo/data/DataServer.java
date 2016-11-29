package com.mlr.demo.data;

import com.mlr.demo.model.AppInfo;
import com.mlr.demo.model.TitleInfo;
import com.mlr.model.ViewTypeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mulinrui on 2016/11/29.
 */
public class DataServer {

    // ==========================================================================
    // Constants
    // ==========================================================================
    public static final int MaxCount = 3;//假数据 最多加载3次更多数据

    public static final int spanCount = 2;

    // ==========================================================================
    // Fields
    // ==========================================================================


    // ==========================================================================
    // Constructors
    // ==========================================================================


    // ==========================================================================
    // Getters
    // ==========================================================================
    public static List<AppInfo> getCommonData(int length) {
        List<AppInfo> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppName("common" + i);
            list.add(appInfo);
        }
        return list;
    }

    public static List<AppInfo> getCommonMoreData(int length) {
        List<AppInfo> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppName("more data " + i);
            list.add(appInfo);
        }
        return list;
    }

    public static List<ViewTypeInfo> getSectionData(int length) {
        List<ViewTypeInfo> list = new ArrayList<>(length);
        for (int j = 0; j < length; j++) {
            TitleInfo titleInfo = new TitleInfo();
            titleInfo.setTitle("title" + j);
            list.add(titleInfo);
            for (int i = 0; i < 5; i++) {
                AppInfo appInfo = new AppInfo();
                appInfo.setAppName("title" + j + " appName" + i);
                list.add(appInfo);
            }
        }
        return list;
    }

    public static List<ViewTypeInfo> getSectionMoreData(int length) {
        List<ViewTypeInfo> list = new ArrayList<>(length);
        for (int j = 0; j < length; j++) {
            TitleInfo titleInfo = new TitleInfo();
            titleInfo.setTitle("more title" + j);
            list.add(titleInfo);
            for (int i = 0; i < 5; i++) {
                AppInfo appInfo = new AppInfo();
                appInfo.setAppName("more title" + j + " appName" + i);
                list.add(appInfo);
            }
        }
        return list;
    }

    // ==========================================================================
    // Setters
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

}
