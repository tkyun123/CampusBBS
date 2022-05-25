package com.example.myapplication;

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
                    public void loadDataSortByTime(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        // 加载文字数据
                        int a = 1;
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num, Handler handler, int sort_all) {

                    }
                }, true);
                return share_browse;
            case 2:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        // 加载视频数据
                        int a = 2;
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num, Handler handler, int sort_all) {

                    }
                },true);
                return share_browse;
            case 3:
                share_browse = new ShareBrowse(new ShareBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(JSONArray data_list, int load_num, Handler handler, int sort_all) {
                        // 加载音频数据
                        int a = 3;
                    }

                    @Override
                    public void loadDataSortByWave(JSONArray data_list, int load_num, Handler handler, int sort_all) {

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

    private void searchBrowseLoadData(JSONArray data_list,
                                     int load_num, Handler handler, int type){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                Message message = new Message();
//                try {
//                    int page_index = (data_list.length()+load_num-1)/load_num;
//                    int user_id = SystemService.getUserId(my_activity);
//
//                    String result = HttpRequest.post("/API/get_page_posts",
//                            String.format("page_size=%s&page_index=%s&order_type=%s&uid=%s",
//                                    load_num, page_index, type, user_id),
//                            null);
//                    JSONObject jsonObject = new JSONObject(result);
//                    JSONArray array = jsonObject.getJSONArray("data");
//                    for(int i=0;i<array.length();i++){
//                        data_list.put(array.getJSONObject(i));
//                    }
//                    message.what = 0;
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                    message.what = -1;
//                }
//                handler.sendMessage(message);
//            }
//        };
//        thread.start();
    }
}
