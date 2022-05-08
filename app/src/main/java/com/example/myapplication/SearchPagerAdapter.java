package com.example.myapplication;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    int sort_type;
    public static ShareBrowse share_browse;

    public SearchPagerAdapter(FragmentManager fm, int NumOfTabs, int type) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.sort_type = type;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","文字动态"+(data_list.size()+1));
                            data.put("content","内容(按时间)");
                            data_list.add(data);
                            load_num--;
                        }
                    }

                    @Override
                    public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","文字动态"+(data_list.size()+1));
                            data.put("content","内容(按热度)");
                            data_list.add(data);
                            load_num--;
                        }
                    }
                });
                return share_browse;
            case 1:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","视频动态"+(data_list.size()+1));
                            data.put("content","内容(按时间)");
                            data_list.add(data);
                            load_num--;
                        }
                    }

                    @Override
                    public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","视频动态"+(data_list.size()+1));
                            data.put("content","内容(按热度)");
                            data_list.add(data);
                            load_num--;
                        }
                    }
                });
                return share_browse;
            case 2:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","音频动态"+(data_list.size()+1));
                            data.put("content","内容(按时间)");
                            data_list.add(data);
                            load_num--;
                        }
                    }

                    @Override
                    public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("title","音频动态"+(data_list.size()+1));
                            data.put("content","内容(按热度)");
                            data_list.add(data);
                            load_num--;
                        }
                    }
                });
                return share_browse;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
