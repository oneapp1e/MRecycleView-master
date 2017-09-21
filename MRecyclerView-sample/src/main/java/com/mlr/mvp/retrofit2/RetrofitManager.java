package com.mlr.mvp.retrofit2;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.WebSettings;

import com.mlr.MyApplication;
import com.mlr.mvp.entity.ReasonResult;
import com.mlr.utils.LogUtil;
import com.mlr.utils.LogUtils;
import com.mlr.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mulinrui on 12/12 0012.
 */
public class RetrofitManager {

    private NewListService mNewsService;

    private static RetrofitManager retrofitManager;

    private static volatile OkHttpClient sOkHttpClient;

    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;

    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";


    /**
     *
     */
    public static RetrofitManager getInstance() {
        if (retrofitManager == null) {
            synchronized (RetrofitManager.class) {
                retrofitManager = new RetrofitManager();
                return retrofitManager;
            }
        }
        return retrofitManager;
    }

    private RetrofitManager() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://v.juhe.cn/")
                .client(getOkHttpClient()).addConverterFactory(GsonConverterFactory.create())
                .build();
        mNewsService = retrofit.create(NewListService.class);
    }

    public static OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                Cache cache = new Cache(new File(MyApplication.getAppContext().getCacheDir(), "HttpCache"),
                        1024 * 1024 * 100);
                if (sOkHttpClient == null) {

                    /**
                     * 云端响应头拦截器，用来配置缓存策略
                     * Dangerous interceptor that rewrites the server's cache-control header.
                     */
                    final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            if (!NetworkUtils.isConnected(MyApplication.getAppContext())) {
                                request = request.newBuilder()
                                        .cacheControl(CacheControl.FORCE_CACHE)
                                        .build();
                                LogUtils.d("no network");
                            }
                            Response originalResponse = chain.proceed(request);
                            if (NetworkUtils.isConnected(MyApplication.getAppContext())) {
                                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                                String cacheControl = request.cacheControl().toString();
                                return originalResponse.newBuilder()
                                        .header("Cache-Control", cacheControl)
                                        .removeHeader("Pragma")
                                        .build();
                            } else {
                                return originalResponse.newBuilder()
                                        .header("Cache-Control", "public," + CACHE_CONTROL_CACHE)
                                        .removeHeader("Pragma")
                                        .build();
                            }
                        }
                    };


                    final Interceptor headerInterceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            response.newBuilder().removeHeader("User-Agent")
                                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                    .addHeader("Accept-Encoding", "gzip, deflate")
                                    .addHeader("Connection", "keep-alive")
                                    .addHeader("Accept", "*/*")
                                    .addHeader("User-Agent", getUserAgent(MyApplication.getAppContext()))
                                    .build();
                            return response;
                        }
                    };

                    HttpLoggingInterceptor mHttpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            LogUtils.e("mlr message:" + message);
                        }
                    });


                    //设置log日志级别
                    mHttpLoggingInterceptor
                            .setLevel(HttpLoggingInterceptor.Level.BODY);

                    sOkHttpClient = new OkHttpClient.Builder().cache(cache)
                            .connectTimeout(6, TimeUnit.SECONDS)
                            .readTimeout(6, TimeUnit.SECONDS)
                            .writeTimeout(6, TimeUnit.SECONDS)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(mHttpLoggingInterceptor)
                            .addInterceptor(headerInterceptor)
                            .build();
                }
            }
        }
        return sOkHttpClient;
    }


    private static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        LogUtil.e("mlr getUserAgent:" + sb.toString());
        return sb.toString();
    }

    /**
     * 根据网络状况获取缓存的策略
     */
    @NonNull
    private String getCacheControl() {
        return NetworkUtils.isConnected(MyApplication.getAppContext()) ? CACHE_CONTROL_AGE : CACHE_CONTROL_CACHE;
    }

    /**
     */
    public Call<ReasonResult> getNewsList(String key, int page, int rows) {
        return mNewsService.getNewList(getCacheControl(), key);
//        return mNewsService.getNewList(getCacheControl(), key, page, rows);
    }

}
