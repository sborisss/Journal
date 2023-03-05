package com.example.journal;

import com.example.journal.placeholder.AddUserPOJO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CreateFireUser {
    @GET("/createfireuser")
    public Call<AddUserPOJO> getPost(@Query("userId") String status, @Query("deviceId") String device);
}
