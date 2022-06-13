package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myStarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        Toolbar tool_bar = findViewById(R.id.myStar_tool_bar);
        tool_bar.setTitle("我的收藏");
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            this.finish();
        });

        getSupportFragmentManager().beginTransaction().replace(
                R.id.myStar_fragment_container, new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        int a = 3;
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num, Handler handler, int sort_all) {

                    }
                }, true, true)).commit();
    }
}
