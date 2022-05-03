package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfo#} factory method to
 * create an instance of this fragment.
 */
public class UserInfo extends Fragment {

    private int my_flag = 0; // 0表示个人主页，1表示他人主页
    private final static int FLAG_SELF = 0;
    private final static int FLAG_OTHER = 1;

    private ActivityResultLauncher login_launcher;

    public UserInfo(int flag) {
        // Required empty public constructor
        my_flag = flag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        Button login_button = view.findViewById(R.id.user_info_login_button);

        TextView nickName_textView = view.findViewById(R.id.user_info_nickName);
        if(my_flag == FLAG_SELF){
            login_launcher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            nickName_textView.setText(intent.getStringExtra("nickName"));
                            login_button.setVisibility(View.INVISIBLE);
                        }
                    });
            login_button.setOnClickListener((view1 -> {
                login_launcher.launch(new Intent(getActivity(), LoginActivity.class));
            }));
        }
        else{
            login_button.setVisibility(View.INVISIBLE);
        }

        return view;

    }
}