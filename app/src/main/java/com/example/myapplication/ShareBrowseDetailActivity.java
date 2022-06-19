package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class ShareBrowseDetailActivity extends AppCompatActivity {

    private JSONObject main_floor_object;
    private JSONArray list_data = new JSONArray();

    private final int load_num = 10;
    private int sort_type = 0; // 0：最早；1：最近
    private int pid;
    private int like_state;
    private String title;
    private boolean main_floor_init_flag; // 标记第0层数据是否已加载

    private Handler data_handler;

    int sy;

    @SuppressLint("ClickableViewAccessibility")
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
        pid = intent.getIntExtra("pid", -1);
        title = intent.getStringExtra("title");

        ImageView loading_icon = findViewById(R.id.share_detail_loading_icon);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.loading_anim);

        RecyclerView floor_list = findViewById(R.id.share_detail_floor_list);

        FloorListAdapter adapter = new FloorListAdapter(ShareBrowseDetailActivity.this,
                list_data, this);
        adapter.setPid(pid);
        floor_list.setAdapter(adapter);
        floor_list.setLayoutManager(new LinearLayoutManager(ShareBrowseDetailActivity.this));

        LinearLayout floor_sort_layout = findViewById(R.id.floor_sort_layout);
        TextView sort_text = findViewById(R.id.floor_sort_text);
        floor_sort_layout.setOnClickListener(view -> {
            sort_type = 1-sort_type;
            SystemService.clearJsonArray(list_data);
            adapter.notifyDataSetChanged();
            loading_icon.setAnimation(rotate);
            loadData();
            sort_text.setText(sort_type == 0?R.string.sort_earliest:
                        R.string.sort_latest);
            floor_list.scrollToPosition(0);
        });


        NestedScrollView scrollView = findViewById(R.id.detail_layout);

        data_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    if(!main_floor_init_flag){
                        init_main_floor();
                        main_floor_init_flag = true;
                    }
                    adapter.notifyDataSetChanged();
                }
                loading_icon.setAnimation(null);
            }
        };

        floor_list.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!floor_list.canScrollVertically(-1) ||
                        !floor_list.canScrollVertically(1)){
                    int y = (int)motionEvent.getY();
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            sy = y;
                            break;
                        case MotionEvent.ACTION_UP:
                            int offset_y = y - sy;
                            // 顶部刷新
                            if(!floor_list.canScrollVertically(-1)
                                    &&offset_y>30){
                                loading_icon.setAnimation(rotate);
                                SystemService.clearJsonArray(list_data);
                                adapter.notifyDataSetChanged();
                                loadData();
                                Log.d("", "onScrollStateChanged: 1");
                            }

                            // 底部加载
                            if(!floor_list.canScrollVertically(1)
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

        loadData();
    }

    public void loadData(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int page_index = (list_data.length()+load_num-1)/load_num;
                    int user_id = SystemService.getUserId(ShareBrowseDetailActivity.this);

                    String result = HttpRequest.post("/API/get_page_floors",
                            String.format("page_size=%s&page_index=%s&order_type=%s&uid=%s&pid=%s",
                                    load_num, page_index, sort_type, user_id, pid),
                            "form");
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = jsonObject.getJSONArray("data");

                    if(page_index == 0){
                        // 首页额外处理
                        main_floor_object = array.getJSONObject(0);
                        for(int i=1;i<array.length();i++){
                            list_data.put(array.getJSONObject(i));
                        }
                    }
                    else{
                        for(int i=0;i<array.length();i++){
                            list_data.put(array.getJSONObject(i));
                        }
                    }
                    message.what = 0;
                }catch (JSONException e) {
                    e.printStackTrace();
                    message.what = -1;
                }
                data_handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private void init_main_floor(){
        ImageView profile_photo = findViewById(R.id.share_detail_photo);
        TextView nickName_textView = findViewById(R.id.share_detail_nickName);
        TextView title_textView = findViewById(R.id.share_detail_title);
        TextView content_textView = findViewById(R.id.shared_detail_content);

        TextView location_textView = findViewById(R.id.share_detail_location);
        TextView time_textView = findViewById(R.id.share_detail_time);
        TextView like_num_textView = findViewById(R.id.share_detail_like_num);
        ImageView like_icon = findViewById(R.id.share_detail_like_icon);
        LinearLayout like_layout = findViewById(R.id.share_detail_like_layout);
        ImageView comment_add_textView = findViewById(R.id.share_detail_comment_add_button);
        ImageView share_icon = findViewById(R.id.share_detail_share_icon);

        LinearLayout multimedia_layout =  findViewById(R.id.share_detail_multimedia_layout);

        int uid = SystemService.getUserId(this);

        Handler give_like_handle = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    like_state = 1;
                    like_icon.setImageResource(R.drawable.like_icon);
                    int like_num = Integer.valueOf(
                            like_num_textView.getText().toString())+1;
                    like_num_textView.setText(String.valueOf(like_num));
                }
            }
        };

        Handler cancel_like_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    like_state = 0;
                    like_icon.setImageResource(R.drawable.unlike_icon);
                    int like_num = Integer.valueOf(
                            like_num_textView.getText().toString())-1;
                    like_num_textView.setText(String.valueOf(like_num));
                }
            }
        };

        try{
            nickName_textView.setText(main_floor_object.getString("nickname"));
            title_textView.setText(title);
            String text = main_floor_object.getString("text");
            content_textView.setText(text);

            location_textView.setText(main_floor_object.getString("position"));
            Date date = new Date(main_floor_object.getLong("time")*1000);
            time_textView.setText(Consts.date_format.format(date));
            like_num_textView.setText(String.valueOf(main_floor_object
                    .getInt("agree_count")));
            like_state = main_floor_object.getInt("agree_state");

            String url = main_floor_object.getString("pic_url");

            int type = main_floor_object.getInt("type");
            int share_type = -1;
            if(type == 0){
                share_type = Consts.TYPE_TEXT;

            }
            else if(type == 1){
                share_type = Consts.TYPE_AUDIO;
            }
            else if(type == 2){
                share_type = Consts.TYPE_VIDEO;
            }

            JSONArray url_array = main_floor_object.getJSONArray("urls");
            Log.d("urls:", String.valueOf(url_array));
            View new_add_view = null;
            if(url_array.length()>0){
                for(int i=0;i<url_array.length();i++){
                    String multimedia_url = SystemService.getBaseUrl()+url_array.getString(i);
                    switch (share_type){
                        case Consts.TYPE_TEXT:
                            new_add_view = LayoutInflater.from(this).inflate(
                                    R.layout.image_add_item, null
                            );
                            ImageView new_add_imageView = new_add_view.findViewById(
                                    R.id.new_add_imageView);
                            SystemService.getImage(multimedia_url,
                                    new Handler(Looper.getMainLooper()){
                                        @Override
                                        public void handleMessage(@NonNull Message msg) {
                                            super.handleMessage(msg);
                                            Bitmap bitmap = msg.getData().getParcelable("image");
                                            new_add_imageView.setImageBitmap(bitmap);
                                        }
                                    });
                            new_add_view.findViewById(R.id.multimedia_delete_icon).setVisibility(
                                    View.INVISIBLE
                            );
                            break;
                        case Consts.TYPE_AUDIO:
                            new_add_view = LayoutInflater.from(this).inflate(
                                    R.layout.audio_add_item, null
                            );
                            VideoView new_add_audioView = new_add_view.findViewById(
                                    R.id.new_add_videoView);
                            new_add_audioView.setBackground(getResources().getDrawable(
                                    R.drawable.audio_background));
                            new_add_audioView.setMediaController(new MediaController(this));
                            SystemService.getAudio(this, multimedia_url,
                                    String.format("%s_%s.mp3", pid, i),
                                    new Handler(Looper.getMainLooper()){
                                        @Override
                                        public void handleMessage(@NonNull Message msg) {
                                            super.handleMessage(msg);
                                            new_add_audioView.setVideoURI(
                                                    msg.getData().getParcelable("uri"));
                                            new_add_audioView.start();
                                        }
                                    });
                            new_add_view.findViewById(R.id.multimedia_delete_icon).setVisibility(
                                    View.INVISIBLE
                            );
                            break;
                        case Consts.TYPE_VIDEO:
                            new_add_view = LayoutInflater.from(this).inflate(
                                    R.layout.video_add_item, null
                            );
                            VideoView new_add_videoView = new_add_view.findViewById(
                                    R.id.new_add_videoView);
                            new_add_videoView.setMediaController(new MediaController(this));
                            SystemService.getVideo(this, multimedia_url,
                                    String.format("%s_%s.mp4", pid, i),
                                    new Handler(Looper.getMainLooper()){
                                        @Override
                                        public void handleMessage(@NonNull Message msg) {
                                            super.handleMessage(msg);
                                            new_add_videoView.setVideoURI(
                                                    msg.getData().getParcelable("uri"));
                                        }
                                    });
                            new_add_view.findViewById(R.id.multimedia_delete_icon).setVisibility(
                                    View.INVISIBLE
                            );
                            break;
                    };
                    multimedia_layout.addView(new_add_view);
                }
            }

            if(!url.equals("null")){
                url = SystemService.getBaseUrl()+url;
                Handler img_handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        Bundle bundle = msg.getData();
                        profile_photo.setImageBitmap(bundle.getParcelable("image"));
                    }
                };
                SystemService.getImage(url, img_handler);
            }
            int user_id = main_floor_object.getInt("uid");
            // 不能通过点击头像进入自己的主页
            if(user_id != uid){
                profile_photo.setOnClickListener(view -> {
                    Intent intent = new Intent(this, UserInfoActivity.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                });
            }

            // 分享
            share_icon.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TITLE, title);
                intent.putExtra(Intent.EXTRA_TEXT, text);
                intent.setType("text/plain");
                startActivity(intent);
//                ShareCompat.IntentBuilder
//                        .from(this)
//                        .setType("text/plain")
//                        .setChooserTitle("分享")
//                        .setText(detail)
//                        .startChooser();
            });
            if(like_state == 1){
                like_icon.setImageResource(R.drawable.like_icon);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        like_layout.setOnClickListener(view -> {
            Handler handler;
            if(like_state == 0){
                handler = give_like_handle;
            }
            else{
                handler = cancel_like_handler;
            }
            ShareOperation.change_like_state(uid,
                    pid, 0, handler, like_state);
        });

        comment_add_textView.setOnClickListener(view -> {
            Intent intent = new Intent(this, CommentAddActivity.class);
            intent.putExtra("pid", pid);
            intent.putExtra("fid",0);
            startActivity(intent);
        });
    }
}