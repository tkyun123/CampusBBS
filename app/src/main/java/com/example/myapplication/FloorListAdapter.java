package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class FloorListAdapter extends RecyclerView.Adapter<FloorListViewHolder> {
    public LayoutInflater my_inflater;
    public List<Map<String,String>> data;
    public FragmentActivity my_activity;
    public FloorListAdapter(Context context, List<Map<String,String>> list_data,
                            FragmentActivity activity) {
        my_inflater = LayoutInflater.from(context);
        data = list_data;
        my_activity = activity;
    }

    @NonNull
    @Override
    public FloorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = my_inflater.inflate(R.layout.share_detail_floor_item, parent, false);
        return new FloorListViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull FloorListViewHolder holder, int position) {
        holder.nickName_textView.setText(data.get(position).get("nickName"));
        holder.content_textView.setText(data.get(position).get("content"));
        holder.floor_num_textView.setText("第"+(position+1)+"楼");
        holder.comment_show.setOnClickListener(view -> {
            Intent intent = new Intent(my_activity, CommentActivity.class);
            my_activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
