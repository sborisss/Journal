package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journal.placeholder.AddUserPOJO;
import com.example.journal.placeholder.InfoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    TextView username, userclass, password;
    FirebaseAuth mAuth;
    RelativeLayout mainWindow;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setTitle("Пользователь");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        this.overridePendingTransition(R.anim.slidein, R.anim.slideout);

        username = findViewById(R.id.username);
        userclass = findViewById(R.id.userclass);
        password = findViewById(R.id.password);
        mainWindow = findViewById(R.id.userlayout);
        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String user = "0";
        Slidr.attach(this);

        Bundle info = this.getIntent().getExtras();
        if (info != null) {
            user = info.getString("info");
        }
        NetworkService.getInstance()
                .getUser()
                .getPost(user)
                .enqueue(new Callback<UserPOJO>() {
                    @Override
                    public void onResponse(@NonNull Call<UserPOJO> call, @NonNull Response<UserPOJO> response) {
                        UserPOJO user = response.body();
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        username.append(user.username);
                        userclass.append(user.userclass);
                        password.append(user.password);
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserPOJO> call, @NonNull Throwable t) {
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getApplicationContext(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }
    public boolean onOptionsItemSelected(MenuItem item){
        super.finish();
        return true;
    }

    public void deleteuser(View view) {
        spinner.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        NetworkService.getInstance()
                .deleteUser()
                .getPost(mAuth.getCurrentUser().getUid(), password.getText().toString())
                .enqueue(new Callback<AddUserPOJO>() {
                    @Override
                    public void onResponse(@NonNull Call<AddUserPOJO> call, @NonNull Response<AddUserPOJO> response) {
                        AddUserPOJO user = response.body();

                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<AddUserPOJO> call, @NonNull Throwable t) {
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getApplicationContext(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    public void selectuser(View view) {
        spinner.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        NetworkService.getInstance()
                .selectUser()
                .getPost(mAuth.getCurrentUser().getUid(), password.getText().toString())
                .enqueue(new Callback<AddUserPOJO>() {
                    @Override
                    public void onResponse(@NonNull Call<AddUserPOJO> call, @NonNull Response<AddUserPOJO> response) {
                        AddUserPOJO user = response.body();

                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<AddUserPOJO> call, @NonNull Throwable t) {
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getApplicationContext(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }
}