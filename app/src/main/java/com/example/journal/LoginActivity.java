package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.journal.placeholder.AddUserPOJO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.r0adkll.slidr.Slidr;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email, password;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Авторизация");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Unable to get token", Toast.LENGTH_LONG).show();
                    return;
                }
                token = task.getResult();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.loginemail);
        password = findViewById(R.id.loginpassword);
        Slidr.attach(this);
        this.overridePendingTransition(R.anim.slidein, R.anim.slideout);
    }

    public void loginfunction(View view) {
        final String userMail = email.getText().toString();
        final String userPassword = password.getText().toString();
        if (userMail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Заполните все поля", Toast.LENGTH_LONG).show();
        } else {
            signIn(userMail, userPassword);
        }
    }

    private void signIn(String userMail, String userPassword) {
        mAuth.signInWithEmailAndPassword(userMail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    NetworkService.getInstance()
                            .fireUser()
                            .getPost(mAuth.getCurrentUser().getUid(), token)
                            .enqueue(new Callback<AddUserPOJO>() {
                                @Override
                                public void onResponse(Call<AddUserPOJO> call, Response<AddUserPOJO> response) {
                                    AddUserPOJO user = response.body();
                                }

                                @Override
                                public void onFailure(Call<AddUserPOJO> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item){
        super.finish();
        return true;
    }
}