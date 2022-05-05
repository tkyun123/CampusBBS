package com.example.myapplication.datatype;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfoStorage {
    public int user_id;
    public String nickName;
    public String introduction;
    public String profile_photo_url;
    public String token;

    public void addInfo(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putInt("user_id", user_id);
        editor.putString("nickName", nickName);
        editor.putString("introduction", introduction);
        editor.putString("profile_photo_url", profile_photo_url);
        editor.apply();
    }
}
