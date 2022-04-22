package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class UserRecyclerViewHolder extends RecyclerView.ViewHolder {
    public RecyclerView.Adapter my_adapter;

    public TextView nickName_textView;
    public Button browse_share_button;
    public Button follow_state_button;

    public UserRecyclerViewHolder(View itemView, RecyclerView.Adapter adapter) {
        super(itemView);
        my_adapter = adapter;
        nickName_textView = itemView.findViewById(R.id.user_browse_item_nickName);
        browse_share_button = itemView.findViewById(R.id.user_browse_share_button);
        follow_state_button = itemView.findViewById(R.id.user_follow_state_button);
    }
}
