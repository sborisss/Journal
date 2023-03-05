package com.example.journal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetAllUsers {
    @GET("/getusers")
    public Call<List<UsersPOJO>> getPost(@Query("userId") String userId);
}
