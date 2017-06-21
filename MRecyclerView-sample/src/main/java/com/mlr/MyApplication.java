package com.mlr;

import android.app.Application;
import android.content.Context;

import com.mlr.demo.BuildConfig;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Created by mulinrui on 12/5 0005.
 */
public class MyApplication extends Application {

    private static Context sAppContext;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        SophixManager.getInstance()
                .setContext(this)
//                .setAesKey()//用户自定义aes秘钥
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
//                            LogUtils.e("mlr 补丁加载成功");
                            System.out.println("mlr 补丁加载成功");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
//                            LogUtils.e("mlr 新补丁生效需要重启");
                            System.out.println("mlr 新补丁生效需要重启");
                            SophixManager.getInstance().killProcessSafely();
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
//                            LogUtils.e("mlr 内部引擎异常");
                            System.out.println("mlr 内部引擎异常");
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
//                            SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
//                            LogUtils.e("mlr 其它错误信息" + code);
                            System.out.println("mlr 其它错误信息" + code);
                        }
                    }
                }).initialize();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
