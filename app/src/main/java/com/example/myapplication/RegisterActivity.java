package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_register);
        Toolbar tool_bar = findViewById(R.id.register_tool_bar);
        tool_bar.setTitle(R.string.title_activity_register);
        setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.back_icon);

        tool_bar.setNavigationOnClickListener(view -> {
            this.finish();
        });

        TextView user_input = findViewById(R.id.register_user_input);
        TextView password_input = findViewById(R.id.register_password_input);
        TextView email_input = findViewById(R.id.register_email_input);
        Button confirm_button = findViewById(R.id.register_confirm_button);
        confirm_button.setOnClickListener(view -> {
            String user = user_input.getText().toString();
            String password = password_input.getText().toString();
            String email = email_input.getText().toString();
            if(checkValid(user, password, email)){
                register(user, password, email);
            }
        });
    }

    private boolean checkValid(String nickName, String password, String email){
        if(nickName.equals("") || password.equals("") || email.equals("")){
            AlertDialog message = new AlertDialog.Builder(this)
                    .setMessage("输入不能为空").create();
            message.show();
            return false;
        }
        return true;
    }

    private void register(String nickName, String password, String email){
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what==-1){
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            }
        };
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                String result = HttpRequest.post("/API/register",
                        String.format("nickname=%s&password=%s&email=%s", nickName,
                                password, email), "form");
                Message message = new Message();
                if(result.equals("error")){
                    message.what = -1;
                }
                else{
                    message.what = 0;
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getInt("register_state")==0){
                            message.what=-1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.what=-1;
                    }
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}
