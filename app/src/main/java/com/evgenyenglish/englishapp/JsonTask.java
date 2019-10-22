package com.evgenyenglish.englishapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonTask {
    @GET("task.php?hash=78668b3c2d2f4ce4d39b6b175bd8a249465324fe")
    Call<List<Task>> getTask(@Query("number") int number);
}