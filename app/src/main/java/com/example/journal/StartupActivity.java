package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.journal.placeholder.AddUserPOJO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartupActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR); getSupportActionBar().hide();
        setContentView(R.layout.activity_startup);}

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            NetworkService.getInstance()
                    .startRequest()
                    .getPost(currentUser.getUid())
                    .enqueue(new Callback<AddUserPOJO>() {
                        @Override
                        public void onResponse(@NonNull Call<AddUserPOJO> call, @NonNull Response<AddUserPOJO> response) {
                            AddUserPOJO user = response.body();
                        }

                        @Override
                        public void onFailure(@NonNull Call<AddUserPOJO> call, @NonNull Throwable t) {
                            t.printStackTrace();
                        }
                    });
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    public void signupfunction(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
    public void signinfunction(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}