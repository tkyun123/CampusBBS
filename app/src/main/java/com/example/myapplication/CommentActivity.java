package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar tool_bar = findViewById(R.id.comment_tool_bar);
        tool_bar.setTitle(R.string.title_activity_comment);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            this.finish();
        });

//        Intent intent = getIntent();

        getSupportFragmentManager().beginTransaction().replace(
                R.id.comment_fragment_container, new CommentBrowse(new CommentBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("nickName","欧米牛坦");
                            data.put("content","评论(按时间)");
                            data_list.add(data);
                            load_num--;
                        }
                    }

                    @Override
                    public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("nickName","欧米牛坦");
                            data.put("content","评论(按热度)");
                            data_list.add(data);
                            load_num--;
                        }
                    }
                })).commit();
    }
}