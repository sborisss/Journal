package com.example.journal;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.journal.placeholder.AddUserPOJO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.r0adkll.slidr.Slidr;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText registeremail, registerpassword, registerpasswordconfirm;
    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Регистрация");
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
        fStore = FirebaseFirestore.getInstance();
        registeremail = findViewById(R.id.registeremail);
        registerpassword = findViewById(R.id.registerpassword);
        registerpasswordconfirm = findViewById(R.id.registerpasswordconfirm);
        Slidr.attach(this);
        this.overridePendingTransition(R.anim.slidein, R.anim.slideout);
    }

    public void registerfunction(View view) {
        final String email = registeremail.getText().toString();
        final String password = registerpassword.getText().toString();

        if (!password.equals(registerpasswordconfirm.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Пароли не совпадают", Toast.LENGTH_LONG).show();
            Log.d(TAG, password);
            Log.d(TAG, registerpasswordconfirm.getText().toString());
        } else if (email.isEmpty() || password.isEmpty() || registerpasswordconfirm.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Заполните все поля", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Успешная регистрация", Toast.LENGTH_LONG).show();
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("user_profile").document(userId);
                        Map<String, Object> user = new HashMap<>();
                        user.put("Email", email);
                        user.put("Password", password);

                        documentReference.set(user);

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
                        Toast.makeText(getApplicationContext(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        super.finish();
        return true;
    }
}