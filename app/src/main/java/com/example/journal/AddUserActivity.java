package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.journal.placeholder.AddUserPOJO;
import com.google.firebase.auth.FirebaseAuth;
import com.r0adkll.slidr.Slidr;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserActivity extends AppCompatActivity {
    EditText login, password;
    Button signin;
    FirebaseAuth mAuth;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        getSupportActionBar().setTitle("Добавить пользователя");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        signin = findViewById(R.id.signin);
        mAuth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.spinner);
        Slidr.attach(this);
        this.overridePendingTransition(R.anim.slidein, R.anim.slideout);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                final String username = login.getText().toString();
                final String password1 = password.getText().toString();

                if (username.isEmpty() || password1.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Заполните все поля", Toast.LENGTH_LONG).show();
                } else {
                    NetworkService.getInstance()
                            .createNewUser()
                            .getPost(mAuth.getCurrentUser().getUid(), username, password1)
                            .enqueue(new Callback<AddUserPOJO>() {
                                @Override
                                public void onResponse(@NonNull Call<AddUserPOJO> call, @NonNull Response<AddUserPOJO> response) {
                                    AddUserPOJO user = response.body();
                                    spinner.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    if (user.response.equals("OK")) {
                                        Toast.makeText(getApplicationContext(), "Пользователь добавлен", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else if (user.response.equals("userexists")){
                                        Toast.makeText(getApplicationContext(), "Вы уже добавляли данного пользователя", Toast.LENGTH_LONG).show();
                                    } else if (user.response.equals("Wronginf")){
                                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_LONG).show();
                                    } else if (user.response.equals("limit")) {
                                        Toast.makeText(getApplicationContext(), "Вы добавили максимальное количество пользователей(5)", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Произошла непредвиденная ошибка", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<AddUserPOJO> call, @NonNull Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Произошла непредвиденная ошибка", Toast.LENGTH_LONG).show();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    t.printStackTrace();
                                }
                            });
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        this.overridePendingTransition(R.anim.slideout, R.anim.slideout);
        super.finish();
        return true;
    }
}