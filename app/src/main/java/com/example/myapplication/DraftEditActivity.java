package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DraftEditActivity extends AppCompatActivity {

    TextView deleteButton;
    TextView postButton;

    String share_title;
    String share_content;

    EditText titleEdit;
    EditText contentEdit;

    int user_id;
    String draft_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_edit);

        Toolbar tool_bar = findViewById(R.id.draft_edit_tool_bar);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            finish();
        });

        titleEdit = findViewById(R.id.draft_edit_title);
        contentEdit = findViewById(R.id.draft_edit_content);

        deleteButton = findViewById(R.id.draft_edit_deleteButton);
        postButton = findViewById(R.id.draft_edit_postButton);

        Intent intent = getIntent();
        titleEdit.setText(intent.getStringExtra("draft_title"));
        contentEdit.setText(intent.getStringExtra("draft_content"));
        draft_id = intent.getStringExtra("draft_id");

        SharedPreferences loginShared = DraftEditActivity.this.getSharedPreferences(
                "login", Context.MODE_PRIVATE
        );
        user_id = loginShared.getInt("user_id", -1);
        SharedPreferences sharedPreferences = DraftEditActivity.this.getSharedPreferences(
                "draft_" + user_id, Context.MODE_PRIVATE
        );

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DraftEditActivity.this);
                builder.setMessage("确认删除该草稿？");
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


    }
}
