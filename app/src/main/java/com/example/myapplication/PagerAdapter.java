package com.example.myapplication;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    public static ShareBrowse share_browse;
    public static MessageBrowse message_browse;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","动态"+(data_list.size()+1));
                            data.put("content","内容(按时间)");
                            data_list.add(data);
                            load_num--;
                        }
                    }

                    @Override
                    public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","动态"+(data_list.size()+1));
                            data.put("content","内容(按热度)");
                            data_list.add(data);
                            load_num--;
                        }
                    }
                });
                return share_browse;
            case 1:
                message_browse = new MessageBrowse((data_list, load_num) -> {
                    if(load_num == -1){
                        for(int i=0;i<10;i++){
                            Map<String, String> data = new HashMap<>();
                            data.put("content","**点赞了你的动态");
                            data.put("time","2022-5-1");
                            data_list.add(data);
                        }
                    }
                });
                return message_browse;
            case 2: return new SharePost();
            case 3:
                return new ShareSearch();
            case 4:
                return new UserInfo();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
