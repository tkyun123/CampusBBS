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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareBrowse extends Fragment{
    private List<Map<String, String>> list_data = new ArrayList<>();
    private final int load_num = 10;

    public ShareRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;
    public RelativeLayout sort_layout;
    public TextView sort_text;

    private final Map<Integer, Integer> sort_map = new HashMap<>();
    private int sort_type = 0; //默认按时间排序

    private loadData interface_data_load;
    public ShareBrowse(loadData data_load) {
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
        View view = inflater.inflate(R.layout.fragment_share_browse, container, false);

        recycler_adapter = new ShareRecyclerAdapter(getContext(), list_data, getActivity());
        recycler = view.findViewById(R.id.share_browse_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    if(sort_type == 0){
                        interface_data_load.loadDataSortByTime(list_data, load_num);
                    }
                    else{
                        interface_data_load.loadDataSortByWave(list_data, load_num);
                    }
                    recycler_adapter.notifyDataSetChanged();
                }
            }
        });

        sort_layout = view.findViewById(R.id.share_sort_layout);
        sort_text = view.findViewById(R.id.share_sort_text);
        sort_text.setText(sort_map.get(sort_type));
        sort_layout.setOnClickListener(view1->{
            sort_type = 1-sort_type;
            sort_text.setText(sort_map.get(sort_type));
            list_data.clear();
            if(sort_type == 0){ //按时间
                interface_data_load.loadDataSortByTime(list_data, load_num);
            }
            else{ //按时间
                interface_data_load.loadDataSortByWave(list_data, load_num);
            }
            recycler_adapter.notifyDataSetChanged();
            recycler.scrollToPosition(0);
        });

        return view;
    }

    public interface loadData {
        public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num);
        public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num);
    }
}