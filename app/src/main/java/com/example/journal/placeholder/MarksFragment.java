package com.example.journal.placeholder;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journal.MarksPOJO;
import com.example.journal.NetworkService;
import com.example.journal.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarksFragment extends Fragment {
    FirebaseAuth mAuth;
    private List<View> allUsers;
    LinearLayout layout;
    TextView nousers;
    ProgressBar spinner;
    SwipeRefreshLayout refresh;
    String period = "now";
    int check = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        View view = getLayoutInflater().inflate(R.layout.fragment_marks, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marks, container, false);
        mAuth = FirebaseAuth.getInstance();
        layout = view.findViewById(R.id.linear);
        nousers = view.findViewById(R.id.nousers);
        nousers.setVisibility(View.INVISIBLE);
        spinner = view.findViewById(R.id.spinner);
        refresh = view.findViewById(R.id.swipeToRefresh);
        spinner.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mAuth.getCurrentUser() != null) {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    sendRequest(period);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.marks_drop, menu);
        MenuItem item = menu.findItem(R.id.marksDrop);
        Spinner dropdown = (Spinner) item.getActionView();
        if (period == "1quart" || period == "now") {
            String[] periods = {"Первая четверть", "Вторая четверть", "Третья четверть", "Четвертая четверть", "Первое полугодие", "Второе полугодие", "Годовая"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, periods);
            dropdown.setAdapter(adapter);
        } else if (period == "2quart") {
            String[] periods = {"Вторая четверть", "Первая четверть", "Третья четверть", "Четвертая четверть", "Первое полугодие", "Второе полугодие", "Годовая"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, periods);
            dropdown.setAdapter(adapter);
        } else if (period == "3quart") {
            String[] periods = {"Третья четверть", "Первая четверть", "Вторая четверть", "Четвертая четверть", "Первое полугодие", "Второе полугодие", "Годовая"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, periods);
            dropdown.setAdapter(adapter);
        } else if (period == "4quart") {
            String[] periods = {"Четвертая четверть", "Первая четверть", "Вторая четверть", "Третья четверть",  "Первое полугодие", "Второе полугодие", "Годовая"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, periods);
            dropdown.setAdapter(adapter);
        } else if (period == "1half") {
            String[] periods = { "Первое полугодие", "Первая четверть","Вторая четверть", "Третья четверть", "Четвертая четверть", "Второе полугодие", "Годовая"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, periods);
            dropdown.setAdapter(adapter);
        } else if (period == "2half") {
            String[] periods = { "Второе полугодие", "Первая четверть", "Вторая четверть", "Третья четверть", "Четвертая четверть", "Первое полугодие", "Годовая"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, periods);
            dropdown.setAdapter(adapter);
        } else if (period == "year") {
            String[] periods = { "Годовая", "Первая четверть", "Вторая четверть", "Третья четверть", "Четвертая четверть", "Первое полугодие", "Второе полугодие"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, periods);
            dropdown.setAdapter(adapter);
        }
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (mAuth.getCurrentUser() != null) {
                    if (item == "Первая четверть" && period != "1quart") {
                        period = "1quart";
                        spinner.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        sendRequest(period);
                    }
                    if (item == "Вторая четверть" && period != "2quart") {
                        period = "2quart";
                        spinner.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        sendRequest(period);
                    }
                    if (item == "Третья четверть" && period != "3quart") {
                        period = "3quart";
                        spinner.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        sendRequest(period);
                    }
                    if (item == "Четвертая четверть" && period != "4quart") {
                        period = "4quart";
                        spinner.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        sendRequest(period);
                    }
                    if (item == "Первое полугодие" && period != "1half") {
                        period = "1half";
                        spinner.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        sendRequest(period);
                    }
                    if (item == "Второе полугодие" && period != "2half") {
                        period = "2half";
                        spinner.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        sendRequest(period);
                    }
                    if (item == "Годовая" && period != "year") {
                        period = "year";
                        spinner.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        sendRequest(period);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void sendRequest(String period) {
        layout.removeAllViews();
        NetworkService.getInstance()
                .getMarks()
                .getPost(mAuth.getCurrentUser().getUid(), period)
                .enqueue(new Callback<List<MarksPOJO>>() {
                    @Override
                    public void onResponse(Call<List<MarksPOJO>> call, Response<List<MarksPOJO>> response) {
                        List<MarksPOJO> marks = response.body();
                        allUsers = new ArrayList<View>();
                        spinner.setVisibility(View.GONE);
                        refresh.setRefreshing(false);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        marks.forEach(obj -> {
                            if (obj.lessonname.equals("nousers")) {
                                nousers.setVisibility(View.VISIBLE);
                            } else {
                                final View view1 = getLayoutInflater().inflate(R.layout.marks_layout, null);

                                TextView lessonName = view1.findViewById(R.id.lessonname);
                                TextView lessonMarks = view1.findViewById(R.id.lessonmarks);

                                lessonName.setText(obj.lessonname);
                                lessonMarks.setText(obj.lessonmarks);
                                allUsers.add(view1);
                                layout.addView(view1);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<MarksPOJO>> call, Throwable t) {
                        spinner.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
    }
}