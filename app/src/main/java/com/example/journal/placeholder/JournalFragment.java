package com.example.journal.placeholder;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.journal.MyExpandableListAdapter;
import com.example.journal.NetworkService;
import com.example.journal.Post;
import com.example.journal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalFragment extends Fragment {
    Calendar cal;
    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> lessonsCollection;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    SwipeRefreshLayout refresh;
    ProgressBar spinner;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        spinner = view.findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        refresh = view.findViewById(R.id.swipeToRefresh);
        mAuth = FirebaseAuth.getInstance();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        createGroupList();
        createCollection();
        expandableListView = view.findViewById(R.id.listView);
        expandableListAdapter = new MyExpandableListAdapter(getActivity(), groupList, lessonsCollection);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;
            @Override
            public void onGroupExpand(int i) {
                if(lastExpandedPosition != -1 && i != lastExpandedPosition){
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = i;
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                createGroupList();
                createCollection();
                expandableListView = view.findViewById(R.id.listView);
                expandableListAdapter = new MyExpandableListAdapter(getActivity(), groupList, lessonsCollection);
                expandableListView.setAdapter(expandableListAdapter);
                expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int lastExpandedPosition = -1;
                    @Override
                    public void onGroupExpand(int i) {
                        if(lastExpandedPosition != -1 && i != lastExpandedPosition){
                            expandableListView.collapseGroup(lastExpandedPosition);
                        }
                        lastExpandedPosition = i;
                    }
                });
                refresh.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.journalbuttons, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.previous){
            spinner.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            createGroupList();
            createCollection1();
            if (lessonsCollection.size() != 0 && groupList.size() != 0) {
                expandableListAdapter = new MyExpandableListAdapter(getActivity(), groupList, lessonsCollection);
                expandableListView.setAdapter(expandableListAdapter);
                expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int lastExpandedPosition = -1;

                    @Override
                    public void onGroupExpand(int i) {
                        if (lastExpandedPosition != -1 && i != lastExpandedPosition) {
                            expandableListView.collapseGroup(lastExpandedPosition);
                        }
                        lastExpandedPosition = i;
                    }
                });
            }
            return true;
        }
        if(id == R.id.next){
            spinner.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            createGroupList();
            createCollection2();
            expandableListAdapter = new MyExpandableListAdapter(getActivity(), groupList, lessonsCollection);
            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int lastExpandedPosition = -1;
                @Override
                public void onGroupExpand(int i) {
                    if(lastExpandedPosition != -1 && i != lastExpandedPosition){
                        expandableListView.collapseGroup(lastExpandedPosition);
                    }
                    lastExpandedPosition = i;
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createCollection1() {
        ArrayList<String> monday1 = new ArrayList<String>();
        ArrayList<String> tuesday1 = new ArrayList<String>();
        ArrayList<String> wednesday1 = new ArrayList<String>();
        ArrayList<String> thursday1 = new ArrayList<String>();
        ArrayList<String> friday1 = new ArrayList<String>();
        ArrayList<String> saturday1 = new ArrayList<String>();
        ArrayList<String> sunday1 = new ArrayList<String>();
        lessonsCollection = new HashMap<String, List<String>>();
        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
        cal.add(Calendar.DATE, -7);

        try {
            if (mAuth.getCurrentUser() != null) {
                NetworkService.getInstance()
                        .getTasks()
                        .getPost(format1.format(cal.getTime()), mAuth.getCurrentUser().getUid())
                        .enqueue(new Callback<List<List<Post>>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<List<Post>>> call, @NonNull Response<List<List<Post>>> response) {
                                List<List<Post>> days = response.body();
                                spinner.setVisibility(View.GONE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                final int[] p = {0};

                                days.forEach(obj -> {
                                    if (p[0] == 0) {
                                        obj.forEach(obj1 -> {
                                            monday1.add(obj1.lesson);
                                        });
                                    } else if (p[0] == 1) {
                                        obj.forEach(obj1 -> {
                                            tuesday1.add(obj1.lesson);
                                        });
                                    } else if (p[0] == 2) {
                                        obj.forEach(obj1 -> {
                                            wednesday1.add(obj1.lesson);
                                        });
                                    } else if (p[0] == 3) {
                                        obj.forEach(obj1 -> {
                                            thursday1.add(obj1.lesson);
                                        });
                                    } else if (p[0] == 4) {
                                        obj.forEach(obj1 -> {
                                            friday1.add(obj1.lesson);
                                        });
                                    } else if (p[0] == 5) {
                                        obj.forEach(obj1 -> {
                                            saturday1.add(obj1.lesson);
                                        });
                                    } else if (p[0] == 6) {
                                        obj.forEach(obj1 -> {
                                            sunday1.add(obj1.lesson);
                                        });
                                    }
                                    p[0]++;
                                });


                                String[] monday = new String[monday1.size()];
                                String[] tuesday = new String[tuesday1.size()];
                                String[] wednesday = new String[wednesday1.size()];
                                String[] thursday = new String[thursday1.size()];
                                String[] friday = new String[friday1.size()];
                                String[] saturday = new String[saturday1.size()];
                                String[] sunday = new String[sunday1.size()];

                                for (int i = 0; i < monday1.size(); i++) {
                                    monday[i] = monday1.get(i);
                                }
                                for (int i = 0; i < tuesday1.size(); i++) {
                                    tuesday[i] = tuesday1.get(i);
                                }
                                for (int i = 0; i < wednesday1.size(); i++) {
                                    wednesday[i] = wednesday1.get(i);
                                }
                                for (int i = 0; i < thursday1.size(); i++) {
                                    thursday[i] = thursday1.get(i);
                                }
                                for (int i = 0; i < friday1.size(); i++) {
                                    friday[i] = friday1.get(i);
                                }
                                for (int i = 0; i < saturday1.size(); i++) {
                                    saturday[i] = saturday1.get(i);
                                }
                                for (int i = 0; i < sunday1.size(); i++) {
                                    sunday[i] = sunday1.get(i);
                                }

                                for (String group : groupList) {
                                    if (group.equals("Понедельник")) {
                                        loadChild(monday);
                                    } else if (group.equals("Вторник"))
                                        loadChild(tuesday);
                                    else if (group.equals("Среда"))
                                        loadChild(wednesday);
                                    else if (group.equals("Четверг"))
                                        loadChild(thursday);
                                    else if (group.equals("Пятница"))
                                        loadChild(friday);
                                    else if (group.equals("Суббота"))
                                        loadChild(saturday);
                                    else
                                        loadChild(sunday);
                                    lessonsCollection.put(group, childList);
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<List<List<Post>>> call, @NonNull Throwable t) {
                                spinner.setVisibility(View.GONE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                                t.printStackTrace();
                            }
                        });
            }
        } catch (Error e) {
            Toast toast = Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG);
            toast.show();
        }
        for (String group : groupList) {
            lessonsCollection.put(group, childList);
        }

    }

    private void createCollection2() {
        ArrayList<String> monday1 = new ArrayList<String>();
        ArrayList<String> tuesday1 = new ArrayList<String>();
        ArrayList<String> wednesday1 = new ArrayList<String>();
        ArrayList<String> thursday1 = new ArrayList<String>();
        ArrayList<String> friday1 = new ArrayList<String>();
        ArrayList<String> saturday1 = new ArrayList<String>();
        ArrayList<String> sunday1 = new ArrayList<String>();
        lessonsCollection = new HashMap<String, List<String>>();
        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
        cal.add(Calendar.DATE, +7);

        if (mAuth.getCurrentUser() != null) {
            NetworkService.getInstance()
                    .getTasks()
                    .getPost(format1.format(cal.getTime()), mAuth.getCurrentUser().getUid())
                    .enqueue(new Callback<List<List<Post>>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<List<Post>>> call, @NonNull Response<List<List<Post>>> response) {
                            List<List<Post>> days = response.body();
                            spinner.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            final int[] p = {0};

                            days.forEach(obj -> {
                                if (p[0] == 0) {
                                    obj.forEach(obj1 -> {
                                        monday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 1) {
                                    obj.forEach(obj1 -> {
                                        tuesday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 2) {
                                    obj.forEach(obj1 -> {
                                        wednesday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 3) {
                                    obj.forEach(obj1 -> {
                                        thursday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 4) {
                                    obj.forEach(obj1 -> {
                                        friday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 5) {
                                    obj.forEach(obj1 -> {
                                        saturday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 6) {
                                    obj.forEach(obj1 -> {
                                        sunday1.add(obj1.lesson);
                                    });
                                }
                                p[0]++;
                            });


                            String[] monday = new String[monday1.size()];
                            String[] tuesday = new String[tuesday1.size()];
                            String[] wednesday = new String[wednesday1.size()];
                            String[] thursday = new String[thursday1.size()];
                            String[] friday = new String[friday1.size()];
                            String[] saturday = new String[saturday1.size()];
                            String[] sunday = new String[sunday1.size()];

                            for (int i = 0; i < monday1.size(); i++) {
                                monday[i] = monday1.get(i);
                            }
                            for (int i = 0; i < tuesday1.size(); i++) {
                                tuesday[i] = tuesday1.get(i);
                            }
                            for (int i = 0; i < wednesday1.size(); i++) {
                                wednesday[i] = wednesday1.get(i);
                            }
                            for (int i = 0; i < thursday1.size(); i++) {
                                thursday[i] = thursday1.get(i);
                            }
                            for (int i = 0; i < friday1.size(); i++) {
                                friday[i] = friday1.get(i);
                            }
                            for (int i = 0; i < saturday1.size(); i++) {
                                saturday[i] = saturday1.get(i);
                            }
                            for (int i = 0; i < sunday1.size(); i++) {
                                sunday[i] = sunday1.get(i);
                            }

                            for (String group : groupList) {
                                if (group.equals("Понедельник")) {
                                    loadChild(monday);
                                } else if (group.equals("Вторник"))
                                    loadChild(tuesday);
                                else if (group.equals("Среда"))
                                    loadChild(wednesday);
                                else if (group.equals("Четверг"))
                                    loadChild(thursday);
                                else if (group.equals("Пятница"))
                                    loadChild(friday);
                                else if (group.equals("Суббота"))
                                    loadChild(saturday);
                                else
                                    loadChild(sunday);
                                lessonsCollection.put(group, childList);
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<List<List<Post>>> call, @NonNull Throwable t) {
                            spinner.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });
        }
        for (String group : groupList) {
            lessonsCollection.put(group, childList);
        }

    }

    private void createCollection() {
        ArrayList<String> monday1 = new ArrayList<String>();
        ArrayList<String> tuesday1 = new ArrayList<String>();
        ArrayList<String> wednesday1 = new ArrayList<String>();
        ArrayList<String> thursday1 = new ArrayList<String>();
        ArrayList<String> friday1 = new ArrayList<String>();
        ArrayList<String> saturday1 = new ArrayList<String>();
        ArrayList<String> sunday1 = new ArrayList<String>();
        lessonsCollection = new HashMap<String, List<String>>();
        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");

        if (mAuth.getCurrentUser() != null) {
            NetworkService.getInstance()
                    .getTasks()
                    .getPost(format1.format(cal.getTime()), mAuth.getCurrentUser().getUid())
                    .enqueue(new Callback<List<List<Post>>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<List<Post>>> call, @NonNull Response<List<List<Post>>> response) {
                            List<List<Post>> days = response.body();
                            spinner.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            final int[] p = {0};

                            days.forEach(obj -> {
                                if (p[0] == 0) {
                                    obj.forEach(obj1 -> {
                                        monday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 1) {
                                    obj.forEach(obj1 -> {
                                        tuesday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 2) {
                                    obj.forEach(obj1 -> {
                                        wednesday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 3) {
                                    obj.forEach(obj1 -> {
                                        thursday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 4) {
                                    obj.forEach(obj1 -> {
                                        friday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 5) {
                                    obj.forEach(obj1 -> {
                                        saturday1.add(obj1.lesson);
                                    });
                                } else if (p[0] == 6) {
                                    obj.forEach(obj1 -> {
                                        sunday1.add(obj1.lesson);
                                    });
                                }
                                p[0]++;
                            });


                            String[] monday = new String[monday1.size()];
                            String[] tuesday = new String[tuesday1.size()];
                            String[] wednesday = new String[wednesday1.size()];
                            String[] thursday = new String[thursday1.size()];
                            String[] friday = new String[friday1.size()];
                            String[] saturday = new String[saturday1.size()];
                            String[] sunday = new String[sunday1.size()];

                            for (int i = 0; i < monday1.size(); i++) {
                                monday[i] = monday1.get(i);
                            }
                            for (int i = 0; i < tuesday1.size(); i++) {
                                tuesday[i] = tuesday1.get(i);
                            }
                            for (int i = 0; i < wednesday1.size(); i++) {
                                wednesday[i] = wednesday1.get(i);
                            }
                            for (int i = 0; i < thursday1.size(); i++) {
                                thursday[i] = thursday1.get(i);
                            }
                            for (int i = 0; i < friday1.size(); i++) {
                                friday[i] = friday1.get(i);
                            }
                            for (int i = 0; i < saturday1.size(); i++) {
                                saturday[i] = saturday1.get(i);
                            }
                            for (int i = 0; i < sunday1.size(); i++) {
                                sunday[i] = sunday1.get(i);
                            }

                            for (String group : groupList) {
                                if (group.equals("Понедельник")) {
                                    loadChild(monday);
                                } else if (group.equals("Вторник"))
                                    loadChild(tuesday);
                                else if (group.equals("Среда"))
                                    loadChild(wednesday);
                                else if (group.equals("Четверг"))
                                    loadChild(thursday);
                                else if (group.equals("Пятница"))
                                    loadChild(friday);
                                else if (group.equals("Суббота"))
                                    loadChild(saturday);
                                else
                                    loadChild(sunday);
                                lessonsCollection.put(group, childList);
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<List<List<Post>>> call, @NonNull Throwable t) {
                            spinner.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });
        }
        for (String group : groupList) {
            lessonsCollection.put(group, childList);
        }

    }

    private void loadChild(String[] lessonModels) {
        childList = new ArrayList<>();
        for(String model : lessonModels){
            childList.add(model);
        }
    }

    private void createGroupList() {
        groupList = new ArrayList<>();
        groupList.add("Понедельник");
        groupList.add("Вторник");
        groupList.add("Среда");
        groupList.add("Четверг");
        groupList.add("Пятница");
        groupList.add("Суббота");
        groupList.add("Воскресенье");
    }
}