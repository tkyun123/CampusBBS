package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SharePost extends Fragment {
    private ScrollView scrollView;
    private String share_title;
    private String share_content;
    private EditText edit_share_title;
    private EditText edit_share_content;
    private LinearLayout multimedia_layout;
    private SwitchCompat  location_switch;
    private FloatingActionButton multimedia_button;
    private PopupWindow multimedia_popupWindow;

    private ImageView loading_icon;
    private Animation rotate;
    View my_view;

    private final int IMAGE_PICK = 1;
    private ActivityResultLauncher select_launcher;
    private ActivityResultLauncher image_add_launcher;
    private ActivityResultLauncher audio_add_launcher;
    private ActivityResultLauncher video_add_launcher;

    private final static int MULTIMEDIA_IMAGE = 0;
    private final static int MULTIMEDIA_AUDIO= 1;
    private final static int MULTIMEDIA_VIDEO= 2;
    private int multimedia_state = MULTIMEDIA_IMAGE;// 0表示选择图片，1表示选择音乐, 2表示视频

    private int image_num = 0;
    private int video_num = 0;
    private final List<Uri> uri_list = new ArrayList<>();
    private final JSONArray multimedia_list = new JSONArray();
    private String SAVE_PATH;
    private Uri save_uri; // 添加(而不是选择)多媒体时生成保存路径

    private String location = null;
    public static Handler location_handler;

    public SharePost() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_post, container, false);
        my_view = view;
        scrollView = view.findViewById(R.id.share_detail_scrollView);

        edit_share_title = view.findViewById(R.id.share_edit_title);
        edit_share_content = view.findViewById(R.id.share_edit_content);

        loading_icon = view.findViewById(R.id.sharePost_loading_icon);
        rotate = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);

        TextView share_post = view.findViewById(R.id.share_detail_postButton);

        share_post.setOnClickListener(view1 -> {
            share_title = edit_share_title.getText().toString();
            share_content = edit_share_content.getText().toString();
            if (share_title.equals("") || share_content.equals("")) {
                AlertDialog message = new AlertDialog.Builder(getContext())
                        .setMessage("标题或内容不能为空").create();
                message.show();
            }
            else{
                loading_icon.setAnimation(rotate);
                sharePost(share_title, share_content);
            }
        });

        multimedia_layout = my_view.findViewById(R.id.share_post_new_add_multimedia_layout);

        select_launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), (uri) -> {
                    if (uri != null) {
                        addMultimediaView(uri);
                    }
                });
        image_add_launcher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), (result)->{
                    if(result){

                        addMultimediaView(save_uri);
                    }
                }
        );
        audio_add_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),(result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Uri uri = result.getData().getData();

                        addMultimediaView(uri);
                    }
                })
        );
        video_add_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), (result)-> {
//                    if(result != null){
//                        addMultimediaView(save_uri);
//                    }
                    if(result.getResultCode() == Activity.RESULT_OK){
                        addMultimediaView(save_uri);
                    }
                }
        );

        location_switch = view.findViewById(R.id.show_location_switch);
        TextView location_textView = view.findViewById(R.id.show_location_textView);
        location_switch.setOnClickListener(view1 -> {
            if (location_switch.isChecked()) {
                location_textView.setText(R.string.show_location_text);
                if(location == null){
                    location = SystemService.DEFAULT_LOCATION;
                    loading_icon.setAnimation(rotate);
                    SystemService.getLocation(getContext(), location_handler);
                }
            } else {
                location_textView.setText(R.string.not_show_location_text);
            }
        });

        location_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    location = msg.getData().getString("location");
                    Log.d("", location);
                }
                loading_icon.setAnimation(null);
            }
        };

        multimedia_button = view.findViewById(R.id.share_post_floating_button);
        multimedia_button.setOnClickListener(view1 -> {
            initPopupWindow(view);
        });
        SAVE_PATH = getContext().getExternalFilesDir("").getPath();
        Log.d("", SAVE_PATH);
        return view;
    }


    public void selectMultimedia(int state) {
        multimedia_popupWindow.dismiss();
        multimedia_state = state;
        if (multimedia_state == MULTIMEDIA_IMAGE) {
            select_launcher.launch("image/*");
        }
        else if(multimedia_state == MULTIMEDIA_AUDIO){
            select_launcher.launch("audio/*");
        }
        else{
            select_launcher.launch("video/*");
        }
    }

    public void addMultimedia(int state){
        multimedia_popupWindow.dismiss();
        multimedia_state = state;
        if (multimedia_state == MULTIMEDIA_IMAGE) {
            filepathToUri(".jpg");
            image_add_launcher.launch(save_uri);
        }
        else if(multimedia_state == MULTIMEDIA_AUDIO){
            filepathToUri(".mp3");
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if(getActivity().getPackageManager().queryIntentActivities(
                    intent, PackageManager.MATCH_DEFAULT_ONLY).size()>0){
                audio_add_launcher.launch(intent);
            }
            else{
                Toast.makeText(getContext(), "无可用录音工具", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else{
            filepathToUri(".mp4");
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, save_uri);
            video_add_launcher.launch(intent);
        }
    }

    private void filepathToUri(String filetype){
        File file = getFile(filetype);
        save_uri = FileProvider.getUriForFile(
                getContext(),
                getContext().getPackageName()+".fileProvider",
                file);
    }

    private File getFile(String filetype){
        int n = 0;
        String path;
        File file;
        do {
            path = String.format("%s/%s%s",SAVE_PATH, n, filetype);
            file = new File(path);
            n++;
        }while (file.exists());
        Log.d("", path);
        return file;
    }

    public void addMultimediaView(Uri uri) {
        View new_add_view;
        if (multimedia_state == MULTIMEDIA_IMAGE) {
            image_num++;
            new_add_view = LayoutInflater.from(this.getContext()).inflate(
                    R.layout.image_add_item, null
            );
            multimedia_layout.addView(new_add_view);
            ImageView new_add_imageView = new_add_view.findViewById(R.id.new_add_imageView);
            new_add_imageView.setImageURI(uri);

        } else { // 音视频使用同一个view
            video_num++;
            new_add_view = LayoutInflater.from(this.getContext()).inflate(
                    R.layout.video_add_item, null
            );
            multimedia_layout.addView(new_add_view);
            VideoView new_add_videoView = new_add_view.findViewById(R.id.new_add_videoView);
            new_add_videoView.setMediaController(new MediaController(this.getContext()));
            new_add_videoView.setVideoURI(uri);
            if(multimedia_state == MULTIMEDIA_AUDIO){
                new_add_videoView.setBackground(getResources().getDrawable(
                        R.drawable.audio_background));
            }
//            new_add_videoView.start();
        }
        uri_list.add(uri);
        ImageView delete_icon = new_add_view.findViewById(R.id.image_delete_icon);
        delete_icon.setOnClickListener((view) -> {
            multimedia_layout.removeView(new_add_view);
            uri_list.remove(uri_list.size()-1);
            if (multimedia_state == 0) {
                image_num--;
            } else {
                video_num--;
            }
        });
        scrollToBottom();
    }

    public void scrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void sharePost(String title, String content){
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                loading_icon.setAnimation(null);
                if(msg.what == -1){
                    Toast.makeText(getContext(), "发布失败，请稍后再试", Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    Toast.makeText(getContext(), "发布成功", Toast.LENGTH_SHORT)
                            .show();
                }
                clearAll();
            }
        };

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                message.what = 0;
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                        "login", Context.MODE_PRIVATE
                );
                int user_id = sharedPreferences.getInt("user_id", -1);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", user_id);
                    jsonObject.put("title", title);
                    jsonObject.put("text", content);
                    jsonObject.put("theme", 0);
                    jsonObject.put("type",0);
                    if(location_switch.isChecked()){
                        jsonObject.put("position", location);
                    }
                    else{
                        jsonObject.put("position","");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = -1;
                    return;
                }
                Log.d("", jsonObject.toString());
                String result = HttpRequest.post("/API/new_post",
                        jsonObject.toString(), "json");
                if(result.equals("error")){
                    message.what = -1;
                }
                else{
                    message.what = 0;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private void clearAll(){
        edit_share_title.setText("");
        edit_share_content.setText("");
        uri_list.clear();
        multimedia_layout.removeAllViews();
    }

    private void initPopupWindow(View parent_view){
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.share_post_multimedia_popupwindow, null, false);
        multimedia_popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        multimedia_popupWindow.setBackgroundDrawable(new ColorDrawable(
                getResources().getColor(R.color.white)));
        multimedia_popupWindow.showAtLocation(parent_view, Gravity.BOTTOM, 0, 0);

        LinearLayout image_select = view.findViewById(R.id.image_select);
        LinearLayout audio_select = view.findViewById(R.id.audio_select);
        LinearLayout video_select = view.findViewById(R.id.video_select);
        LinearLayout image_add = view.findViewById(R.id.image_add);
        LinearLayout audio_add = view.findViewById(R.id.audio_add);
        LinearLayout video_add = view.findViewById(R.id.video_add);

        image_select.setOnClickListener(view1 -> {
            selectMultimedia(MULTIMEDIA_IMAGE);
        });
        audio_select.setOnClickListener(view1 -> {
            selectMultimedia(MULTIMEDIA_AUDIO);
        });
        video_select.setOnClickListener(view1 -> {
            selectMultimedia(MULTIMEDIA_VIDEO);
        });
        image_add.setOnClickListener(view1 -> {
            addMultimedia(MULTIMEDIA_IMAGE);
        });
        audio_add.setOnClickListener(view1 -> {
            addMultimedia(MULTIMEDIA_AUDIO);
        });
        video_add.setOnClickListener(view1 -> {
            addMultimedia(MULTIMEDIA_VIDEO);
        });
    }
}