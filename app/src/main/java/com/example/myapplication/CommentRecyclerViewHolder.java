package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class CommentRecyclerViewHolder extends RecyclerView.ViewHolder{
    public RecyclerView.Adapter my_adapter;

    public TextView nickName_textView;
    public TextView content_textView;

    public CommentRecyclerViewHolder(View itemView, RecyclerView.Adapter adapter) {
        super(itemView);
        my_adapter = adapter;
        content_textView = itemView.findViewById(R.id.comment_recycler_item_content);
        nickName_textView = itemView.findViewById(R.id.comment_recycler_item_nickName);
    }
}
