package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserShareActivity extends AppCompatActivity {

    private int user_id;
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

        Intent intent = getIntent();
        user_id = intent.getIntExtra("user_id", 0);

        getSupportFragmentManager().beginTransaction().replace(
                R.id.user_share, new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {
                        loadData(data_list, load_num, handler, Consts.SORT_BY_TIME);
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {
                        loadData(data_list, load_num, handler, Consts.SORT_BY_WAVE);
                    }
                }, false, true)).commit();
    }

    private void loadData(JSONArray data_list, int load_num, Handler handler, int sort_type){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int index = (data_list.length()+load_num-1)/load_num;

                    String result = HttpRequest.post("/API/get_page_user_posts",
                            String.format("uid=%s&page_index=%s&page_size=%s&type=%s",
                                    user_id, index, load_num, sort_type),
                            "form");

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = jsonObject.getJSONArray("data");
                    for(int i=0;i<array.length();i++){
                        data_list.put(array.getJSONObject(i));
                    }
                    message.what = 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}