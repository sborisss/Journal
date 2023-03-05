package com.example.journal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetTasksRequest {
    @GET("/gettasks")
    public Call<List<List<Post>>> getPost(@Query("q") String status, @Query("useruId") String userId);
}
