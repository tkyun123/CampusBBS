package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DraftBrowse extends Fragment {
    private List<Map<String, String>> list_data = new ArrayList<>();
    private final int load_num = 10;


    public DraftRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;

    private DraftBrowse.loadData interface_data_load;

    public DraftBrowse(DraftBrowse.loadData data_load) {
        interface_data_load = data_load;
        interface_data_load.loadDataSortByTime(list_data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_draft_browse, container, false);

        recycler_adapter = new DraftRecyclerAdapter(getContext(), list_data, getActivity());
        recycler = view.findViewById(R.id.draft_browse_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView loading_icon = view.findViewById(R.id.draft_loading_icon);
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
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

        return view;
    }

    public void loadData(){
        interface_data_load.loadDataSortByTime(list_data);
    }

    public interface loadData {
        public void loadDataSortByTime(List<Map<String, String>> data_list);
    }
}
