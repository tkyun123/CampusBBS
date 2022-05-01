package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MessageBrowse extends Fragment {
    private List<Map<String, String>> list_data = new ArrayList<>();
    private final int load_num = -1;  // load_num = -1 表示加载全部数据


    public MessageRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;

    private MessageBrowse.loadData interface_data_load;

    private boolean pullup = false; // 记录滑动状态，以便上滑更新
    public MessageBrowse(MessageBrowse.loadData data_load) {
        // Required empty public constructor

        interface_data_load = data_load;
        interface_data_load.loadData(list_data, load_num);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_browse, container, false);

        recycler_adapter = new MessageRecyclerAdapter(getContext(), list_data, getActivity());
        recycler = view.findViewById(R.id.message_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView loading_icon = view.findViewById(R.id.message_loading_icon);
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 下拉加载，这里选择一次加载完
//                if(!recyclerView.canScrollVertically(1)){
//                    loadData();
//                    recycler_adapter.notifyDataSetChanged();
//                }
                // 上拉刷新
                if(recyclerView.canScrollVertically(-1)){
                    pullup = false;
                }
                if(!recyclerView.canScrollVertically(-1)){
                    if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                        pullup = true;
                    }
                    if(newState  == RecyclerView.SCROLL_STATE_SETTLING
                        && pullup){
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
                        recycler_adapter.notifyDataSetChanged();
                        pullup = false;
                    }
                }
            }
        });

        TextView message_clearAll_textView = view.findViewById(R.id.message_clearAll_textView);
        message_clearAll_textView.setOnClickListener(view1 -> {
            list_data.clear();
            recycler_adapter.notifyDataSetChanged();
        });
        return view;
    }

    public void loadData(){
        interface_data_load.loadData(list_data, load_num);
    }

    public interface loadData {
        public void loadData(List<Map<String, String>> data_list, int load_num);
    }
}
