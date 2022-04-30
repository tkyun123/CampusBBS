package com.example.myapplication;

import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class FloorListViewHolder extends RecyclerView.ViewHolder {
    public TextView nickName_textView;
    public TextView content_textView;
    public TextView floor_num_textView;
    public TextView comment_show;
    public ImageView add_comment_button;

    public FloorListViewHolder(View itemView) {
        super(itemView);
        nickName_textView = itemView.findViewById(R.id.floor_item_nickName);
        content_textView = itemView.findViewById(R.id.floor_item_content);
        floor_num_textView = itemView.findViewById(R.id.floor_item_num);
        comment_show = itemView.findViewById(R.id.floor_item_comment_show);
        add_comment_button = itemView.findViewById(R.id.floor_item_add_comment);
    }

}
