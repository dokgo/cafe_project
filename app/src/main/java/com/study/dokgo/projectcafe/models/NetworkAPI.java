package com.study.dokgo.projectcafe.models;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dokgo on 27.11.16.
 */

public interface NetworkAPI {
    @GET("select.php")
    Observable<CafeService> getCafeServiceData();

    @GET("selectById.php")
    Observable<CafeService> getCafeById(@Query("id") String id);

    @FormUrlEncoded
    @POST("insertCafe.php")
    Observable<String> insertCafe(
            @Field("address") String address,
            @Field("rank") String rank,
            @Field("description") String description,
            @Field("name") String name,
            @Field("src") String src
    );

    @FormUrlEncoded
    @POST("updateCafe.php")
    Observable<String> updateCafe(
            @Field("address") String address,
            @Field("rank") String rank,
            @Field("description") String description,
            @Field("name") String name,
            @Field("src") String src,
            @Field("id") String id

    );

    @FormUrlEncoded
    @POST("delete.php")
    Observable<String> delete(
            @Field("table") String table,
            @Field("pkey") String pkey,
            @Field("id") String id

    );

}
