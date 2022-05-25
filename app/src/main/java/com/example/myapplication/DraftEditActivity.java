package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DraftEditActivity extends AppCompatActivity {

    TextView deleteButton;
    TextView postButton;
    TextView saveButton;

    String share_title;
    String share_content;

    EditText titleEdit;
    EditText contentEdit;

    int user_id;
    String draft_id;

    Boolean is_saved = false;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_edit);

        SharedPreferences loginShared = DraftEditActivity.this.getSharedPreferences(
                "login", Context.MODE_PRIVATE
        );
        user_id = loginShared.getInt("user_id", -1);

        sharedPreferences = DraftEditActivity.this.getSharedPreferences(
                "draft_" + user_id, Context.MODE_PRIVATE
        );

        Toolbar tool_bar = findViewById(R.id.draft_edit_tool_bar);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            if(!is_saved) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DraftEditActivity.this);
                builder.setMessage("是否保存修改？");
                builder.setCancelable(true);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        share_title = titleEdit.getText().toString();
                        share_content = contentEdit.getText().toString();
                        saveShare(share_title, share_content);
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                finish();
            }
        });

        titleEdit = findViewById(R.id.draft_edit_title);
        contentEdit = findViewById(R.id.draft_edit_content);

        deleteButton = findViewById(R.id.draft_edit_deleteButton);
        postButton = findViewById(R.id.draft_edit_postButton);
        saveButton = findViewById(R.id.draft_edit_saveButton);

        Intent intent = getIntent();
        titleEdit.setText(intent.getStringExtra("draft_title"));
        contentEdit.setText(intent.getStringExtra("draft_content"));
        draft_id = intent.getStringExtra("draft_id");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DraftEditActivity.this);
                builder.setMessage("确认删除该草稿？");
                builder.setCancelable(true);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("is_deleted" + draft_id, true);
                        editor.commit();

                        finish();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_title = titleEdit.getText().toString();
                share_content = contentEdit.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(DraftEditActivity.this);
                builder.setMessage("确认发布？");
                builder.setCancelable(true);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // To do
                        finish();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_title = titleEdit.getText().toString();
                share_content = contentEdit.getText().toString();
                saveShare(share_title, share_content);
                is_saved = true;
            }
        });

    }

    private void saveShare(String title, String content){
//        Date date = new Date();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("title" + draft_id, title);
        editor.putString("content" + draft_id, content);
        editor.commit();
    }
}
