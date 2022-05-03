package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class FloorListAdapter extends RecyclerView.Adapter<FloorListViewHolder> {
    public LayoutInflater my_inflater;
    public List<Map<String,String>> data;
    public FragmentActivity my_activity;
//    private ActivityResultLauncher comment_add_launcher;

    public FloorListAdapter(Context context, List<Map<String,String>> list_data,
                            FragmentActivity activity) {
        my_inflater = LayoutInflater.from(context);
        data = list_data;
        my_activity = activity;

//        comment_add_launcher = my_activity.registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(), result -> {
//                    if(result.getResultCode() == Activity.RESULT_OK){
//                        Intent intent = result.getData();
//                        Log.d("", intent.getStringExtra("content"));
//                    }
//                });
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

        holder.add_comment_button.setOnClickListener(view -> {
            if(!SystemService.checkLogin(my_activity)){
                my_activity.startActivity(new Intent(my_activity, LoginActivity.class));
            }
            else{
                my_activity.startActivity(new Intent(my_activity, CommentAddActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
