package com.app.ctatracker.rest;

import android.content.Context;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://www.ctabustracker.com/";
    private static Retrofit retrofit = null;
    private static Retrofit retrofitWithCache = null;


    private static OkHttpClient getOkHttpClient(boolean withCache, Context context) {
        if (withCache) {
            int cacheSize = 20 * 1024 * 1024; // 20 MB
            Cache cache = new Cache(context.getCacheDir(), cacheSize);

            return new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addNetworkInterceptor(chain -> {
                        Response response = chain.proceed(chain.request());
                        CacheControl cacheControl = new CacheControl.Builder()
                                .maxAge(24, TimeUnit.HOURS) // cache the response for 24 hours
                                .build();
                        return response.newBuilder()
                                .header("Cache-Control", cacheControl.toString())
                                .build();
                    })
                    .build();
        } else {
            return new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();
        }
    }

    public static Retrofit getClient(boolean withCache, Context context) {
        if (withCache) {
            if (retrofitWithCache == null) {
                retrofitWithCache = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getOkHttpClient(true, context))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofitWithCache;
        } else {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getOkHttpClient(false, context))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
}
