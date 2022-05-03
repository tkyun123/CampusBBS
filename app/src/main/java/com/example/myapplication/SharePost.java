package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class SharePost extends Fragment {
    private ScrollView scrollView;
    private String share_title;
    private String share_content;
    private EditText edit_share_title;
    private EditText edit_share_content;
    private LinearLayout multimedia_layout;
    View my_view;

    private final int IMAGE_PICK = 1;
    private ActivityResultLauncher launcher;
    private int multimedia_state = 0; // 0表示选择图片，1表示选择视频

    private int image_num = 0;
    private int video_num = 0;
    private List<Uri> uri_list = new ArrayList<>();

    private String location = "";

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

        TextView share_post = view.findViewById(R.id.share_detail_postButton);

        share_post.setOnClickListener(view1 -> {
            share_title = edit_share_title.getText().toString();
            share_content = edit_share_content.getText().toString();
            if (share_title.equals("") || share_content.equals("")) {
                AlertDialog message = new AlertDialog.Builder(getContext())
                        .setMessage("标题或内容不能为空").create();
                message.show();
                return;
            }
            edit_share_title.setText("");
            edit_share_content.setText("");
        });

        ImageView image_add_icon = view.findViewById(R.id.share_post_image_add_icon);
        image_add_icon.setOnClickListener(view1 -> addMultimedia(0));
        ImageView video_add_icon = view.findViewById(R.id.share_post_video_add_icon);
        video_add_icon.setOnClickListener(view1 -> addMultimedia(1));
        multimedia_layout = my_view.findViewById(R.id.share_post_new_add_multimedia_layout);

        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), (uri) -> {
                    if (uri != null) {
                        addMultimediaView(uri);
                    }
                });

        SwitchCompat location_switch = view.findViewById(R.id.show_location_switch);
        TextView location_textView = view.findViewById(R.id.show_location_textView);
        location_switch.setOnClickListener(view1 -> {
            if (location_switch.isChecked()) {
                location_textView.setText(R.string.show_location_text);
                if(location.equals("")){
                    location = SystemService.getLocation(getActivity().getApplicationContext());
                }
            } else {
                location_textView.setText(R.string.not_show_location_text);
            }
        });

        return view;
    }


    public void addMultimedia(int state) {
        multimedia_state = state;
        if (multimedia_state == 0) {
            launcher.launch("image/*");
        } else {
            launcher.launch("video/*");
        }
    }

    public void addMultimediaView(Uri uri) {
        View new_add_view;
        if (multimedia_state == 0) {
            image_num++;
            new_add_view = LayoutInflater.from(this.getContext()).inflate(
                    R.layout.image_add_item, null
            );
            multimedia_layout.addView(new_add_view);
            ImageView new_add_imageView = new_add_view.findViewById(R.id.new_add_imageView);
            new_add_imageView.setImageURI(uri);

        } else {
            video_num++;
            new_add_view = LayoutInflater.from(this.getContext()).inflate(
                    R.layout.video_add_item, null
            );
            multimedia_layout.addView(new_add_view);
            VideoView new_add_videoView = new_add_view.findViewById(R.id.new_add_videoView);
            new_add_videoView.setMediaController(new MediaController(this.getContext()));
            new_add_videoView.setVideoURI(uri);
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

}