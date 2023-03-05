package com.example.journal;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<String, List<String>> lessonsCollection;
    private List<String> groupList;

    public MyExpandableListAdapter(Context context, List<String> groupList,
                                   Map<String, List<String>> lessonsCollection){
        this.context = context;
        this.lessonsCollection = lessonsCollection;
        this.groupList = groupList;
    }

    @Override
    public int getGroupCount() {
        return lessonsCollection.size();
    }

    @Override
    public int getChildrenCount(int i) {
        int p = 0;
        try {
            p = lessonsCollection.get(groupList.get(i)).size();
        } catch (NullPointerException e) {
            Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_LONG).show();
        }
        return p;
    }

    @Override
    public Object getGroup(int i) {
        return groupList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return lessonsCollection.get(groupList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String lessonName = getGroup(i).toString();
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_item, null);
        }
        TextView item = view.findViewById(R.id.mobile);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(lessonName);
        return view;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        String model = getChild(i, i1).toString();
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_item, null);
        }
        TextView item = view.findViewById(R.id.model);
        item.setText(model);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}