package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MessageRecyclerViewHolder extends RecyclerView.ViewHolder {
    public RecyclerView.Adapter my_adapter;

    public TextView content_textView;
    public TextView time_textView;
//    public ImageView delete_icon;

    public MessageRecyclerViewHolder(View itemView, RecyclerView.Adapter adapter) {
        super(itemView);
        my_adapter = adapter;
        content_textView = itemView.findViewById(R.id.message_content_textView);
        time_textView = itemView.findViewById(R.id.message_time_textView);
//        delete_icon = itemView.findViewById(R.id.message_delete_icon);
    }
}
