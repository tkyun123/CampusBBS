package com.example.myapplication;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
//    int sort_type;
    public static ShareBrowse share_browse;
    public static UserBrowse user_browse;


    public SearchPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
//        this.sort_type = type;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {

                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {

                    }
                }, true);
                return share_browse;
            case 1:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {

                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {

                    }

                }, true);
                return share_browse;
            case 2:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {

                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {

                    }
                }, true);
                return share_browse;
            case 3:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {

                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {

                    }
                }, true);
                return share_browse;
            case 4:
                user_browse = new UserBrowse(new UserBrowse.loadData() {
                    @Override
                    public void loadData(JSONArray data_list, int load_num, Handler handler) {

                    }
                });
                return user_browse;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
