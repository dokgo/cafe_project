package com.study.dokgo.projectcafe.presenter;

import android.util.Log;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dokgo on 28.11.16.
 */

public class RetrofitAPI {
    private static Retrofit retrofit;

    public RetrofitAPI() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(msg -> Log.i("OK_HTTP", msg));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        String baseUrl = "http://cafepro.esy.es/";

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
