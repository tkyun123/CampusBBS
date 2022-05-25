package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.datatype.UserInfoStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfo#} factory method to
 * create an instance of this fragment.
 */
public class UserInfo extends Fragment {

    private int my_flag = 0; // 0表示个人主页，1表示他人主页
    public final static int FLAG_SELF = 0;
    public final static int FLAG_OTHER = 1;

    private TextView nickName_textView;
    private TextView introduction_textView;
    private ImageView profile_photo_imageView;
    private Button info_modify_button;
    private Button login_button;
    private RelativeLayout draft_layout;

    private LinearLayout following_layout;
    private LinearLayout block_layout;
    private LinearLayout followed_layout;
    private LinearLayout share_layout;
    private TextView following_num_textView;
    private TextView block_num_textView;
    private TextView followed_num_textView;
    private TextView share_num_textView;

    private Button follow_button;
    private Button block_button;

    private ImageView loading_icon;
    private Animation rotate;

    private int user_id;
    private int relation = -1;
    private String photo_path = "default";

    private ActivityResultLauncher login_launcher;
    private ActivityResultLauncher info_modify_launcher;

    private final static int BASIC_INFO_MODIFIED = 2;

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

        if(my_flag == FLAG_OTHER && user_id == SystemService.getUserId(getActivity())){
            my_flag = FLAG_SELF;
        }

        login_button = view.findViewById(R.id.user_info_login_button);
        info_modify_button = view.findViewById(R.id.user_info_modify_button);

        draft_layout = view.findViewById(R.id.user_info_draft);

        loading_icon = view.findViewById(R.id.user_info_loading_icon);
        rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        nickName_textView = view.findViewById(R.id.user_info_nickName);
        introduction_textView = view.findViewById(R.id.user_info_introduction);
        profile_photo_imageView = view.findViewById(R.id.user_info_profile_photo);

        following_layout = view.findViewById(R.id.user_info_following_layout);
        block_layout = view.findViewById(R.id.user_info_block_layout);
        followed_layout = view.findViewById(R.id.user_info_followed_layout);
        share_layout = view.findViewById(R.id.user_info_share_layout);

        following_num_textView = view.findViewById(R.id.user_info_following_num);
        block_num_textView = view.findViewById(R.id.user_info_block_num);
        followed_num_textView = view.findViewById(R.id.user_info_followed_num);
        share_num_textView = view.findViewById(R.id.user_info_share_num);

        follow_button = view.findViewById(R.id.user_info_follow_button);
        block_button = view.findViewById(R.id.user_info_block_button);

        RelativeLayout relation_layout = view.findViewById(R.id.user_info_relation_layout);
        if(my_flag == FLAG_SELF){
            init_launcher();
            relation_layout.removeAllViews();
            follow_button.setVisibility(View.INVISIBLE);
            block_button.setVisibility(View.INVISIBLE);
            if(!SystemService.checkLogin(getActivity())){
                login_button.setOnClickListener((view1 -> {
                    login_launcher.launch(new Intent(getActivity(), LoginActivity.class));
                }));
            }
            else{
                user_id = SystemService.getUserId(getActivity());
                init_activities();
                getUserInfo();
            }

            draft_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), DraftActivity.class);

                    startActivity(intent);
                }
            });
        }
        else{
            login_button.setVisibility(View.INVISIBLE);
            init_activities();
            init_relation_button();
            getUserInfo();
        }

        info_modify_button.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), InfoModifyActivity.class);
            intent.putExtra("nickName", nickName_textView.getText().toString());
            intent.putExtra("introduction", introduction_textView.getText().toString());
            intent.putExtra("photo_path", photo_path);
            info_modify_launcher.launch(intent);
        });


        return view;
    }

    private void changeToLoginState(){
        login_button.setText(R.string.user_info_logout_text);
        login_button.setOnClickListener(view -> {
            changeToLogoutState();
        });
        info_modify_button.setText(R.string.user_info_modify_text);
        info_modify_button.setVisibility(View.VISIBLE);
    }

    private void changeToLogoutState(){
        login_button.setText(R.string.user_info_login_text);
        login_button.setOnClickListener((view1 -> {
            login_launcher.launch(new Intent(getActivity(), LoginActivity.class));
        }));
        info_modify_button.setVisibility(View.INVISIBLE);
        SystemService.clearInfo(getActivity());
        resetView();
    }

    private void resetView(){
        profile_photo_imageView.setImageResource(R.drawable.default_profile_photo);
        nickName_textView.setText(R.string.user_info_nickName_default);
        introduction_textView.setText(R.string.user_info_introduction_default);

        following_num_textView.setText("0");
        block_num_textView.setText("0");
        followed_num_textView.setText("0");
        share_num_textView.setText("0");
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
                    if(my_flag == FLAG_SELF){
                        changeToLoginState();
                    }
                    nickName_textView.setText(msg.getData().getString("nickname"));
                    String introduction = msg.getData().getString("introduction");
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
                String result = HttpRequest.get(String.format("/API/user_info/%s/%s",
                        SystemService.getUserId(getActivity()), user_id));
                Message message = new Message();
                if(result.equals("error")){
                    message.what = -1;
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        Bundle bundle = new Bundle();
                        bundle.putString("nickname", jsonObject.getString("nickname"));
                        bundle.putString("introduction", jsonObject.getString(
                                    "intro"));

                        following_num_textView.post(() -> {
                            try {
                                following_num_textView.setText(
                                        String.valueOf(jsonObject.getInt("sub_count")));
                                block_num_textView.setText(
                                        String.valueOf(
                                                jsonObject.getInt("block_count")));
                                followed_num_textView.setText(
                                        String.valueOf(
                                                jsonObject.getInt("_sub_count")));
                                share_num_textView.setText(
                                        String.valueOf(
                                                jsonObject.getInt("post_count")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

                        if(my_flag == 1){
                            int r = jsonObject.getInt("relation");
                            if(r == 1){
                                relation = Consts.RELATION_FOLLOW;
                                follow_button.post(() -> {
                                    follow_button.setText(R.string.unfollow);
                                });
                            }
                            else if(r == 0){
                                relation = Consts.RELATION_BLOCK;
                                block_button.post(() -> {
                                    block_button.setText(R.string.unblock);
                                });
                            }
                        }

                        String url = jsonObject.getString("pic_url");
                        if(!url.equals("null")){
                            url = SystemService.getBaseUrl()+url;
                            Bitmap bitmap = SystemService.getBitmapFromUrl(url);
                            profile_photo_imageView.post(() -> {
                                profile_photo_imageView.setImageBitmap(bitmap);
                            });
                            photo_path = url;
                        }

                        message.setData(bundle);
                        message.what = 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.what = -1;
                    }
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
        loading_icon.setAnimation(rotate);
    }

    private void init_launcher(){
        login_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        user_id = intent.getIntExtra("user_id", 0);
                        init_activities();
                        UserInfoStorage user = new UserInfoStorage();
                        user.user_id = user_id;
                        SystemService.addInfo(getActivity(), user);
                        getUserInfo();
                    }
                });
        info_modify_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),(result -> {
                    if(result.getResultCode() == BASIC_INFO_MODIFIED){
                        getUserInfo();
                    }
                }));
    }

    private void init_activities(){
        following_layout.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), RelationUserActivity.class);
            intent.putExtra("type", Consts.RELATION_FOLLOW);
            intent.putExtra("user_id", user_id);
            getActivity().startActivity(intent);
        });

        block_layout.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), RelationUserActivity.class);
            intent.putExtra("type", Consts.RELATION_BLOCK);
            intent.putExtra("user_id", user_id);
            getActivity().startActivity(intent);
        });

        followed_layout.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), RelationUserActivity.class);
            intent.putExtra("type", Consts.RELATION_FOLLOWED);
            intent.putExtra("user_id", user_id);
            getActivity().startActivity(intent);
        });

        share_layout.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), UserShareActivity.class);
            intent.putExtra("user_id", user_id);
            getActivity().startActivity(intent);
        });
    }

    private void init_relation_button(){
        int agent_id = SystemService.getUserId(getActivity());
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    relation = msg.arg1;
                    if(relation == Consts.RELATION_FOLLOW){
                        follow_button.setText(R.string.unfollow);
                        block_button.setText(R.string.block);
                    }
                    else if(relation == Consts.RELATION_BLOCK){
                        follow_button.setText(R.string.follow);
                        block_button.setText(R.string.unblock);
                    }
                    else{
                        follow_button.setText(R.string.follow);
                        block_button.setText(R.string.block);
                    }
                }
            }
        };
        follow_button.setOnClickListener(view -> {
            int new_relation;
            if(relation != Consts.RELATION_FOLLOW) {
                new_relation = Consts.RELATION_FOLLOW;
            }
            else{
                new_relation = Consts.RELATION_NONE;
            }
            ShareOperation.change_relation(agent_id, user_id,
                    handler, new_relation);
        });
        block_button.setOnClickListener(view -> {
            int new_relation;
            if(relation != Consts.RELATION_BLOCK){
                new_relation = Consts.RELATION_BLOCK;
            }
            else{
                new_relation = Consts.RELATION_NONE;
            }
            ShareOperation.change_relation(agent_id, user_id,
                    handler, new_relation);
        });
    }
}