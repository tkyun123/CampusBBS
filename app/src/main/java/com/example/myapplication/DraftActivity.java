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
    public List<Map<String, String>> data = new ArrayList<>();

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
        int user_id = sharedPreferences.getInt("user_id", -1);

        File file= new File("/data/data/"+getPackageName().toString()+"/shared_prefs","draft_"+ user_id +".xml");
        if(file.exists()) {
            //file.delete();
            SharedPreferences share = getSharedPreferences("draft_" + user_id, DraftActivity.this.MODE_PRIVATE);
            totalDraft = share.getInt("num",0);
            for(int i = 0; i < totalDraft; i ++) {
                Map<String, String> map = new HashMap<>();
                map.put("id", (i + 1) + "");
                map.put("title", share.getString("title" + (i + 1),""));
                map.put("content", share.getString("content" + (i + 1),""));
                data.add(map);
            }
        }
        else {
            SharedPreferences share = getSharedPreferences("draft_" + user_id, DraftActivity.this.MODE_PRIVATE);
            SharedPreferences.Editor editor = share.edit();
            editor.putInt("num", 0);
            editor.commit();
            totalDraft = 0;
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.draft_fragment_container, new DraftBrowse(new DraftBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list) {
                        for(int i = 0; i < totalDraft; i ++) {
                            data_list.add(data.get(i));
                        }
                    }
                })).commit();
    }
}
