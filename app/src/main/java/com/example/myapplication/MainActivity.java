package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public ViewPager pager;
    public PagerAdapter adapter;
    public BottomNavigationView navigator;
    @SuppressLint({"NonConstantResourceId", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tool_bar = findViewById(R.id.main_tool_bar);
        tool_bar.setTitle(R.string.app_name);
        setSupportActionBar(tool_bar);

        navigator = findViewById(R.id.main_navigator);
        pager = findViewById(R.id.main_pager);
        adapter = new PagerAdapter(getSupportFragmentManager(),navigator.getMaxItemCount(), this);
        pager.setAdapter(adapter);


        Map<Integer, Integer> map = new HashMap<>();
        map.put(R.id.tab_browse, 0);
        map.put(R.id.tab_message, 1);
        map.put(R.id.tab_post, 2);
        map.put(R.id.tab_search,3);
        map.put(R.id.tab_personInfo,4);

        navigator.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.tab_post && !SystemService.checkLogin(this)){
                startActivity(new Intent(this, LoginActivity.class));
                return false;
            }
            else{
                pager.setCurrentItem(map.get(item.getItemId()));
                return true;
            }
        });

        BottomNavigationMenuView menu = (BottomNavigationMenuView)navigator.getChildAt(0);
        BottomNavigationItemView item= (BottomNavigationItemView)menu.getChildAt(
                map.get(R.id.tab_post));
        Resources resources = getResources();
        item.setIconSize(resources.getDimensionPixelSize(R.dimen.share_post_icon_size));
    }

//    @Override
//    public void transData(String share_title, String share_content) {
//
//        PagerAdapter.transData(share_title, share_content);
//    }
}