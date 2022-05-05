package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DraftActivity extends AppCompatActivity {

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

        getSupportFragmentManager().beginTransaction().replace(
                R.id.draft_fragment_container, new DraftBrowse(new DraftBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","题目");
                            data.put("content","内容");
                            data_list.add(data);
                            load_num--;
                        }
                    }
                })).commit();
    }
}
