package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;


public class ShareRecyclerAdapter extends RecyclerView.Adapter<ShareRecyclerViewHolder>
{
    public LayoutInflater my_inflater;
    public List<Map<String,String>> data;
    public FragmentActivity my_activity;
    public ShareRecyclerAdapter(Context context, List<Map<String,String>> list_data,
                                FragmentActivity activity) {
        my_inflater = LayoutInflater.from(context);
        data = list_data;
        my_activity = activity;
    }

    @NonNull
    @Override
    public ShareRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = my_inflater.inflate(R.layout.share_browse_recycler_item, parent, false);
        return new ShareRecyclerViewHolder(item_view,this, my_activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareRecyclerViewHolder holder, int position) {
            holder.title_textView.setText(data.get(position).get("title"));
            holder.content_textView.setText(data.get(position).get("content"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
