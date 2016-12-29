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
    Observable<Cafes> getCafesData();

    @GET("selectByFilter.php")
    Observable<Cafes> getCafesData(@Query("rank") String rank);

    @GET("selectCafeById.php")
    Observable<Cafes> getCafeById(@Query("id") String id);

    @GET("selectDishById.php")
    Observable<Dishes> getDishById(@Query("id") String id);

    @GET("selectMenu.php")
    Observable<Dishes> getMenuById(@Query("id") String id);

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
    @POST("insertDish.php")
    Observable<String> insertDish(
            @Field("name") String name,
            @Field("type") String type,
            @Field("portion") String portion,
            @Field("cost") String cost,
            @Field("time") String time,
            @Field("cuisine") String cuisine,
            @Field("cafeId") String cafeId
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
    @POST("updateDish.php")
    Observable<String> updateDish(
            @Field("name") String name,
            @Field("type") String type,
            @Field("portion") String portion,
            @Field("cost") String cost,
            @Field("time") String time,
            @Field("cuisine") String cuisine,
            @Field("dishId") String dishId

    );

    @FormUrlEncoded
    @POST("delete.php")
    Observable<String> delete(
            @Field("table") String table,
            @Field("pkey") String pkey,
            @Field("id") String id

    );

}
