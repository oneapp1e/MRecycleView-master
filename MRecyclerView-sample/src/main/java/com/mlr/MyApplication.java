package com.mlr;

import android.app.Application;
import android.content.Context;

/**
 * Created by mulinrui on 12/5 0005.
 */
public class MyApplication extends Application {

    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
