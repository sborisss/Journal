package com.example.journal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetMarksRequest {
    @GET("/getmarks")
    public Call<List<MarksPOJO>> getPost(@Query("q") String status, @Query("period") String period);
}
