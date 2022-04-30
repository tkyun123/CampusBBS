package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class ShareBrowseDetailActivity extends AppCompatActivity {
    private int comment_span_state = 0;  // 0:unspanned, 1:spanned

    private List<Map<String, String>> list_data = new ArrayList<>();
    private final int load_num = 10;
    private int sort_type = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_browse_detail);

        Toolbar tool_bar = findViewById(R.id.share_detail_tool_bar);
        tool_bar.setTitle(R.string.title_activity_share_browse_detail);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);
        tool_bar.setNavigationOnClickListener(view->{
            this.finish();
        });

        Intent intent = getIntent();

        TextView sharedPost_title = findViewById(R.id.sharedPost_title);
        sharedPost_title.setText(intent.getStringExtra("share_title"));

        TextView sharedPost_content = findViewById(R.id.sharedPost_content);
        sharedPost_content.setText(intent.getStringExtra("share_content"));

        RecyclerView floor_list = findViewById(R.id.share_detail_floor_list);

        loadDataSortByTime(list_data, load_num);
        FloorListAdapter adapter = new FloorListAdapter(ShareBrowseDetailActivity.this,
                list_data, this);
        floor_list.setAdapter(adapter);
        floor_list.setLayoutManager(new LinearLayoutManager(ShareBrowseDetailActivity.this));

        RelativeLayout floor_sort_layout = findViewById(R.id.floor_sort_layout);
        TextView sort_text = findViewById(R.id.floor_sort_text);
        floor_sort_layout.setOnClickListener(view -> {
            sort_type = 1-sort_type;
            list_data.clear();
            loadData();
            sort_text.setText(sort_type == 0?R.string.sort_by_time_text:
                        R.string.sort_by_wave_text);
            adapter.notifyDataSetChanged();
            floor_list.scrollToPosition(0);
        });

        ImageView loading_icon = findViewById(R.id.share_detail_loading_icon);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.loading_anim);

        NestedScrollView scrollView = findViewById(R.id.detail_layout);
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener)
                (view, x, y, ox, oy) -> {
            // 下拉加载
            if(!scrollView.canScrollVertically(1)){
                loadData();
                adapter.notifyDataSetChanged();
            }
        });

        floor_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!scrollView.canScrollVertically(-1)   // 上拉刷新
                        && newState == RecyclerView.SCROLL_STATE_SETTLING){
                    loading_icon.setAnimation(rotate);
                    list_data.clear();
                    loadData();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            loading_icon.setAnimation(null);
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 500);
//                    loading_icon.setAnimation(null);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void loadData(){
        if(sort_type == 0){
            loadDataSortByTime(list_data, load_num);
        }
        else{
            loadDataSortByWave(list_data, load_num);
        }
    }

    public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num){
        while(load_num>0){
            Map<String, String> data = new HashMap<>();
            data.put("nickName","欧米牛坦");
            data.put("content","内容(按时间)");
            data_list.add(data);
            load_num--;
        }
    }

    public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num){
        while(load_num>0){
            Map<String, String> data = new HashMap<>();
            data.put("nickName","欧米牛坦");
            data.put("content","内容(按热度)");
            data_list.add(data);
            load_num--;
        }
    }
}