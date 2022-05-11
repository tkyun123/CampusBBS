package com.example.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ShareOperation {
    public static void change_like_state(int uid, int pid, int fid, Handler handler, int state){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    String url;
                    if(state == 0){ // 当前未点赞
                        url = "/API/new_agreement";
                    }
                    else{
                        url = "/API/del_agreement";
                    }

                    String result = HttpRequest.post(url,
                            String.format("uid=%s&pid=%s&fid=%s", uid, pid, fid),
                            null);
                    Log.d("", result);
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("agree_state") == 1){
                        message.what = 0;
                    }
                    else{
                        message.what = -1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}
