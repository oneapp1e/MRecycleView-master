package com.mlr.demo.utils;

import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.mlr.mvp.retrofit2.RetrofitManager;

import java.io.InputStream;

import okhttp3.Call;

/**
 * Created by mulinrui on 9/19 0019.
 */

public class MyOkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private final Call.Factory client;

    public MyOkHttpUrlLoader(Call.Factory client) {
        this.client = client;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(GlideUrl glideUrl, int width, int height, Options options) {
        return new LoadData<>(glideUrl, new MyOkHttpStreamFetcher(client, glideUrl));
    }

    @Override
    public boolean handles(GlideUrl glideUrl) {
        return true;
    }


    /**
     * The default factory for {@link MyOkHttpStreamFetcher}s.
     */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static volatile Call.Factory internalClient;
        private Call.Factory client;

        /**
         * Constructor for a new Factory that runs requests using a static singleton client.
         */
        public Factory() {
            this(getInternalClient());
        }

        /**
         * Constructor for a new Factory that runs requests using given client.
         *
         * @param client this is typically an instance of {@code OkHttpClient}.
         */
        public Factory(Call.Factory client) {
            this.client = client;
        }

        private static Call.Factory getInternalClient() {
            if (internalClient == null) {
                synchronized (MyOkHttpUrlLoader.Factory.class) {
                    if (internalClient == null) {
                        internalClient = RetrofitManager.getOkHttpClient();
                    }
                }
            }
            return internalClient;
        }


        @Override
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new MyOkHttpUrlLoader(client);
        }


        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
