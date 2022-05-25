package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MessageBrowse extends Fragment {
    private JSONArray list_data = new JSONArray();
    private final int load_num = 10;  // load_num = -1 表示加载全部数据

    public MessageRecyclerAdapter recycler_adapter;
    public RecyclerView recycler;

    private TabLayout tabLayout;
    private int message_type = Consts.MESSAGE_LIKE_OR_COMMENT;

    Handler data_handler;
//    private MessageBrowse.loadData interface_data_load;
    ImageView loading_icon;
    Animation rotate;

    int sy;
    private boolean pullup = false; // 记录滑动状态，以便上滑更新
    public MessageBrowse() {
        // Required empty public constructor

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_browse, container, false);

        recycler_adapter = new MessageRecyclerAdapter(getContext(), list_data, getActivity());
        recycler = view.findViewById(R.id.message_recyclerView);
        recycler.setAdapter(recycler_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loading_icon = view.findViewById(R.id.message_loading_icon);
        rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        tabLayout = view.findViewById(R.id.message_browse_tab);
        init_tab();

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

        TextView message_clearAll_textView = view.findViewById(R.id.message_clearAll_textView);
        message_clearAll_textView.setOnClickListener(view1 -> {
            SystemService.clearJsonArray(list_data);
            recycler_adapter.notifyDataSetChanged();
        });

        data_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    recycler_adapter.notifyDataSetChanged();
                    loading_icon.setAnimation(null);
                }
            }
        };

        loading_icon.setAnimation(rotate);
        loadData();
        return view;
    }

    private void init_tab(){
        tabLayout.addTab(tabLayout.newTab().setText("点赞或评论"));
        tabLayout.addTab(tabLayout.newTab().setText("关注的人"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        message_type = Consts.MESSAGE_LIKE_OR_COMMENT;
                        break;
                    case 1:
                        message_type = Consts.MESSAGE_FOLLOW;
                        break;
                }
                SystemService.clearJsonArray(list_data);
                loadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    public void loadData(){
//        interface_data_load.loadData(list_data, load_num);
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int page_index = (list_data.length()+load_num-1)/load_num;
                    int user_id = SystemService.getUserId(getActivity());

                    String result = HttpRequest.post("/API",
                            "","form");
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = jsonObject.getJSONArray("data");
                    for(int i=0;i<array.length();i++){
                        list_data.put(array.getJSONObject(i));
                    }
                    message.what = 0;
                }catch (JSONException e){
                    e.printStackTrace();
                    message.what = -1;
                }
                data_handler.sendMessage(message);
            }
        };
        thread.start();
    }

//    public interface loadData {
//        public void loadData(JSONArray data_list, int load_num);
//    }

}
