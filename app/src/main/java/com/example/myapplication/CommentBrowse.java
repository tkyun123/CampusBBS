package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentBrowse} factory method to
 * create an instance of this fragment.
 */
public class CommentBrowse extends Fragment {

    private JSONArray list_data = new JSONArray();
    private final int load_num = 10;

    public CommentRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;

    private final Map<Integer, Integer> sort_map = new HashMap<>();
    private int sort_type = 0; //最早 or 最近
    private Handler data_handler;

    private CommentBrowse.loadData interface_data_load;
    private int comment_type;

    int sy;
    public CommentBrowse(CommentBrowse.loadData data_load, int type) {
        // Required empty public constructor
        sort_map.put(0, R.string.sort_earliest);
        sort_map.put(1, R.string.sort_latest);

        interface_data_load = data_load;
        comment_type = type;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_browse, container, false);

        recycler_adapter = new CommentRecyclerAdapter(getContext(), list_data, getActivity(),
                                                      comment_type);

        recycler = view.findViewById(R.id.comment_browse_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView loading_icon = view.findViewById(R.id.comment_loading_icon);
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

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

        RelativeLayout comment_sort_layout = view.findViewById(R.id.comment_sort_layout);
        TextView sort_text = view.findViewById(R.id.sort_text);
        comment_sort_layout.setOnClickListener(view1 -> {
            sort_type = 1-sort_type;
            sort_text.setText(sort_map.get(sort_type));
            SystemService.clearJsonArray(list_data);
            loadData();
            recycler.scrollToPosition(0);
        });

        data_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    recycler_adapter.notifyDataSetChanged();
                }
                loading_icon.setAnimation(null);
            }
        };

        interface_data_load.loadData(list_data, load_num, sort_type, data_handler);
        return view;
    }

    public void loadData(){
        interface_data_load.loadData(list_data, load_num, sort_type, data_handler);
    }

    public interface loadData {
        public void loadData(JSONArray data_list, int load_num, int sort_type,
                             Handler handler);
    }

}