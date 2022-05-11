package com.example.myapplication;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapplication.datatype.UserInfoStorage;

public class BasicInfoModify extends Fragment {
    private UserInfoStorage userInfoStorage = new UserInfoStorage();
    private String nickName = "";
    private String introduction = "";
    private Uri my_uri;

    private ActivityResultLauncher image_select_launcher;

    public BasicInfoModify() {
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
        View view = inflater.inflate(R.layout.fragment_basic_info_modify, container, false);

        ImageView profile_photo_imageView = view.findViewById(
                R.id.info_modify_profile_photo_imageView);
        Button profile_photo_change_button = view.findViewById(
                R.id.info_modify_profile_photo_button);
        EditText nickName_input_editText = view.findViewById(
                R.id.info_modify_nickName_editText);
        EditText introduction_input_editText = view.findViewById(
                R.id.info_modify_introduction_editText);
        Button confirm_button = view.findViewById(
                R.id.basic_info_modify_confirm_button);

        SystemService.getInfo(getActivity(),userInfoStorage);
        nickName_input_editText.setText(userInfoStorage.nickName);
        introduction_input_editText.setText(userInfoStorage.introduction);

        confirm_button.setOnClickListener(view1 -> {
            nickName = nickName_input_editText.getText().toString();
            introduction = introduction_input_editText.getText().toString();
            if(checkValid()){
                changeBasicInfo();
            }
        });

        init_launcher();
        profile_photo_change_button.setOnClickListener(view1 -> {
            image_select_launcher.launch("image/*");
        });
        return view;
    }

    private boolean checkValid(){
        if(nickName.equals("") || introduction.equals("")){
            AlertDialog message = new AlertDialog.Builder(getContext())
                    .setMessage("输入不能为空").create();
            message.show();
            return false;
        }
        if(nickName.equals(userInfoStorage.nickName) && introduction.equals(
                userInfoStorage.introduction)){
            AlertDialog message = new AlertDialog.Builder(getContext())
                    .setMessage("未修改信息").create();
            message.show();
            return false;
        }
        return true;
    }

    private void init_launcher(){
        image_select_launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), (uri) -> {
                    if (uri != null) {
                        my_uri = uri;
                    }
                });
    }

    private void changeBasicInfo(){ // to do
    }
}