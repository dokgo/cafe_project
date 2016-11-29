package com.study.dokgo.projectcafe.presenter;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dokgo on 28.11.16.
 */

public class RetrofitAPI {
    private static Retrofit retrofit;

    public RetrofitAPI(){
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .baseUrl("http://test.site/")
                .build();
    }
    public Retrofit getRetrofit(){
        return retrofit;
    }
}
