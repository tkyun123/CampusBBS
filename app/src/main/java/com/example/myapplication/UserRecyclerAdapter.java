package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerViewHolder> {
    public LayoutInflater my_inflater;
    public JSONArray data;
    public FragmentActivity my_activity;
    public UserRecyclerAdapter(Context context, JSONArray list_data,
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
        try {
            JSONObject object = data.getJSONObject(position);
            holder.nickName_textView.setText(object.getString("nickname"));
            holder.introduction_textView.setText(object.getString("intro"));
            holder.user_id = object.getInt("uid");
            int relation= object.getInt("relation");

            if(relation == -1){
                holder.my_relation = Consts.RELATION_NONE;
                holder.follow_state_button.setText(R.string.follow);
                holder.block_state_button.setText(R.string.block);
            }
            else if(relation == 1){
                holder.my_relation = Consts.RELATION_FOLLOW;
                holder.follow_state_button.setText(R.string.unfollow);
                holder.block_state_button.setText(R.string.block);
            }
            else {
                holder.my_relation = Consts.RELATION_BLOCK;
                holder.follow_state_button.setText(R.string.follow);
                holder.block_state_button.setText(R.string.unblock);
            }

            String url = object.getString("pic_url");
            if(!url.equals("null")){
                url = SystemService.getBaseUrl()+url;
                Handler img_handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        Bundle bundle = msg.getData();
                        holder.profile_photo.setImageBitmap(bundle.getParcelable("image"));
                    }
                };
                SystemService.getImage(url, img_handler);
            }

            Handler relation_change_handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if(msg.what == 0){
                        holder.my_relation = msg.arg1;
                        if(holder.my_relation == Consts.RELATION_FOLLOW){
                            holder.follow_state_button.setText(R.string.unfollow);
                            holder.block_state_button.setText(R.string.block);
                        }
                        else if(holder.my_relation == Consts.RELATION_BLOCK){
                            holder.follow_state_button.setText(R.string.follow);
                            holder.block_state_button.setText(R.string.unblock);
                        }
                        else{
                            holder.follow_state_button.setText(R.string.follow);
                            holder.block_state_button.setText(R.string.block);
                        }
                    }
                }
            };
            holder.follow_state_button.setOnClickListener(view -> {
                int new_relation;
                if(holder.my_relation != Consts.RELATION_FOLLOW){
                    new_relation = Consts.RELATION_FOLLOW;
                }
                else{
                    new_relation = Consts.RELATION_NONE;
                }
                ShareOperation.change_relation(SystemService.getUserId(my_activity), holder.user_id,
                        relation_change_handler, new_relation);
            });
            holder.block_state_button.setOnClickListener(view -> {
                int new_relation;
                if(holder.my_relation != Consts.RELATION_BLOCK){
                    new_relation = Consts.RELATION_BLOCK;
                }
                else{
                    new_relation = Consts.RELATION_NONE;
                };
                ShareOperation.change_relation(SystemService.getUserId(my_activity), holder.user_id,
                        relation_change_handler, new_relation);
            });

            holder.browse_share_button.setOnClickListener(view -> {
                Intent intent = new Intent(my_activity, UserShareActivity.class);
                intent.putExtra("user_id", holder.user_id);
                my_activity.startActivity(intent);
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
