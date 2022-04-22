package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ShareRecyclerViewHolder extends RecyclerView.ViewHolder {
    public RecyclerView.Adapter my_adapter;
    public TextView title_textView;
    public TextView content_textView;
    private FragmentActivity my_activity;

    public ShareRecyclerViewHolder(View itemView, RecyclerView.Adapter adapter,
                                   FragmentActivity activity) {
        super(itemView);
        my_adapter = adapter;
        my_activity = activity;
        title_textView = itemView.findViewById(R.id.share_browse_recycler_item_title);
        content_textView = itemView.findViewById(R.id.share_browse_recycler_item_content);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail();
            }
        });
    }


    private void showDetail(){
        Intent intent = new Intent(my_activity, ShareBrowseDetailActivity.class);

        intent.putExtra("share_title", title_textView.getText().toString());
        intent.putExtra("share_content", content_textView.getText().toString());
        my_activity.startActivity(intent);
    }
}
