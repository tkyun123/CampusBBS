package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class RelationUserActivity extends AppCompatActivity {

    // user_id对应一个用户
    private int user_id;

    // 0表示他关注的人;1表示他屏蔽的人;2表示关注他的人;3表示显示此人信息
    private int relation_type;
    private int my_user_id;  // 用户界面实际显示的关注、屏蔽关系是与当前使用者的关系

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation_user);

        Toolbar tool_bar = findViewById(R.id.relation_user_tool_bar);

        Intent intent = getIntent();
        relation_type = intent.getIntExtra("type", 0);
        user_id = intent.getIntExtra("user_id", 0);

        my_user_id = SystemService.getUserId(this);

        if(relation_type == Consts.RELATION_FOLLOW){
            tool_bar.setTitle("关注的人");
        }
        else if(relation_type == Consts.RELATION_BLOCK){
            tool_bar.setTitle("屏蔽的人");
        }
        else if(relation_type == Consts.RELATION_FOLLOWED){
            tool_bar.setTitle("粉丝");
        }
        else{
            tool_bar.setTitle("用户");
        }
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);
        tool_bar.setNavigationOnClickListener(view-> this.finish());

        getSupportFragmentManager().beginTransaction().replace(
                R.id.relation_user_containerView, new UserBrowse((data_list, load_num, handler) -> {
                    loadUserDate(data_list, load_num, handler, relation_type);
                })
        );
    }

    private void loadUserDate(JSONArray data_list,
                              int load_num, Handler handler, int  relation_type){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int page_index = (data_list.length()+load_num-1)/load_num;

                    String url = "";
                    if(relation_type == Consts.RELATION_FOLLOW){
                        url = SystemService.getBaseUrl()+"/API/get_sub";
                    }
                    else if(relation_type == Consts.RELATION_BLOCK){
                        url = SystemService.getBaseUrl()+"/API/get_block";
                    }
                    else if(relation_type == Consts.RELATION_FOLLOWED){
                        url = SystemService.getBaseUrl()+"/API/get_";
                    }
                    else{
                        url = SystemService.getBaseUrl()+"/API";
                    }
                    String result = HttpRequest.post(url,
                            String.format("page_size=%s&page_index=%s&uid=%s",
                                    load_num, page_index,user_id),
                            null);
                    JSONObject jsonObject = new JSONObject(result);
//                    Log.d("", result);
                    JSONArray array = jsonObject.getJSONArray("data");
                    Log.d("array:", String.valueOf(array));
                    for(int i=0;i<array.length();i++){
                        data_list.put(array.getJSONObject(i));
                    }
                    message.what = 0;
                }catch (JSONException e) {
                    e.printStackTrace();
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}