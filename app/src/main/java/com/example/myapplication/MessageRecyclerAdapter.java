package com.example.myapplication;

import android.content.Context;
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

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerViewHolder> {
    public LayoutInflater my_inflater;
    public List<Map<String,String>> data;
    public FragmentActivity my_activity;

    public MessageRecyclerAdapter(Context context, List<Map<String,String>> list_data,
                                  FragmentActivity activity) {
        my_inflater = LayoutInflater.from(context);
        data = list_data;
        my_activity = activity;
    }
    @NonNull
    @Override
    public MessageRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = my_inflater.inflate(R.layout.message_recycler_item, parent, false);

        LinearLayout message_layout = itemView.findViewById(R.id.message_layout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)message_layout.getLayoutParams();
        WindowManager window_mg = (WindowManager)my_activity.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        window_mg.getDefaultDisplay().getSize(size);
        layoutParams.width = size.x;
        message_layout.setLayoutParams(layoutParams);
        return new MessageRecyclerViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecyclerViewHolder holder, int position) {
        holder.content_textView.setText(data.get(position).get("content"));
        holder.time_textView.setText(data.get(position).get("time"));
        holder.delete_icon.setOnClickListener(view -> {
            data.remove(position);
            this.notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
