package com.example.myapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class FloorListViewHolder extends RecyclerView.ViewHolder {
    public ImageView profile_photo_imageView;
    public TextView nickName_textView;
    public TextView content_textView;
    public TextView floor_num_textView;

    public TextView comment_show;
    public ImageView add_comment_button;
    public TextView location_textView;
    public TextView time_textView;
    public TextView like_num_textView;
    public ImageView like_icon;
    public LinearLayout floor_like_layout;

    public int user_id;
    public int like_state;

    public FloorListViewHolder(View itemView) {
        super(itemView);
        profile_photo_imageView = itemView.findViewById(R.id.floor_item_profile_photo);
        nickName_textView = itemView.findViewById(R.id.floor_item_nickName);
        content_textView = itemView.findViewById(R.id.floor_item_content);
        floor_num_textView = itemView.findViewById(R.id.floor_item_num);

        comment_show = itemView.findViewById(R.id.floor_item_comment_show);
        add_comment_button = itemView.findViewById(R.id.floor_item_add_comment);
        location_textView = itemView.findViewById(R.id.floor_item_location);
        time_textView = itemView.findViewById(R.id.floor_item_time);
        like_num_textView = itemView.findViewById(R.id.floor_item_like_num);
        floor_like_layout = itemView.findViewById(R.id.floor_like_layout);
        like_icon = itemView.findViewById(R.id.floor_item_like_icon);
    }

}
