package com.example.journal;

import com.example.journal.placeholder.AddUserPOJO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CreateNewUser {
    @GET("/createuser")
    public Call<AddUserPOJO> getPost(@Query("userId") String userId, @Query("username") String username, @Query("password") String password);
}
