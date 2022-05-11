package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ShareRecyclerAdapter extends RecyclerView.Adapter<ShareRecyclerViewHolder>
{
    public LayoutInflater my_inflater;
    public JSONArray data;
    public FragmentActivity my_activity;

    SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);

    public ShareRecyclerAdapter(Context context, JSONArray list_data,
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
        try {
            JSONObject object = data.getJSONObject(position);
            holder.nickName_textView.setText(object.getString("nickname"));
            holder.title_textView.setText(object.getString("title"));
            holder.location_textView.setText(object.getString("position"));

            Date date = new Date(object.getLong("post_time")*1000);
            holder.time_textView.setText(date_format.format(date));
            holder.like_num_textView.setText(String.valueOf(object.getInt("agree_count")));
            holder.pid = object.getInt("pid");
            holder.like_state = object.getInt("agree_state");
            if(holder.like_state == 1){
                holder.like_icon.setImageResource(R.drawable.like_icon);
            }

            Handler give_like_handle = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if(msg.what == 0){
                        holder.like_state = 1;
                        holder.like_icon.setImageResource(R.drawable.like_icon);
                        int like_num = Integer.valueOf(
                                holder.like_num_textView.getText().toString())+1;
                        holder.like_num_textView.setText(String.valueOf(like_num));
                    }
                }
            };

            Handler cancel_like_handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if(msg.what == 0){
                        holder.like_state = 0;
                        holder.like_icon.setImageResource(R.drawable.unlike_icon);
                        int like_num = Integer.valueOf(
                                holder.like_num_textView.getText().toString())-1;
                        holder.like_num_textView.setText(String.valueOf(like_num));
                    }
                }
            };

            holder.share_like_layout.setOnClickListener(view -> {
                Handler handler;
                if(holder.like_state == 0){
                    handler = give_like_handle;
                }
                else{
                    handler = cancel_like_handler;
                }
                ShareOperation.change_like_state(SystemService.getUserId(my_activity),
                        holder.pid, 0, handler, holder.like_state);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }
}
