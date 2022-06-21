package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class DraftRecyclerViewHolder extends RecyclerView.ViewHolder{

    public RecyclerView.Adapter my_adapter;

    public TextView title_textView;
    public TextView content_textView;
    public TextView date_textView;
    public TextView location_textView;
    public String draft_id;
    public String type;

    private FragmentActivity my_activity;

    public DraftRecyclerViewHolder(View itemView, RecyclerView.Adapter adapter,
                                   FragmentActivity activity) {
        super(itemView);
        my_adapter = adapter;
        my_activity = activity;
        content_textView = itemView.findViewById(R.id.draft_recycler_item_content);
        title_textView = itemView.findViewById(R.id.draft_recycler_item_title);
        date_textView = itemView.findViewById(R.id.share_time);
        location_textView = itemView.findViewById(R.id.share_location);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDraft();
            }
        });
    }

    private void editDraft(){
        Intent intent = new Intent(my_activity, DraftEditActivity.class);

        intent.putExtra("draft_title", title_textView.getText().toString());
        intent.putExtra("draft_content", content_textView.getText().toString());
        intent.putExtra("draft_id", draft_id);
        intent.putExtra("date", date_textView.getText().toString());
        intent.putExtra("location", location_textView.getText().toString());
        intent.putExtra("type", type);
        my_activity.startActivity(intent);
    }
}
