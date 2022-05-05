package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

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

public class CommentAddActivity extends AppCompatActivity {


    private String location = null;
    private Handler location_handler;

    private ImageView loading_icon;
    private Animation rotate;;
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
            String content = comment_edit.getText().toString();
            if(content.equals("")){
                AlertDialog message = new AlertDialog.Builder(this)
                            .setMessage("内容不能为空").create();
                message.show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("content", content);
            if(location_switch.isChecked()){
                intent.putExtra("location", location);
            }
            else{
                intent.putExtra("location", "");
            }
            // ... transport data to backend
            finish();
        });
    }
}