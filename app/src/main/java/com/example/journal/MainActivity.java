package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.journal.placeholder.InfoFragment;
import com.example.journal.placeholder.JournalFragment;
import com.example.journal.placeholder.MarksFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView nav;
    JournalFragment journalFragment = new JournalFragment();
    MarksFragment marksFragment = new MarksFragment();
    InfoFragment infoFragment = new InfoFragment();

    Fragment active = journalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().setStatusBarColor(ContextCompat.getColor(this, androidx.cardview.R.color.cardview_shadow_start_color));
        FragmentManager fManager = getSupportFragmentManager();
        fManager.beginTransaction().add(R.id.container, infoFragment, "3").hide(infoFragment).commit();
        fManager.beginTransaction().add(R.id.container, marksFragment, "2").hide(marksFragment).commit();
        fManager.beginTransaction().add(R.id.container, journalFragment, "1").commit();

        getSupportActionBar().setTitle("Журнал");

        nav = findViewById(R.id.navigation_bar);

            nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.journal:
                            fManager.beginTransaction().hide(active).show(journalFragment).commit();
                            active = journalFragment;
                            getSupportActionBar().setTitle("Журнал");
                            return true;
                        case R.id.marks:
                            fManager.beginTransaction().hide(active).show(marksFragment).commit();
                            active = marksFragment;
                            getSupportActionBar().setTitle("Оценки");
                            return true;
                        case R.id.info:
                            fManager.beginTransaction().hide(active).show(infoFragment).commit();
                            active = infoFragment;
                            getSupportActionBar().setTitle("Мои пользователи");
                            return true;
                    }
                    return false;
                }
            });
    }
}