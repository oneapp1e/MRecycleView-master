package com.mlr.mvp.retrofit2;


import com.mlr.mvp.entity.ReasonResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by mulinrui on 12/12 0012.
 */
public interface NewListService {

    /**
     * @return
     */
    @GET("weixin/query")
    Call<ReasonResult> getNewList(
            @Header("Cache-Control") String cacheControl, @Query("key") String key,
            @Query("pno") int page, @Query("ps") int rows);

    /**
     * http://v.juhe.cn/toutiao/index
     *
     * @return
     */
    @GET("toutiao/index")
    Call<ReasonResult> getNewList(
            @Header("Cache-Control") String cacheControl, @Query("key") String key);

}
