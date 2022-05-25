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

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBrowse extends Fragment {

    private final JSONArray list_data = new JSONArray();
    private final int load_num = 10;


    public UserRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;

    private loadData interface_data_load;
    Handler handler;

    int sy;
    public UserBrowse(loadData data_load) {
        // Required empty public constructor
        interface_data_load = data_load;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_browse, container, false);

        recycler_adapter = new UserRecyclerAdapter(getContext(), list_data, getActivity());
        recycler = view.findViewById(R.id.user_browse_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView loading_icon = view.findViewById(R.id.user_loading_icon);
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    recycler_adapter.notifyDataSetChanged();
                }
                loading_icon.setAnimation(null);
            }
        };

        recycler.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!recycler.canScrollVertically(-1) ||
                        !recycler.canScrollVertically(1)){
                    int y = (int)motionEvent.getY();
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
                                SystemService.clearJsonArray(list_data);
                                loadData();
                                Log.d("", "onScrollStateChanged: 1");
                            }

                            // 底部加载
                            if(!recycler.canScrollVertically(1)
                                    &&offset_y<-30){
                                loading_icon.setAnimation(rotate);
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

        loading_icon.setAnimation(rotate);
        interface_data_load.loadData(list_data, load_num, handler);

        return view;
    }

    private void loadData(){
        interface_data_load.loadData(list_data, load_num, handler);
    }

    public interface loadData {
        public void loadData(JSONArray data_list, int load_num, Handler handler);
    }
}