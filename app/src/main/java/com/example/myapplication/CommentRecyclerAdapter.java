package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerViewHolder>{
    public LayoutInflater my_inflater;
    public JSONArray data;
    public FragmentActivity my_activity;
    public int comment_type; // 0:楼层的评论 1:自己的评论

    public CommentRecyclerAdapter(Context context, JSONArray list_data,
                               FragmentActivity activity, int type) {
        my_inflater = LayoutInflater.from(context);
        data = list_data;
        my_activity = activity;
        comment_type = type;
    }

    @NonNull
    @Override
    public CommentRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view;
        if(comment_type == 0){
            item_view = my_inflater.inflate(R.layout.comment_recycler_item, parent, false);
        }
        else{
            item_view = my_inflater.inflate(R.layout.my_comment_recycler_item, parent, false);

            LinearLayout comment_layout = item_view.findViewById(R.id.comment_layout);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)comment_layout.getLayoutParams();
            WindowManager window_mg = (WindowManager)my_activity.getSystemService(Context.WINDOW_SERVICE);
            Point size = new Point();
            window_mg.getDefaultDisplay().getSize(size);
            layoutParams.width = size.x;
            comment_layout.setLayoutParams(layoutParams);
        }

        return new CommentRecyclerViewHolder(item_view, comment_type);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerViewHolder holder, int position) {
        try {
            JSONObject object = data.getJSONObject(position);

            holder.nickName_textView.setText(object.getString("nickname"));
            holder.content_textView.setText(object.getString("text"));

            holder.location_textView.setText(object.getString("position"));
            Date date = new Date(object.getLong("time")*1000);
            holder.time_textView.setText(Consts.date_format.format(date));

            holder.user_id = object.getInt("uid");
            holder.pid = object.getInt("pid");
            holder.fid = object.getInt("fid");
            holder.cid = object.getInt("cid");

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
            holder.profile_photo.setOnClickListener(view -> {
                Intent intent = new Intent(my_activity, UserInfoActivity.class);
                intent.putExtra("user_id", holder.user_id);
                my_activity.startActivity(intent);
            });

            if(comment_type == Consts.COMMENT_USER){
                // 自己的评论，增加删除按钮
                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        if(msg.what == 0){
                            Toast.makeText(my_activity, "删除成功", Toast.LENGTH_SHORT)
                                    .show();
                            data.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                        }
                    }
                };
                holder.delete_icon.setOnClickListener(view -> {
                    deleteComment(holder.pid, holder.fid, holder.cid, handler);
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    private void deleteComment(int pid, int fid, int cid,
                               Handler handler){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try{
                    String result = HttpRequest.post("/API/del_comment",
                            String.format("pid=%s&fid=%s&cid=%s",pid,fid,cid),
                            "form");
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("state") == 1){
                        message.what = 0;
                    }
                    else{
                        message.what = -1;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}
