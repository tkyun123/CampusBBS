package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfo#} factory method to
 * create an instance of this fragment.
 */
public class UserInfo extends Fragment {

    private int my_flag = 0; // 0表示个人主页，1表示他人主页
    private final static int FLAG_SELF = 0;
    private final static int FLAG_OTHER = 1;

    private ActivityResultLauncher login_launcher;

    TextView nickName_textView;
    TextView introduction_textView;
    ImageView profile_photo_imageView;

    public UserInfo(int flag) {
        // Required empty public constructor
        my_flag = flag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        Button login_button = view.findViewById(R.id.user_info_login_button);

        nickName_textView = view.findViewById(R.id.user_info_nickName);
        introduction_textView = view.findViewById(R.id.user_info_introduction);
        profile_photo_imageView = view.findViewById(R.id.user_info_profile_photo);
        if(my_flag == FLAG_SELF){
            login_launcher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            login_button.setVisibility(View.INVISIBLE);
                            getUserInfo(intent.getIntExtra("user_id", 0));
                        }
                    });
            login_button.setOnClickListener((view1 -> {
                login_launcher.launch(new Intent(getActivity(), LoginActivity.class));
            }));
        }
        else{
            login_button.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private void getUserInfo(int user_id){
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == -1){
                    Toast.makeText(getContext(), "加载信息失败", Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    nickName_textView.setText(msg.getData().getString("nickname"));
                    String introduction = msg.getData().getString("introduction");
                    if(!introduction.equals("null")){
                        introduction_textView.setText(introduction);
                    }
                }
            }
        };

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                String result = HttpRequest.get(String.format("/API/user_info/%s",
                        user_id));
                Message message = new Message();
                if(result.equals("error")){
                    message.what = -1;
                }
                else{
                    try {
                        message.what = 0;
                        JSONObject jsonObject = new JSONObject(result);
                        Bundle bundle = new Bundle();
                        bundle.putString("nickname", jsonObject.getString("nickname"));
                        bundle.putString("introduction", jsonObject.getString(
                                    "intro"));
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.what = -1;
                    }
                }
            }
        };
        thread.start();
    }
}