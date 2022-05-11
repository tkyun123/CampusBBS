package com.example.myapplication;

import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_share);

        Toolbar tool_bar = findViewById(R.id.user_share_tool_bar);
        tool_bar.setTitle(R.string.title_activity_user_share);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            this.finish();
        });

//        Intent intent = getIntent();

        getSupportFragmentManager().beginTransaction().replace(
                R.id.user_share, new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {
//                        while(load_num>0){
//                            Map<String, String> data = new HashMap<>();
//                            data.put("title","动态"+(data_list.length()+1));
//                            data.put("content","未关注");
//                            data_list.put(data);
//                            load_num--;
//                        }
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {
//                        while(load_num>0){
//                            Map<String, String> data = new HashMap<>();
//                            data.put("title","动态"+(data_list.size()+1));
//                            data.put("content","关注");
//                            data_list.add(data);
//                            load_num--;
//                        }
                    }
                }, false)).commit();
    }
}