package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class UserRecyclerViewHolder extends RecyclerView.ViewHolder {
    public RecyclerView.Adapter my_adapter;

    public ImageView profile_photo;
    public TextView nickName_textView;
    public TextView introduction_textView;
    public Button browse_share_button;
    public Button follow_state_button;
    public Button block_state_button;

    public int user_id;
    public int my_relation;

    public UserRecyclerViewHolder(View itemView, RecyclerView.Adapter adapter) {
        super(itemView);
        my_adapter = adapter;
        profile_photo = itemView.findViewById(R.id.user_browse_item_profile_photo);
        nickName_textView = itemView.findViewById(R.id.user_browse_item_nickName);
        introduction_textView = itemView.findViewById(R.id.user_browse_item_introduction);
        browse_share_button = itemView.findViewById(R.id.user_browse_share_button);
        follow_state_button = itemView.findViewById(R.id.user_follow_state_button);
        block_state_button = itemView.findViewById(R.id.user_block_state_button);
    }
}
