package com.mlr.demo.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.mlr.demo.R;

import java.io.InputStream;

/**
 * Created by mulinrui on 9/18 0018.
 */
@GlideModule
public class MyAppGlideModule extends AppGlideModule {


    @Override
    public boolean isManifestParsingEnabled() {
        return false;//是否启用manifest的配置
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        RequestOptions options = new RequestOptions();
        options.format(DecodeFormat.PREFER_ARGB_8888);
        options.placeholder(R.color.image_place_holder);//加载中图片
        options.error(R.drawable.ic_load_fail);//加载错误的图片
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        builder.setDefaultRequestOptions(options);
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        //配置glide网络加载框架
        registry.replace(GlideUrl.class, InputStream.class, new MyOkHttpUrlLoader.Factory());
    }
}
