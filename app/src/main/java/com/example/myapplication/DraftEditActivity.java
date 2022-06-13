package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DraftEditActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private LinearLayout multimedia_layout;
    private SwitchCompat location_switch;
    private FloatingActionButton multimedia_button;
    private PopupWindow multimedia_popupWindow;

    private ImageView loading_icon;
    private Animation rotate;

    private Spinner share_type_select_spinner;
    private final static int TEXT = 0;
    private final static int IMAGE = 1;
    private final static int AUDIO = 2;
    private final static int VIDEO = 3;
    private int share_type = TEXT;

    private final int IMAGE_PICK = 1;
    private ActivityResultLauncher select_launcher;
    private ActivityResultLauncher image_add_launcher;
    private ActivityResultLauncher audio_add_launcher;
    private ActivityResultLauncher video_add_launcher;

    private final static int MULTIMEDIA_IMAGE = 1;
    private final static int MULTIMEDIA_AUDIO= 2;
    private final static int MULTIMEDIA_VIDEO= 3;
    private int multimedia_state = MULTIMEDIA_IMAGE;// 0表示选择图片，1表示选择音乐, 2表示视频

    private int multimedia_num = 0;

    private final JSONArray multimedia_list = new JSONArray();
    private String SAVE_PATH;
    private Uri save_uri; // 添加(而不是选择)多媒体时生成保存路径

    private String location = null;
    public static Handler location_handler;

    TextView deleteButton;
    TextView postButton;
    TextView saveButton;

    private String share_title;
    private String share_content;

    EditText titleEdit;
    EditText contentEdit;
    String oldLocation;

    int user_id;
    String draft_id;
    int type;

    Boolean is_saved = false;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_edit);

        SharedPreferences loginShared = DraftEditActivity.this.getSharedPreferences(
                "login", Context.MODE_PRIVATE
        );
        user_id = loginShared.getInt("user_id", -1);

        sharedPreferences = DraftEditActivity.this.getSharedPreferences(
                "draft_" + user_id, Context.MODE_PRIVATE
        );

        loading_icon = findViewById(R.id.draftEdit_loading_icon);
        rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading_anim);

        scrollView = findViewById(R.id.draft_edit_scrollView);

        Toolbar tool_bar = findViewById(R.id.draft_edit_tool_bar);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            if(!is_saved) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DraftEditActivity.this);
                builder.setMessage("是否保存修改？");
                builder.setCancelable(true);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        share_title = titleEdit.getText().toString();
                        share_content = contentEdit.getText().toString();
                        saveShare(share_title, share_content);
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                finish();
            }
        });

        titleEdit = findViewById(R.id.draft_edit_title);
        contentEdit = findViewById(R.id.draft_edit_content);

        deleteButton = findViewById(R.id.draft_edit_deleteButton);
        postButton = findViewById(R.id.draft_edit_postButton);
        saveButton = findViewById(R.id.draft_edit_saveButton);

        Intent intent = getIntent();
        titleEdit.setText(intent.getStringExtra("draft_title"));
        contentEdit.setText(intent.getStringExtra("draft_content"));
        draft_id = intent.getStringExtra("draft_id");
        type = Integer.parseInt(intent.getStringExtra("type"));

        // 新加
        multimedia_layout = findViewById(R.id.draft_edit_new_add_multimedia_layout);

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
                    if(result.getResultCode() == Activity.RESULT_OK){
                        addMultimediaView(save_uri);
                    }
                }
        );

        location_switch = findViewById(R.id.show_location_switch);
        TextView location_textView = findViewById(R.id.show_location_textView);
        if(oldLocation != "") {
            location_switch.setChecked(true);
            location_textView.setText(R.string.show_location_text);
            location = oldLocation;
        }
        location_switch.setOnClickListener(view1 -> {
            if (location_switch.isChecked()) {
                location_textView.setText(R.string.show_location_text);
                if(location == null){
                    location = SystemService.DEFAULT_LOCATION;
                    loading_icon.setAnimation(rotate);
                    SystemService.getLocation(getApplicationContext(), location_handler);
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
                }
                loading_icon.setAnimation(null);
            }
        };

        multimedia_button = findViewById(R.id.draft_edit_floating_button);
        multimedia_button.setOnClickListener(view1 -> initPopupWindow(getWindow().getDecorView()));

        SAVE_PATH = getApplicationContext().getExternalFilesDir("").getPath()+"/multimedia";

        share_type_select_spinner = findViewById(R.id.draft_select_share_type_spinner);
        share_type_select_spinner.setSelection(type);
        share_type_select_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                share_type = i;
                multimedia_layout.removeAllViews();
                SystemService.clearJsonArray(multimedia_list);
                multimedia_num = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DraftEditActivity.this);
                builder.setMessage("确认删除该草稿？");
                builder.setCancelable(true);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("is_deleted" + draft_id, true);
                        editor.commit();

                        finish();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_title = titleEdit.getText().toString();
                share_content = contentEdit.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(DraftEditActivity.this);
                builder.setMessage("确认发布？");
                builder.setCancelable(true);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(checkPostValid()){
                            loading_icon.setAnimation(rotate);
                            postShare(share_title, share_content);
                        }
                        finish();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_title = titleEdit.getText().toString();
                share_content = contentEdit.getText().toString();
                saveShare(share_title, share_content);
                Toast toast = Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                is_saved = true;
            }
        });

    }

    private void saveShare(String title, String content){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String realDate = df.format(date);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("title" + draft_id, title);
        editor.putString("content" + draft_id, content);
        editor.putString("date" + draft_id, realDate);
        editor.putInt("type" + draft_id, share_type);
        if(location_switch.isChecked()) {
            editor.putString("location" + draft_id, location);
        }
        else {
            editor.putString("location" + draft_id, "");
        }
        editor.commit();
    }

    private void postShare(String title, String content){
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                loading_icon.setAnimation(null);
                if(msg.what == -1){
                    Toast.makeText(getApplicationContext(), "发布失败，请稍后再试", Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT)
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

                SharedPreferences sharedPreferencesDraft = DraftEditActivity.this.getSharedPreferences(
                        "draft_" + user_id, Context.MODE_PRIVATE
                );
                SharedPreferences.Editor editor = sharedPreferencesDraft.edit();
                editor.putBoolean("is_deleted" + draft_id, true);
                editor.commit();


                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", user_id);
                    jsonObject.put("title", title);
                    jsonObject.put("text", content);
                    jsonObject.put("theme", 0);
                    int type = -1;
                    if(share_type == TEXT || share_type == IMAGE){
                        type = 0;
                    }
                    else if(share_type == VIDEO){
                        type = 2;
                    }
                    else if(share_type == AUDIO){
                        type = 1;
                    }
                    jsonObject.put("type",type);
                    if(location_switch.isChecked()){
                        jsonObject.put("position", location);
                    }
                    else{
                        jsonObject.put("position","");
                    }
                    String result = HttpRequest.post("/API/new_post",
                            jsonObject.toString(), "json");
                    if(result.equals("error")){
                        message.what = -1;
                    }
                    else{
                        message.what = 0;
                        // 传输多媒体资源
                        if(share_type != TEXT){
                            JSONObject post_result = new JSONObject(result);
                            int pid = post_result.getInt("pid");

                            JSONObject object = new JSONObject();
                            JSONArray list = new JSONArray();

                            object.put("uid", user_id);
                            object.put("pid", pid);
                            object.put("fid", 0);
                            for(int i=0;i<multimedia_list.length();i++){
                                JSONObject multimedia_object = new JSONObject();
                                Uri add_uri = Uri.parse(multimedia_list.getJSONObject(i).
                                        getString("uri"));
                                if(share_type == IMAGE){
                                    multimedia_object.put("mul_type", "pic");
                                    multimedia_object.put("filename",
                                            String.format("%s_%s.jpg", pid, user_id));
                                    multimedia_object.put("data",
                                            Codec.imageUriToBase64(add_uri,
                                                    DraftEditActivity.this.getContentResolver(),
                                                    false));
                                }
                                else if(share_type == VIDEO) {
                                    multimedia_object.put("mul_type", "video");
                                    multimedia_object.put("filename",
                                            String.format("%s_%s.mp4", pid, user_id));
                                    multimedia_object.put("data",
                                            Codec.videoUriToBase64(add_uri, DraftEditActivity.this.getContentResolver()));
                                }
                                else if(share_type == AUDIO){
                                    multimedia_object.put("mul_type", "video");
                                    multimedia_object.put("filename",
                                            String.format("%s_%s.mp3", pid, user_id));
                                    multimedia_object.put("data",
                                            Codec.videoUriToBase64(add_uri, DraftEditActivity.this.getContentResolver()));
                                }
                                list.put(multimedia_object);
                            }
                            object.put("source_data", list);

                            String multimedia_result = HttpRequest.post("/API/update",
                                    object.toString(), "json");
                            JSONObject multimedia_object = new JSONObject(multimedia_result);
                            if(multimedia_object.getInt("update_state") == 1){
                                message.what = 0;
                            }
                            else{
                                message.what = -1;
                            }
                        }
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

    private boolean checkPostValid(){
        share_title = titleEdit.getText().toString();
        share_content = contentEdit.getText().toString();
        if (share_title.equals("") || share_content.equals("")) {
            Toast.makeText(getApplicationContext(), "标题或内容不能为空", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if(share_type != TEXT && multimedia_num == 0){
            Toast.makeText(getApplicationContext(), "未添加多媒体资源", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void clearAll(){
        SystemService.clearJsonArray(multimedia_list);
        multimedia_layout.removeAllViews();
        multimedia_num = 0;
        FileOperation.clearDir(getApplicationContext(), "/multimedia");
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
        String path;
        if (multimedia_state == MULTIMEDIA_IMAGE) {
            path = generatePath(".jpg");
            save_uri = FileOperation.getUriFromFilepath(getApplicationContext(), path);
            image_add_launcher.launch(save_uri);
        }
        else if(multimedia_state == MULTIMEDIA_AUDIO){
            path = generatePath(".mp3");
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if(DraftEditActivity.this.getPackageManager().queryIntentActivities(
                    intent, PackageManager.MATCH_DEFAULT_ONLY).size()>0){
                audio_add_launcher.launch(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), "无可用录音工具", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else{
            path = generatePath(".mp4");
            save_uri = FileOperation.getUriFromFilepath(getApplicationContext(), path);
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, save_uri);
            video_add_launcher.launch(intent);
        }
    }

    private String generatePath(String filetype){
        String path;
        path = String.format("%s/%s%s",SAVE_PATH, new Date().getTime(), filetype);
        return path;
    }

    public void addMultimediaView(Uri uri)  {
        View new_add_view;

        JSONObject object = new JSONObject();

        String type;
        if (multimedia_state == MULTIMEDIA_IMAGE) {
            multimedia_num++;
            new_add_view = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.image_add_item, null
            );
            multimedia_layout.addView(new_add_view);
            ImageView new_add_imageView = new_add_view.findViewById(R.id.new_add_imageView);
            new_add_imageView.setImageURI(uri);
            type = "jpg";
        } else {
            new_add_view = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.video_add_item, null
            );
            multimedia_layout.addView(new_add_view);
            VideoView new_add_videoView = (VideoView) new_add_view.findViewById(R.id.new_add_videoView);
            new_add_videoView.setMediaController(new MediaController(getApplicationContext()));
            new_add_videoView.setVideoURI(uri);
            if(multimedia_state == MULTIMEDIA_AUDIO){
                new_add_videoView.setBackground(getResources().getDrawable(
                        R.drawable.audio_background));
                multimedia_num++;
                type = "mp3";
            }
            else{
                multimedia_num++;
                type = "mp4";
            }
//            new_add_videoView.start();
        }
        try{
            object.put("uri", uri);
            object.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        multimedia_list.put(object);
        ImageView delete_icon = new_add_view.findViewById(R.id.multimedia_delete_icon);
        delete_icon.setOnClickListener((view) -> {
            multimedia_layout.removeView(new_add_view);
            multimedia_list.remove(multimedia_list.length()-1);
            multimedia_num--;
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

    private void initPopupWindow(View parent_view){
        View view = LayoutInflater.from(getApplicationContext()).inflate(
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
            if(checkValid(IMAGE)){
                selectMultimedia(MULTIMEDIA_IMAGE);
            }
        });
        audio_select.setOnClickListener(view1 ->{
            if(checkValid(AUDIO)){
                selectMultimedia(MULTIMEDIA_AUDIO);
            }
        });
        video_select.setOnClickListener(view1 -> {
            if(checkValid(VIDEO)){
                selectMultimedia(MULTIMEDIA_VIDEO);
            }
        });
        image_add.setOnClickListener(view1 -> {
            if(checkValid(IMAGE)){
                addMultimedia(MULTIMEDIA_IMAGE);
            }
        });
        audio_add.setOnClickListener(view1 -> {
            if(checkValid(AUDIO)){
                addMultimedia(MULTIMEDIA_AUDIO);
            }
        });
        video_add.setOnClickListener(view1 -> {
            if(checkValid(VIDEO)){
                addMultimedia(MULTIMEDIA_VIDEO);
            }
        });
    }

    private boolean checkValid(int type){
        if(type != share_type){
            multimedia_popupWindow.dismiss();
            Toast.makeText(getApplicationContext(), "请选择正确的动态类型", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if((type == AUDIO || type == VIDEO) && multimedia_num >= 1){
            multimedia_popupWindow.dismiss();
            Toast.makeText(getApplicationContext(), "最多添加一个音视频资源", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }
}
