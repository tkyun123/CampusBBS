package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;

public class UserInfoActivity extends AppCompatActivity {

    private int user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Toolbar tool_bar = findViewById(R.id.user_info_tool_bar);
        tool_bar.setTitle(R.string.title_activity_user_info);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);
        tool_bar.setNavigationOnClickListener(view -> {
            this.finish();
        });

        Intent intent = getIntent();
        user_id = intent.getIntExtra("user_id", -1);

        getSupportFragmentManager().beginTransaction().replace(
                R.id.user_info_containerView,
                new UserInfo(Consts.FLAG_OTHER, user_id)).commit();

    }
}