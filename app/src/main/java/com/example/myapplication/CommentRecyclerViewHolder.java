package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class CommentRecyclerViewHolder extends RecyclerView.ViewHolder{

    public ImageView profile_photo;
    public TextView nickName_textView;
    public TextView content_textView;
    public TextView location_textView;
    public TextView time_textView;
    public ImageView delete_icon;

    public int user_id; // 评论者uid
    public int pid;
    public int fid;
    public int cid;

    public CommentRecyclerViewHolder(View itemView, int type) {
        super(itemView);
        profile_photo = itemView.findViewById(R.id.comment_recycler_item_profile_photo);
        content_textView = itemView.findViewById(R.id.comment_recycler_item_content);
        nickName_textView = itemView.findViewById(R.id.comment_recycler_item_nickName);
        location_textView = itemView.findViewById(R.id.comment_location);
        time_textView = itemView.findViewById(R.id.comment_time);
        if(type == 1){
            delete_icon = itemView.findViewById(R.id.comment_delete_icon);
        }
    }
}
