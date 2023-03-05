package com.example.journal.placeholder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journal.AddUserActivity;
import com.example.journal.NetworkService;
import com.example.journal.Post;
import com.example.journal.R;
import com.example.journal.StartupActivity;
import com.example.journal.UserActivity;
import com.example.journal.UsersPOJO;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoFragment extends Fragment {
    Button button;
    private List<View> allUsers;
    FirebaseAuth mAuth;
    ProgressBar spinner;
    SwipeRefreshLayout refresh;
    LinearLayout linear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.addperson, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_info, container, false);

        linear = viewMain.findViewById(R.id.linear);
        mAuth = FirebaseAuth.getInstance();
        spinner = viewMain.findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        refresh = viewMain.findViewById(R.id.swipeToRefresh);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        sendRequest();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                sendRequest();
            }
        });

        return viewMain;
    }

    public void sendRequest() {
        linear.removeAllViews();
        NetworkService.getInstance()
                .getUsers()
                .getPost(mAuth.getCurrentUser().getUid())
                .enqueue(new Callback<List<UsersPOJO>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<UsersPOJO>> call, @NonNull Response<List<UsersPOJO>> response) {
                        List<UsersPOJO> users = response.body();
                        allUsers = new ArrayList<View>();
                        spinner.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        refresh.setRefreshing(false);

                        users.forEach(obj -> {
                            final View view1 = getLayoutInflater().inflate(R.layout.custom_layout, null);
                            Button user = view1.findViewById(R.id.user);
                            user.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Bundle info = new Bundle();
                                        info.putString("info", obj.userId);
                                        Intent myIntent = new Intent(getActivity(), UserActivity.class);
                                        myIntent.putExtras(info);
                                        getActivity().startActivity(myIntent);
                                    } catch (IndexOutOfBoundsException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                            user.setText(obj.user);
                            allUsers.add(view1);
                            linear.addView(view1);
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<UsersPOJO>> call, @NonNull Throwable t) {
                        spinner.setVisibility(View.GONE);
                        refresh.setRefreshing(false);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.add_person){
            Intent myIntent = new Intent(getActivity(), AddUserActivity.class);
            getActivity().startActivity(myIntent);
            return true;
        }
        if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(InfoFragment.this.getActivity(), StartupActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}