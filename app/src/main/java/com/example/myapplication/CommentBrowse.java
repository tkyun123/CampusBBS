package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentBrowse} factory method to
 * create an instance of this fragment.
 */
public class CommentBrowse extends Fragment {

    private List<Map<String, String>> list_data = new ArrayList<>();
    private final int load_num = 10;


    public CommentRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;

    private final Map<Integer, Integer> sort_map = new HashMap<>();
    private int sort_type = 0; //默认按时间排序

    private CommentBrowse.loadData interface_data_load;

    public CommentBrowse(CommentBrowse.loadData data_load) {
        // Required empty public constructor
        sort_map.put(0, R.string.sort_by_time_text);
        sort_map.put(1, R.string.sort_by_wave_text);

        interface_data_load = data_load;
        interface_data_load.loadDataSortByTime(list_data, load_num);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_browse, container, false);

        recycler_adapter = new CommentRecyclerAdapter(getContext(), list_data, getActivity());
        recycler = view.findViewById(R.id.comment_browse_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView loading_icon = view.findViewById(R.id.comment_loading_icon);
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 下拉加载
                if(!recyclerView.canScrollVertically(1)){
                    loadData();
                    recycler_adapter.notifyDataSetChanged();
                }
                // 上拉刷新
                if(!recyclerView.canScrollVertically(-1)
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
                    recycler_adapter.notifyDataSetChanged();
                }
            }
        });

        RelativeLayout comment_sort_layout = view.findViewById(R.id.comment_sort_layout);
        TextView sort_text = view.findViewById(R.id.sort_text);
        comment_sort_layout.setOnClickListener(view1 -> {
            sort_type = 1-sort_type;
            sort_text.setText(sort_map.get(sort_type));
            list_data.clear();
            loadData();
            recycler_adapter.notifyDataSetChanged();
            recycler.scrollToPosition(0);
        });

        return view;
    }

    public void loadData(){
        if(sort_type == 0){
            interface_data_load.loadDataSortByTime(list_data, load_num);
        }
        else{
            interface_data_load.loadDataSortByWave(list_data, load_num);
        }
    }

    public interface loadData {
        public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num);
        public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num);
    }

}