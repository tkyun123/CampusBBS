package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerViewHolder>{
    public LayoutInflater my_inflater;
    public List<Map<String,String>> data;
    public FragmentActivity my_activity;
    public CommentRecyclerAdapter(Context context, List<Map<String,String>> list_data,
                               FragmentActivity activity) {
        my_inflater = LayoutInflater.from(context);
        data = list_data;
        my_activity = activity;
    }

    @NonNull
    @Override
    public CommentRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = my_inflater.inflate(R.layout.comment_recycler_item, parent, false);

        LinearLayout comment_layout = item_view.findViewById(R.id.message_layout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)comment_layout.getLayoutParams();
        WindowManager window_mg = (WindowManager)my_activity.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        window_mg.getDefaultDisplay().getSize(size);
        layoutParams.width = size.x;
        comment_layout.setLayoutParams(layoutParams);
        return new CommentRecyclerViewHolder(item_view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerViewHolder holder, int position) {
        holder.nickName_textView.setText(data.get(position).get("nickName"));
        holder.content_textView.setText(data.get(position).get("content"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
