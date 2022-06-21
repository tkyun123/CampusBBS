package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;


public class CommentAddActivity extends AppCompatActivity {


    private String location = null;
    private String content = null;

    private Handler location_handler;
    private Handler comment_add_handler;

    private ImageView loading_icon;
    private Animation rotate;;

    private int pid;
    private int fid;
    private int user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_add);

        Toolbar tool_bar = findViewById(R.id.comment_add_tool_bar);
        tool_bar.setTitle(R.string.title_activity_comment_add);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);
        tool_bar.setNavigationOnClickListener(view->{
            this.finish();
        });

        Intent intent = getIntent();
        pid = intent.getIntExtra("pid", -1);
        fid = intent.getIntExtra("fid", -1);

        loading_icon = findViewById(R.id.comment_add_loading_icon);
        rotate = AnimationUtils.loadAnimation(this, R.anim.loading_anim);;

        SwitchCompat location_switch = findViewById(R.id.show_location_switch);
        TextView location_textView = findViewById(R.id.show_location_textView);
        location_switch.setOnClickListener(view1 -> {
            if (location_switch.isChecked()) {
                location_textView.setText(R.string.show_location_text);
                if(location == null){
                    location = SystemService.DEFAULT_LOCATION;
                    loading_icon.setAnimation(rotate);
                    SystemService.getLocation(this, location_handler);
                }
            } else {
                location_textView.setText(R.string.not_show_location_text);
            }
        });

        location_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    location = msg.getData().getString("location");
                }
                loading_icon.setAnimation(null);
            }
        };

        EditText comment_edit = findViewById(R.id.comment_edit_content);
        TextView comment_add_button = findViewById(R.id.comment_add_button);
        comment_add_button.setOnClickListener(view -> {
            content = comment_edit.getText().toString();
            if(content.equals("")){
                AlertDialog message = new AlertDialog.Builder(this)
                            .setMessage("内容不能为空").create();
                message.show();
                return;
            }
            String lo = "";
            if(location_switch.isChecked()){
                lo = location;
            }

            add_comment(lo);
            // ... transport data to backend
            loading_icon.setAnimation(rotate);
            finish();
        });

        comment_add_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    Toast.makeText(CommentAddActivity.this,
                            "发布成功", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }else{
                    Toast.makeText(CommentAddActivity.this,
                            "发布失败", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
    }

    private void add_comment(String lo){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                message.what = 0;
                int user_id = SystemService.getUserId(CommentAddActivity.this);

                String result;
                if(fid == 0){
                    result = HttpRequest.post("/API/new_floor",
                            String.format("uid=%s&pid=%s&text=%s&type=0&position=%s",
                                    user_id, pid, content, lo),"form");
                }
                else{
                    result = HttpRequest.post("/API/new_comment",
                            String.format("uid=%s&pid=%s&fid=%s&text=%s&position=%s",
                                    user_id, pid, fid, content, lo),"form");
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("state") == 1){
                        message.what = 0;
                    }
                    else{
                        message.what = -1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = -1;
                }
                comment_add_handler.sendMessage(message);
            }
        };
        thread.start();
    }

}