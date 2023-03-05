package com.example.journal;

import java.util.List;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService mInstance;
    private static final String BASE_URL = "http://192.168.1.194:3000";
    private Retrofit mRetrofit;

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    public GetTasksRequest getTasks() {
        return mRetrofit.create(GetTasksRequest.class);
    }
    public GetMarksRequest getMarks() {
        return mRetrofit.create(GetMarksRequest.class);
    }
    public GetAllUsers getUsers() {
        return mRetrofit.create(GetAllUsers.class);
    }
    public GetUser getUser() { return mRetrofit.create(GetUser.class); }
    public CreateNewUser createNewUser() { return mRetrofit.create(CreateNewUser.class); }
    public AppStartRequest startRequest() { return mRetrofit.create(AppStartRequest.class); }
    public SelectUser selectUser() { return mRetrofit.create(SelectUser.class); }
    public DeleteUser deleteUser() { return mRetrofit.create(DeleteUser.class); }
    public CreateFireUser fireUser() { return mRetrofit.create(CreateFireUser.class); }
}