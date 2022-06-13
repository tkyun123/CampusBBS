package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kotlinx.coroutines.Delay;

public class ShareBrowse extends Fragment{
    private JSONArray list_data = new JSONArray();
    private final int load_num = 10;

    public ShareRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;

    public LinearLayout sort_all_layout;
    public TextView sort_all_text;
    public LinearLayout sort_layout;
    public TextView sort_text;

    private final Map<Integer, Integer> sort_map = new HashMap<>();
    private int sort_all_type = Consts.SORT_ALL;  // 只查看已关注的人还是所有人
    private int sort_type = 0; // 默认按时间排序

    private boolean sort_all = false;
    private boolean sort_all2 = false;

    private loadData interface_data_load;

    Handler data_handler;

    int sy;

    public ShareBrowse(loadData data_load, boolean _sort_all, boolean _sort_all2) {
        // Required empty public constructor
        sort_map.put(0, R.string.sort_by_time_text);
        sort_map.put(1, R.string.sort_by_wave_text);
        interface_data_load = data_load;
        sort_all = _sort_all;   // 设置是否显示sort_all选择项；例如显示某个人的动态就不需要
                                // 显示，只需要时间/热度排序
        sort_all2 = _sort_all2;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_browse, container, false);

        recycler_adapter = new ShareRecyclerAdapter(getContext(), list_data, getActivity());
        recycler = view.findViewById(R.id.share_browse_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView loading_icon = view.findViewById(R.id.share_loading_icon);
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        recycler.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int)motionEvent.getY();
                if(!recycler.canScrollVertically(-1) ||
                   !recycler.canScrollVertically(1)){
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            sy = y;
                            break;
                        case MotionEvent.ACTION_UP:
                            int offset_y = y - sy;
                            // 顶部刷新
                            if(!recycler.canScrollVertically(-1)
                                    &&offset_y>30){
                                loading_icon.setAnimation(rotate);
                                loading_icon.setVisibility(View.VISIBLE);
                                SystemService.clearJsonArray(list_data);
                                recycler_adapter.notifyDataSetChanged();
                                loadData();
                                Log.d("", "onScrollStateChanged: 1");
                            }

                            // 底部加载
                            if(!recycler.canScrollVertically(1)
                                    &&offset_y<-30){
                                loading_icon.setAnimation(rotate);
                                loading_icon.setVisibility(View.VISIBLE);
                                recycler_adapter.notifyDataSetChanged();
                                loadData();
                                Log.d("", "onScrollStateChanged: 2");
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        if(sort_all){
            sort_all_layout = view.findViewById(R.id.share_sort_all_layout);
            sort_all_text = view.findViewById(R.id.share_sort_all_text);
            sort_all_layout.setOnClickListener(view1 -> {
                if(sort_all_type == Consts.SORT_FOLLOW){
                    sort_all_type = Consts.SORT_ALL;
                    sort_all_text.setText(R.string.sort_all_text);
                }
                else{
                    sort_all_type = Consts.SORT_FOLLOW;
                    sort_all_text.setText(R.string.sort_follow_text);
                }
                SystemService.clearJsonArray(list_data);
                recycler_adapter.notifyDataSetChanged();
                loading_icon.setAnimation(rotate);
                loading_icon.setVisibility(View.VISIBLE);
                loadData();
                recycler.scrollToPosition(0);
            });
            sort_all_layout.setVisibility(View.VISIBLE);
        }

        if(sort_all2) {
            sort_layout = view.findViewById(R.id.share_sort_layout);
            sort_text = view.findViewById(R.id.share_sort_text);
            sort_layout.setOnClickListener(view1->{
                sort_type = 1-sort_type;
                sort_text.setText(sort_map.get(sort_type));
                SystemService.clearJsonArray(list_data);
                recycler_adapter.notifyDataSetChanged();
                loadData();
                recycler.scrollToPosition(0);
            });
            sort_layout.setVisibility(View.VISIBLE);
        }

        data_handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    recycler_adapter.notifyDataSetChanged();
                }
                loading_icon.setAnimation(null);
                loading_icon.setVisibility(View.INVISIBLE);
            }
        };

        loading_icon.setAnimation(rotate);
        loading_icon.setVisibility(View.VISIBLE);
        loadData();
        return view;
    }

    public void loadData(){
        if(sort_type == 0){
            interface_data_load.loadDataSortByTime(list_data, load_num, data_handler,
                    sort_all_type);
        }
        else{
            interface_data_load.loadDataSortByWave(list_data, load_num, data_handler,
                    sort_all_type);
        }
    }

    public interface loadData {
        public void loadDataSortByTime(JSONArray data_list, int load_num,
                                       Handler handler, int sort_all);
        public void loadDataSortByWave(JSONArray data_list, int load_num,
                                       Handler handler, int sort_all);
    }
}