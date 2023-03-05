package com.example.journal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetUser {
    @GET("/getuser")
    public Call<UserPOJO> getPost(@Query("q") String status);
}
