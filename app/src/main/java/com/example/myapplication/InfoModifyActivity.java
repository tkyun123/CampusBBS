package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.tabs.TabLayout;

public class InfoModifyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_modify);

        Toolbar tool_bar = findViewById(R.id.info_modify_tool_bar);
        tool_bar.setTitle(R.string.title_activity_info_modify);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            this.setResult(RESULT_CANCELED);
            this.finish();
        });

        TabLayout tabLayout = findViewById(R.id.info_modify_tab);
        tabLayout.addTab(tabLayout.newTab().setText("基本信息"));
        tabLayout.addTab(tabLayout.newTab().setText("修改密码"));

        Fragment basic_info_modify_fragment = new BasicInfoModify();
        Fragment info_modify_password_fragment = new PasswordModify();

        getSupportFragmentManager().beginTransaction().replace(
                R.id.info_modify_containerView, basic_info_modify_fragment).commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("", String.valueOf(tab.getPosition()));
                switch (tab.getPosition()){
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .remove(info_modify_password_fragment)
                                .add(R.id.info_modify_containerView, basic_info_modify_fragment)
                                .commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .remove(basic_info_modify_fragment)
                                .add(R.id.info_modify_containerView, info_modify_password_fragment)
                                .commit();
                        break;
                    default:break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
