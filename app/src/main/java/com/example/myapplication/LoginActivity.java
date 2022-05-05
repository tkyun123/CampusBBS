package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private String checkCode_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);
        Toolbar tool_bar = findViewById(R.id.login_tool_bar);
        tool_bar.setTitle(R.string.title_activity_login);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            this.setResult(RESULT_CANCELED);
            this.finish();
        });

        TextView login_to_register_button = findViewById(R.id.login_to_register_button);
        login_to_register_button.setOnClickListener(view -> {
            this.startActivity(new Intent(this, RegisterActivity.class));
        });

        TextView email_textView = findViewById(R.id.login_email_input);
        TextView password_textView = findViewById(R.id.login_password_input);
        Button login_confirm_button = findViewById(R.id.login_confirm_button);
        login_confirm_button.setOnClickListener(view -> {
            login(email_textView.getText().toString(),
                    password_textView.getText().toString());
;
        });

//        ImageView checkCode_imageView = findViewById(R.id.checkCode_imageView);
//        checkCode_imageView.setOnClickListener(view -> {
//
//        });
    }

    private void login(String email, String password){
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what != -1){
                    Intent data = new Intent();
                    data.putExtra("user_id", msg.getData().getInt("user_id"));
                    setResult(RESULT_OK, data);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
                else{
//                    AlertDialog message = new AlertDialog.Builder(
//                            LoginActivity.this).setMessage("登录失败").create();
//                    message.show();
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                String result = HttpRequest.post("/API/login",
                        String.format("password=%s&email=%s",password, email),
                        null);
                Message message = new Message();
                if(result.equals("error")){
                    message.what = -1;
                }
                else{
                    message.what = 0;
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getInt("login_state")==0){
                            message.what = -1;
                        }
                        else{
                            Bundle bundle = new Bundle();
                            bundle.putInt("user_id", jsonObject.getInt("uid"));
                            bundle.putString("token", jsonObject.getString("token"));
                            message.setData(bundle);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.what = -1;
                    }
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}