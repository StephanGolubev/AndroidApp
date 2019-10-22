package com.evgenyenglish.englishapp;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("lesson.php?hash=78668b3c2d2f4ce4d39b6b175bd8a249465324fe")
    Call<List<Lesson>> getPosts();
}