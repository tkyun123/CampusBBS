package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShareBrowseDetailActivity extends AppCompatActivity {
    private int comment_span_state = 0;  // 0:unspanned, 1:spanned

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_browse_detail);

        Toolbar tool_bar = findViewById(R.id.share_detail_tool_bar);
        tool_bar.setTitle(R.string.title_activity_share_browse_detail);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view->{
            this.finish();
        });

        Intent intent = getIntent();

        TextView sharedPost_title = findViewById(R.id.sharedPost_title);
        sharedPost_title.setText(intent.getStringExtra("share_title"));

        TextView sharedPost_content = findViewById(R.id.sharedPost_content);
        sharedPost_content.setText(intent.getStringExtra("share_content"));

        getSupportFragmentManager().beginTransaction().replace(R.id.share_detail_comment,
                new CommentBrowse(new CommentBrowse.loadData() {
                    @Override
                    public void loadDataSortByTime(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("nickName","欧米牛坦");
                            data.put("content","评论(按时间)");
                            data_list.add(data);
                            load_num--;
                        }
                    }

                    @Override
                    public void loadDataSortByWave(List<Map<String, String>> data_list, int load_num) {
                        while(load_num>0){
                            Map<String, String> data = new HashMap<>();
                            data.put("nickName","欧米牛坦");
                            data.put("content","评论(按热度");
                            data_list.add(data);
                            load_num--;
                        }
                    }
                })).commit();

        LinearLayout comment_span_layout = findViewById(R.id.comment_span_layout);
        ImageView comment_span_state_icon = findViewById(R.id.comment_span_state_icon);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
                comment_span_layout.getLayoutParams();
        comment_span_layout.setOnClickListener(view -> {
            comment_span_state = 1-comment_span_state;
            if(comment_span_state == 0) {
                comment_span_state_icon.setImageResource(R.drawable.span_icon);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            else{
                comment_span_state_icon.setImageResource(R.drawable.unspan_icon);
                lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            comment_span_layout.setLayoutParams(lp);
        });

    }

}