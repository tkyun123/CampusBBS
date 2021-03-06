package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public ViewPager pager;
    public PagerAdapter adapter;
    public BottomNavigationView navigator;
    public Menu menu;

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

        menu = navigator.getMenu();

        Map<Integer, Integer> map = new HashMap<>();
        map.put(R.id.tab_browse, 0);
        map.put(R.id.tab_message, 1);
        map.put(R.id.tab_post, 2);
        map.put(R.id.tab_search,3);
        map.put(R.id.tab_personInfo,4);


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

        // ???????????????????????????????????????????????????????????????
        FileOperation.createDir(this, "/multimedia");

        // TODO(???????????????????????????????????????)
        // ???????????????????????????????????????????????????????????????????????????????????????????????????(??????????????????????????????)????????????????????????????????????
        // ????????????????????????????????????
        FileOperation.createDir(this, "/drafts");
//        FileOperation.clearDir(this, "/multimedia");

        // ?????????????????????
        FileOperation.createDir(this,  "/tmp");

        if(SystemService.checkLogin(this)){
            Intent intent = new Intent(this, AskForNoticeService.class);
            intent.putExtra("user_id",  SystemService.getUserId(this));
            stopService(intent);
            startService(intent);
        }
//        SystemService.clearInfo(this);
        FileOperation.listFile(this, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("a","destroy");
        FileOperation.clearDir(this, "/tmp");
    }
}