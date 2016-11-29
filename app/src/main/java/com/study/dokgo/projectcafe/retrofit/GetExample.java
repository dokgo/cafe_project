package com.study.dokgo.projectcafe.retrofit;

import java.util.ArrayList;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by dokgo on 26.11.16.
 */

public interface GetExample {
    @GET("test.php")
    Observable<ArrayList<ExampleData>> getExampleData();
}
