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
                            "form");
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

    public static void change_relation(int agent_id, int patient_id, Handler handler, int new_relation){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    String url = "";
                    if(new_relation == Consts.RELATION_NONE){
                        url = "/API/cancel_relation";
                    }
                    else if(new_relation == Consts.RELATION_FOLLOW){
                        url = "/API/sub";
                    }
                    else{
                        url = "/API/block";
                    }
                    String result = HttpRequest.post(url,
                            String.format("uid_1=%s&uid_2=%s", agent_id, patient_id),
                            "form");
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("state") == 1){
                        message.what = 0;
                        message.arg1 = new_relation;
                    }
                    else{
                        message.what = 1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = 1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}
