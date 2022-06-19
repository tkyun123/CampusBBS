package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ShareRecyclerViewHolder extends RecyclerView.ViewHolder {
    public RecyclerView.Adapter my_adapter;
    public TextView title_textView;
// 不展示内容    public TextView content_textView;
    public ImageView profile_photo_imageView;
    public TextView nickName_textView;
    public TextView follow_textView;
    public TextView location_textView;
    public TextView time_textView;
    public TextView like_num_textView;
    public ImageView like_icon;
    public LinearLayout share_like_layout;

    public int user_id;
    public int pid;
    public int like_state; // 是否点赞  0:未点赞;1:已点赞

    private FragmentActivity my_activity;

    public ShareRecyclerViewHolder(View itemView, RecyclerView.Adapter adapter,
                                   FragmentActivity activity) {
        super(itemView);
        my_adapter = adapter;
        my_activity = activity;
        title_textView = itemView.findViewById(R.id.share_browse_recycler_item_title);
        follow_textView = itemView.findViewById(R.id.share_browse_recycler_item_follow);
        profile_photo_imageView = itemView.findViewById(
                R.id.share_browse_recycler_item_profile_photo);
        nickName_textView = itemView.findViewById(R.id.share_browse_recycler_item_nickName);
        location_textView = itemView.findViewById(R.id.share_location);
        time_textView = itemView.findViewById(R.id.share_time);
        like_num_textView = itemView.findViewById(R.id.share_like_num);

        itemView.setOnClickListener(view -> showDetail());

        like_icon = itemView.findViewById(R.id.share_like_icon);
        share_like_layout = itemView.findViewById(R.id.share_like_layout);
    }


    private void showDetail(){
        Intent intent = new Intent(my_activity, ShareBrowseDetailActivity.class);

        intent.putExtra("pid", pid);
        intent.putExtra("title", title_textView.getText());
//        intent.putExtra("share_content", content_textView.getText().toString());
        my_activity.startActivity(intent);
    }


}
