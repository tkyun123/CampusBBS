package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SharePost#} factory method to
 * create an instance of this fragment.
 */
public class SharePost extends Fragment {
    public SharePost() {
        // Required empty public constructor
    }

    private String share_title;
    private String share_content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_post, container, false);
        final EditText edit_share_title = view.findViewById(R.id.share_edit_title);
        final EditText edit_share_content = view.findViewById(R.id.share_edit_content);


        TextView share_post = view.findViewById(R.id.share_detail_postButton);

        share_post.setOnClickListener(view1 -> {
            share_title = edit_share_title.getText().toString();
            share_content = edit_share_content.getText().toString();
            if(share_title.equals("") || share_content.equals("")){
                AlertDialog message = new AlertDialog.Builder(getContext())
                        .setMessage("标题或内容不能为空").create();
                message.show();
//                Toast.makeText(getContext(), "标题或内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            edit_share_title.setText("");
            edit_share_content.setText("");
        });
        return view;
    }

}