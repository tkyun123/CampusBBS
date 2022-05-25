package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private int pid;
    private int fid;
    private int uid;
    private int comment_type; // 0:楼层的评论 1:自己的评论
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

        Intent intent = getIntent();
        comment_type = intent.getIntExtra("comment_type", -1);
        if(comment_type == 0){
            pid = intent.getIntExtra("pid", -1);
            fid = intent.getIntExtra("fid", -1);
        }
        uid = SystemService.getUserId(this);

        CommentBrowse commentBrowse = new CommentBrowse(
                this::loadData, comment_type);

        getSupportFragmentManager().beginTransaction().replace(
                R.id.comment_fragment_container, commentBrowse).commit();
    }

    private void loadData(JSONArray data_list, int load_num, int sort_type,
                          Handler handler){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int page_index = (data_list.length()+load_num-1)/load_num;
                    String url = "/API/get_page_comments";
                    String result = HttpRequest.post(url,
                            String.format("uid=%s&pid=%s&fid=%s&page_index=%s&page_size=%s",
                                    uid, pid,fid, page_index, load_num),
                            "form");
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = jsonObject.getJSONArray("data");
                    for(int i=0;i<array.length();i++){
                        data_list.put(array.getJSONObject(i));
                    }
                    message.what = 0;
                }catch (JSONException e){
                    e.printStackTrace();
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();

    }
}