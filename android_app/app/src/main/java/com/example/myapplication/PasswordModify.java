package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordModify extends Fragment {

    private String origin_password = "";
    private String first_input_password = "";
    private String second_input_password = "";

    private final static int PASSWORD_MODIFIED = 3;
    public PasswordModify() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_modify, container, false);

        Button confirm_button = view.findViewById(R.id.info_modify_password_confirm_button);
        EditText origin_password_editText = view.findViewById(
                R.id.info_modify_origin_password_editText);
        EditText first_input_editText = view.findViewById(
                R.id.info_modify_origin_password_first_input);
        EditText second_input_editText = view.findViewById(
                R.id.info_modify_origin_password_second_input);
        confirm_button.setOnClickListener(view1 -> {
            origin_password = origin_password_editText.getText().toString();
            first_input_password = first_input_editText.getText().toString();
            second_input_password = second_input_editText.getText().toString();
            if(checkValid()){
                changePassword();
            }
        });
        return view;
    }

    private boolean checkValid(){
        if(origin_password.equals("") || first_input_password.equals("") ||
            second_input_password.equals("")){
            AlertDialog message = new AlertDialog.Builder(getContext())
                    .setMessage("?????????????????????").create();
            message.show();
            return false;
        }
        if(!second_input_password.equals(first_input_password)){
            AlertDialog message = new AlertDialog.Builder(getContext())
                    .setMessage("?????????????????????????????????").create();
            message.show();
            return false;
        }
        return true;
    }

    private void changePassword(){ // to do
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0) {
                    getActivity().setResult(PASSWORD_MODIFIED);
                    Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT)
                            .show();
                }
                else if(msg.what == -1){
                    Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    int user_id = SystemService.getUserId(getActivity());
                    String result = HttpRequest.post("/API/password_change",
                            String.format("uid=%s&new_password=%s&old_password=%s",
                                    user_id,first_input_password, origin_password),
                            "form");
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("state") == 1){
                        message.what = 0;
                    }
                    else{
                        message.what = -1;
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}