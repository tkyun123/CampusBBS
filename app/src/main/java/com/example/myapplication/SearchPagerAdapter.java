package com.example.myapplication;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public static ShareBrowse share_browse;
    public static UserBrowse user_browse;
    private Activity my_activity;
    private String keyWord;

    public SearchPagerAdapter(FragmentManager fm, int NumOfTabs, Activity activity, String search_content) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        my_activity = activity;
        keyWord = search_content;
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
                        shareBrowseLoadData(data_list, load_num, handler,  -1, keyWord);
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num,
                                                   Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler, -1, keyWord);
                    }
                }, false, false);
                return share_browse;
            case 1:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler, 0, keyWord);
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler,  0, keyWord);
                    }
                },  false, false);
                return share_browse;
            case 2:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler, 1, keyWord);
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler, 1, keyWord);
                    }
                },false, false);
                return share_browse;
            case 3:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler, 2, keyWord);
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        shareBrowseLoadData(data_list, load_num, handler, 2, keyWord);
                    }
                }, false, false);
                return share_browse;
            case 4:
                user_browse = new UserBrowse(new UserBrowse.loadData() {
                    @Override
                    public void loadData(JSONArray data_list, int load_num, Handler handler) {
                        userBrowseLoadData(data_list, load_num, handler, keyWord);
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

    private void shareBrowseLoadData(JSONArray data_list,
                                     int load_num, Handler handler, int type,
                                     String keyWord){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int page_index = (data_list.length()+load_num-1)/load_num;
                    int user_id = SystemService.getUserId(my_activity);

                    String result = HttpRequest.post("/API/search_page_posts",
                            String.format("page_size=%s&page_index=%s&type=%s&uid=%s&keyword=%s",
                                    load_num, page_index, type, user_id, keyWord),
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

    private void userBrowseLoadData(JSONArray data_list,
                                     int load_num, Handler handler,
                                     String keyWord){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int page_index = (data_list.length()+load_num-1)/load_num;
                    int user_id = SystemService.getUserId(my_activity);

                    String result = HttpRequest.post("/API/search_page_users",
                            String.format("page_size=%s&page_index=%s&uid=%s&keyword=%s",
                                    load_num, page_index, user_id, keyWord),
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
