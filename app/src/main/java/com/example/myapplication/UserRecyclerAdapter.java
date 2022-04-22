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

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerViewHolder> {
    public LayoutInflater my_inflater;
    public List<Map<String,String>> data;
    public FragmentActivity my_activity;
    public UserRecyclerAdapter(Context context, List<Map<String,String>> list_data,
                                FragmentActivity activity) {
        my_inflater = LayoutInflater.from(context);
        data = list_data;
        my_activity = activity;
    }

    @NonNull
    @Override
    public UserRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = my_inflater.inflate(R.layout.user_recycler_item, parent, false);
        return new UserRecyclerViewHolder(item_view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerViewHolder holder, int position) {
        holder.nickName_textView.setText(data.get(position).get("nickName"));
        holder.browse_share_button.setOnClickListener(view -> {
            Intent intent = new Intent(my_activity, UserShareActivity.class);
            my_activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
