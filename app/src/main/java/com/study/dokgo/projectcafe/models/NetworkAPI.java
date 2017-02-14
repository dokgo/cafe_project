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
    @GET("login.php")
    Observable<User> login(@Query("email") String email, @Query("pass") String pass);

    @GET("register.php")
    Observable<User> register(@Query("email") String email, @Query("pass") String pass);

@GET("select.php")
    Observable<Cafes> getCafesData();

    @GET("selectByFilter.php")
    Observable<Cafes> getCafesData(@Query("rank") String rank, @Query("email") String email);

    @GET("selectCafeById.php")
    Observable<Cafes> getCafeById(@Query("id") String id);

    @GET("selectDishById.php")
    Observable<Dishes> getDishById(@Query("id") String id);

    @GET("selectDrinkById.php")
    Observable<Drinks> getDrinkById(@Query("id") String id);

    @GET("selectTobaccoById.php")
    Observable<Tobaccos> getTobaccoById(@Query("id") String id);

    @GET("selectDrinks.php")
    Observable<Drinks> getDrinksById(@Query("id") String id);

    @GET("selectTobaccos.php")
    Observable<Tobaccos> getTobaccosById(@Query("id") String id);

    @GET("selectComments.php")
    Observable<Comments> getCommentsById(@Query("id") String id);

    @GET("selectCommentById.php")
    Observable<Comments> getCommentById(@Query("id") String id);

    @GET("selectMenu.php")
    Observable<Dishes> getMenuById(@Query("id") String id);

    @GET("freeQuery.php")
    Observable<String> getFreeQuery(@Query("query") String query);

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
    @POST("insertDrink.php")
    Observable<String> insertDrink(
            @Field("name") String name,
            @Field("cost") String cost,
            @Field("volume") String volume,
            @Field("cafeId") String cafeId
    );

    @FormUrlEncoded
    @POST("insertTobacco.php")
    Observable<String> insertTobacco(
            @Field("name") String name,
            @Field("cost") String cost,
            @Field("line") String line,
            @Field("cafeId") String cafeId
    );

    @FormUrlEncoded
    @POST("insertComment.php")
    Observable<String> insertComment(
            @Field("email") String email,
            @Field("content") String content,
            @Field("rate") String rate,
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
    @POST("updateDrink.php")
    Observable<String> updateDrink(
            @Field("name") String name,
            @Field("cost") String cost,
            @Field("volume") String volume,
            @Field("drinkId") String dishId

    );

    @FormUrlEncoded
    @POST("updateTobacco.php")
    Observable<String> updateTobacco(
            @Field("name") String name,
            @Field("cost") String cost,
            @Field("line") String line,
            @Field("tobaccoId") String tobaccoId

    );

    @FormUrlEncoded
    @POST("updateComment.php")
    Observable<String> updateComment(
            @Field("email") String email,
            @Field("content") String content,
            @Field("rate") String rate,
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
