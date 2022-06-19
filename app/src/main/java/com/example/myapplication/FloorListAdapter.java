package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class FloorListAdapter extends RecyclerView.Adapter<FloorListViewHolder> {
    public LayoutInflater my_inflater;
    public JSONArray data;
    public Activity my_activity;
    private int my_pid;
//    private ActivityResultLauncher comment_add_launcher;

    public FloorListAdapter(Context context, JSONArray list_data,
                            Activity activity) {
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
        try {
            JSONObject object = data.getJSONObject(position);
            holder.nickName_textView.setText(object.getString("nickname"));
            holder.content_textView.setText(object.getString("text"));
            Date date = new Date(object.getLong("time")*1000);

            holder.location_textView.setText(object.getString("position"));
            holder.time_textView.setText(Consts.date_format.format(date));
            int fid = object.getInt("fid");
            holder.floor_num_textView.setText(String.format("第%s楼", fid));
            holder.like_num_textView.setText(String.valueOf(
                    object.getInt("agree_count")));
            holder.like_state = object.getInt("agree_state");

            holder.user_id = object.getInt("uid");

            String url = object.getString("pic_url");
            if(!url.equals("null")){
                url = SystemService.getBaseUrl()+url;
                Handler img_handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        Bundle bundle = msg.getData();
                        holder.profile_photo_imageView.setImageBitmap(bundle.getParcelable("image"));
                    }
                };
                SystemService.getImage(url, img_handler);
            }

            if(holder.user_id != SystemService.getUserId(my_activity)){
                holder.profile_photo_imageView.setOnClickListener(view -> {
                    Intent intent = new Intent(my_activity, UserInfoActivity.class);
                    intent.putExtra("user_id", holder.user_id);
                    my_activity.startActivity(intent);
                });
            }

            if(holder.user_id != SystemService.getUserId(my_activity)){
                holder.profile_photo_imageView.setOnClickListener(view -> {
                    Intent intent = new Intent(my_activity, UserInfoActivity.class);
                    intent.putExtra("user_id", holder.user_id);
                    my_activity.startActivity(intent);
                });
            }

            holder.comment_show.setOnClickListener(view -> {
                Intent intent = new Intent(my_activity, CommentActivity.class);
                intent.putExtra("pid", my_pid);
                intent.putExtra("fid", fid);
                intent.putExtra("comment_type", Consts.COMMENT_FLOOR);
                my_activity.startActivity(intent);
            });

            holder.add_comment_button.setOnClickListener(view -> {
                Intent intent = new Intent(my_activity, CommentAddActivity.class);
                intent.putExtra("pid", my_pid);
                intent.putExtra("fid", fid);
                my_activity.startActivity(intent);
            });

            if(holder.like_state == 1){
                holder.like_icon.setImageResource(R.drawable.like_icon);
            }
            else{
                holder.like_icon.setImageResource(R.drawable.unlike_icon);
            }

            Handler give_like_handle = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if(msg.what == 0){
                        holder.like_state = 1;
                        holder.like_icon.setImageResource(R.drawable.like_icon);
                        int like_num = Integer.parseInt(
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
                        int like_num = Integer.parseInt(
                                holder.like_num_textView.getText().toString())-1;
                        holder.like_num_textView.setText(String.valueOf(like_num));
                    }
                }
            };

            holder.floor_like_layout.setOnClickListener(view -> {
                Handler handler;
                if(holder.like_state == 0){
                    handler = give_like_handle;
                }
                else{
                    handler = cancel_like_handler;
                }
                ShareOperation.change_like_state(SystemService.getUserId(my_activity),
                        my_pid, position+1, handler, holder.like_state);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    public void setPid(int pid){
        my_pid = pid;
    }

}
