package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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

import java.io.IOException;
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
//                Toast.makeText(getContext(), "标题或内容不能为空", Toast.LENGTH_SHORT).show();
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
                getLocation();
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
            new_add_videoView.start();
        }

        ImageView delete_icon = new_add_view.findViewById(R.id.image_delete_icon);
        delete_icon.setOnClickListener((view) -> {
            multimedia_layout.removeView(new_add_view);
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

    public void getLocation() {
        // 模拟器调试存在问题
        LocationManager locationManager = (LocationManager) getContext().getSystemService(
                Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 设置相对省电
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);

        String best_provider = locationManager.getBestProvider(criteria, true);
        if (best_provider == null) {
            List<String> providers = locationManager.getProviders(true);
            if (providers != null && providers.size() > 0) {
                best_provider = providers.get(0);
            }
        }
        if (best_provider == null) {
            Log.d("", "未找到provider");
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("", "权限不支持");
            return;
        }
        Location location = locationManager.getLastKnownLocation(best_provider);
        if(location == null){
            Log.d("", "未找到location");
            return;
        }

        Geocoder geocoder = new Geocoder(getContext(), Locale.CHINESE);

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1
            );
            Address address = addresses.get(0);
            Log.d("地址:", address.getAddressLine(1));
        }catch (IOException e){
            Log.d("error:", String.valueOf(e));
        }

    }

}