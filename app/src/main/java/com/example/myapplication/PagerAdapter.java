package com.example.myapplication;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    public static ShareBrowse share_browse;
    public static MessageBrowse message_browse;
    private Activity my_activity;
    public PagerAdapter(FragmentManager fm, int NumOfTabs, Activity activity) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        my_activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list,
                                                   int load_num, Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler, Consts.SORT_BY_TIME,
                                sort_all);
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list,
                                                   int load_num, Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler, Consts.SORT_BY_WAVE,
                                sort_all);
                    }
                }, true);
                return share_browse;
            case 1:
                return new MessageBrowse();
            case 2:
                return new SharePost();
            case 3:
                return new ShareSearch();
            case 4:
                return new UserInfo(0, 0);
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    private void shareBrowseLoadData(JSONArray data_list,
                                     int load_num, Handler handler, int type, int sort_all){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int page_index = (data_list.length()+load_num-1)/load_num;
                    int user_id = SystemService.getUserId(my_activity);

                    String result = HttpRequest.post("/API/get_page_posts",
                            String.format("page_size=%s&page_index=%s&order_type=%s&uid=%s&sort_type=%s",
                                    load_num, page_index, type, user_id, sort_all),
                            "form");
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = jsonObject.getJSONArray("data");
                    Log.d("array:", String.valueOf(array));
                    for(int i=0;i<array.length();i++){
                        data_list.put(array.getJSONObject(i));
                    }
                    message.what = 0;
                }catch (JSONException e) {
                    e.printStackTrace();
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}
