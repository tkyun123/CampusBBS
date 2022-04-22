package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBrowse extends Fragment {

    private List<Map<String, String>> list_data = new ArrayList<>();
    private final int load_num = 10;


    public UserRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;

    private loadData interface_data_load;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public UserBrowse(loadData data_load) {
        // Required empty public constructor
        interface_data_load = data_load;
        interface_data_load.loadData(list_data, load_num);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_browse, container, false);

        recycler_adapter = new UserRecyclerAdapter(getContext(), list_data, getActivity());
        recycler = view.findViewById(R.id.user_browse_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    interface_data_load.loadData(list_data, load_num);
                    recycler_adapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    public interface loadData {
        public void loadData(List<Map<String, String>> data_list, int load_num);
    }

}