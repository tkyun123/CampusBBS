package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;


public class LoginActivity extends AppCompatActivity {

    private String checkCode_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);
        Toolbar tool_bar = findViewById(R.id.login_tool_bar);
        tool_bar.setTitle(R.string.title_activity_login);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            this.setResult(RESULT_CANCELED);
            this.finish();
        });

        TextView login_to_register_button = findViewById(R.id.login_to_register_button);
        login_to_register_button.setOnClickListener(view -> {
            this.startActivity(new Intent(this, RegisterActivity.class));
        });

        Button login_confirm_button = findViewById(R.id.login_confirm_button);
        login_confirm_button.setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra("nickName","欧米牛坦");
            this.setResult(RESULT_OK, data);
            this.finish();
        });

//        ImageView checkCode_imageView = findViewById(R.id.checkCode_imageView);
//        checkCode_imageView.setOnClickListener(view -> {
//
//        });
    }
}