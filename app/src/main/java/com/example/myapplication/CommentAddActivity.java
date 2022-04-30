package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class CommentAddActivity extends AppCompatActivity {

    private String location = "";
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

        SwitchCompat location_switch = findViewById(R.id.show_location_switch);
        TextView location_textView = findViewById(R.id.show_location_textView);
        location_switch.setOnClickListener(view1 -> {
            if (location_switch.isChecked()) {
                location_textView.setText(R.string.show_location_text);
            } else {
                location_textView.setText(R.string.not_show_location_text);
            }
        });

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
                location = SystemService.getLocation(this);
            }
            intent.putExtra("location", location);
            // ... transport data to backend
            finish();
        });
    }
}