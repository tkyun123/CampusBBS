package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.datatype.UserInfoStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfo#} factory method to
 * create an instance of this fragment.
 */
public class UserInfo extends Fragment {

    private int my_flag = 0; // 0表示个人主页，1表示他人主页
    public final static int FLAG_SELF = 0;
    public final static int FLAG_OTHER = 1;

    private ActivityResultLauncher login_launcher;

    private TextView nickName_textView;
    private TextView introduction_textView;
    private ImageView profile_photo_imageView;
    private Button login_button;

    private ImageView loading_icon;
    private Animation rotate;

    private int user_id;

    private ActivityResultLauncher info_modify_launcher;

    public UserInfo(int flag, int uid) {
        // Required empty public constructor
        my_flag = flag;
        user_id = uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        login_button = view.findViewById(R.id.user_info_login_button);

        loading_icon = view.findViewById(R.id.user_info_loading_icon);
        rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        nickName_textView = view.findViewById(R.id.user_info_nickName);
        introduction_textView = view.findViewById(R.id.user_info_introduction);
        profile_photo_imageView = view.findViewById(R.id.user_info_profile_photo);
        if(my_flag == FLAG_SELF){
            if(!SystemService.checkLogin(getActivity())){
                login_launcher = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(), result -> {
                            if(result.getResultCode() == Activity.RESULT_OK){
                                Intent intent = result.getData();
                                login_button.setVisibility(View.INVISIBLE);
                                user_id = intent.getIntExtra("user_id", 0);
                                getUserInfo();
                            }
                        });
                login_button.setOnClickListener((view1 -> {
                    login_launcher.launch(new Intent(getActivity(), LoginActivity.class));
                }));
            }
            else{
                UserInfoStorage stored_user = new UserInfoStorage();
                SystemService.getInfo(getActivity(), stored_user);
                nickName_textView.setText(stored_user.nickName);
                introduction_textView.setText(stored_user.introduction);
                user_id = stored_user.user_id;
                changeLoginToModify();
            }
            init_info_modify_launcher();
        }
        else{
            login_button.setVisibility(View.INVISIBLE);
            getUserInfo();
        }

        return view;
    }

    private void changeLoginToModify(){
        login_button.setText(R.string.modify_self_info);
        login_button.setOnClickListener(view -> {
            // TO DO 修改个人信息
            info_modify_launcher.launch(new Intent(getActivity(),
                    InfoModifyActivity.class));
        });
    }

    private void getUserInfo(){
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == -1){
                    Toast.makeText(getContext(), "加载信息失败", Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    String introduction = msg.getData().getString("introduction");
                    if(my_flag == FLAG_SELF){
                        UserInfoStorage userInfoStorage = new UserInfoStorage();
                        userInfoStorage.nickName = msg.getData().getString("nickname");
                        userInfoStorage.introduction = getActivity().getResources().getString(
                                R.string.user_info_introduction_default);
                        userInfoStorage.user_id = user_id;
                        if(!introduction.equals("null")){
                            userInfoStorage.introduction = introduction;
                        }
                        SystemService.addInfo(getActivity(), userInfoStorage);

                        changeLoginToModify();
                    }
                    nickName_textView.setText(msg.getData().getString("nickname"));
                    if(!introduction.equals("null")){
                        introduction_textView.setText(introduction);
                    }
                }
                loading_icon.setAnimation(null);
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
                        bundle.putInt("user_id", 0);
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
        loading_icon.setAnimation(rotate);
    }

    private void init_info_modify_launcher(){
        info_modify_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),(result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = Uri.fromFile(new File(data.getStringExtra("path")));
                        profile_photo_imageView.setImageURI(uri);
                        nickName_textView.setText(data.getStringExtra("nickName"));
                        introduction_textView.setText(data.getStringExtra("introduction"));
                    }
                }));
    }
}