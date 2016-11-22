package com.mlr.utils;

import android.view.MotionEvent;

/**
 * Created by liubin on 2016/1/21.
 * TouchEvent拦截器
 */
public interface TouchEventInterceptor {

    /**
     * event是否被子类优先消耗
     * @param event
     * @return true 被消耗;false 没有消耗
     */
    public boolean intercepted(MotionEvent event);

}
