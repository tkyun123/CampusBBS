package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.JsonReader;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
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

//        SharedPreferences sharedPreferences = getSharedPreferences(
//                "login", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.commit();

        if(!SystemService.checkLogin(this)){
            pager.setCurrentItem(map.get(R.id.tab_personInfo));
            navigator.setSelectedItemId(R.id.tab_personInfo);
        }

        navigator.setOnItemSelectedListener(item -> {
//            pager.setCurrentItem(map.get(item.getItemId()));
//            return true;
            if(item.getItemId() != R.id.tab_personInfo && !SystemService.checkLogin(this)){
//                startActivity(new Intent(this, LoginActivity.class));
                pager.setCurrentItem(map.get(R.id.tab_personInfo));
                navigator.setSelectedItemId(R.id.tab_personInfo);
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

        // 发布时多媒体资源的缓存目录，发布后立即清空
        FileOperation.createDir(this, "/multimedia");

        // 草稿箱多媒体资源的根目录；每创建一个草稿，这个目录下创建一个子目录(可以通过时间戳来分别)用来存储多媒体资源的缓存
        // 子目录直到草稿删除再删除
        FileOperation.createDir(this, "/drafts");
        FileOperation.clearDir(this, "/multimedia");

    }

}