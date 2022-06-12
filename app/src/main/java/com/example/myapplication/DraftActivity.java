package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DraftActivity extends AppCompatActivity {

    public int totalDraft;
    public int validDraft = 0;
    public List<Map<String, String>> data = new ArrayList<>();

    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);

        Toolbar tool_bar = findViewById(R.id.draft_tool_bar);
        tool_bar.setTitle("草稿箱");
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            this.finish();
        });

        SharedPreferences sharedPreferences = DraftActivity.this.getSharedPreferences(
                "login", Context.MODE_PRIVATE
        );
        user_id = sharedPreferences.getInt("user_id", -1);

        File file= new File("/data/data/" + getPackageName() + "/shared_prefs","draft_"+ user_id +".xml");
        if(file.exists()) {
            //file.delete();
            loadData();
        }
        else {
            totalDraft = 0;
            validDraft = 0;
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.draft_fragment_container, new DraftBrowse(new DraftBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list) {
                        for(int i = 0; i < validDraft; i ++) {
                            data_list.add(data.get(i));
                        }
                    }
                })).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();
        getSupportFragmentManager().beginTransaction().replace(
                R.id.draft_fragment_container, new DraftBrowse(new DraftBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list) {
                        for(int i = 0; i < validDraft; i ++) {
                            data_list.add(data.get(i));
                        }
                    }
                })).commit();
    }

    private void loadData() {
        validDraft = 0;
        data.clear();
        SharedPreferences share = getSharedPreferences("draft_" + user_id, DraftActivity.this.MODE_PRIVATE);
        totalDraft = share.getInt("num",0);
        for(int i = 0; i < totalDraft; i ++) {
            boolean is_deleted = share.getBoolean("is_deleted" + (i + 1),false);
            String id = (i + 1) + "";
            String newTitle = share.getString("title" + (i + 1),"");
            String newContent = share.getString("content" + (i + 1),"");
            String newDate = share.getString("date" + (i + 1),"");
            String newLocation = share.getString("location" + (i + 1),"");
            int newType = share.getInt("type" + (i + 1),0);

            if(!is_deleted) {
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("title", newTitle);
                map.put("content", newContent);
                map.put("date", newDate);
                map.put("location", newLocation);
                map.put("type", newType + "");
                data.add(map);
                validDraft += 1;
            }
        }
    }
}
