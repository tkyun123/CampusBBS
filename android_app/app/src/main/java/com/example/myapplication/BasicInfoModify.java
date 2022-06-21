package com.example.myapplication;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BasicInfoModify extends Fragment {
    private String my_nickName = "";
    private String my_introduction = "";
    private String my_photo_path;
    private Uri new_uri = null;
    private int user_id;

    private final static int BASIC_INFO_MODIFIED = 2;

    private ActivityResultLauncher image_select_launcher;

    ImageView profile_photo_imageView;

    public BasicInfoModify(String nickName, String introduction, String photo_path) {
        // Required empty public constructor
        my_nickName = nickName;
        my_introduction = introduction;
        my_photo_path = photo_path;
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

        user_id = SystemService.getUserId(getActivity());

        profile_photo_imageView = view.findViewById(
                R.id.info_modify_profile_photo_imageView);
        Button profile_photo_change_button = view.findViewById(
                R.id.info_modify_profile_photo_button);
        EditText nickName_input_editText = view.findViewById(
                R.id.info_modify_nickName_editText);
        EditText introduction_input_editText = view.findViewById(
                R.id.info_modify_introduction_editText);
        Button confirm_button = view.findViewById(
                R.id.basic_info_modify_confirm_button);

        nickName_input_editText.setText(my_nickName);
        introduction_input_editText.setText(my_introduction);

        if(my_photo_path.equals("default")){
            profile_photo_imageView.setImageResource(R.drawable.default_profile_photo);
        }
        else{
            Handler handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    profile_photo_imageView.setImageBitmap(
                            msg.getData().getParcelable("image"));
                }
            };
            SystemService.getImage(my_photo_path, handler);
        }

        confirm_button.setOnClickListener(view1 -> {
            String nickName = nickName_input_editText.getText().toString();
            String introduction = introduction_input_editText.getText().toString();
            if(checkValid(nickName, introduction)){
                modifyBasicInfo(nickName, introduction);
            }
        });

        init_launcher();
        profile_photo_change_button.setOnClickListener(view1 -> {
            image_select_launcher.launch("image/*");
        });
        return view;
    }

    private boolean checkValid(String nickName, String introduction){
        if(nickName.equals(my_nickName) && introduction.equals(my_introduction)
             && new_uri == null){
            AlertDialog message = new AlertDialog.Builder(getContext())
                    .setMessage("未进行修改").create();
            message.show();
            return false;
        }

        if(nickName.equals("") || introduction.equals("")){
            AlertDialog message = new AlertDialog.Builder(getContext())
                    .setMessage("输入不能为空").create();
            message.show();
            return false;
        }
        return true;
    }

    private void init_launcher(){
        image_select_launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), (uri) -> {
                    if (uri != null) {
                        new_uri = uri;
                        profile_photo_imageView.setImageURI(uri);
                    }
                });
    }

    private void modifyBasicInfo(String nickName, String introduction){ // to do
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0) {
                    getActivity().setResult(BASIC_INFO_MODIFIED);
                    Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT)
                            .show();
                };
            }
        };

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    String result = HttpRequest.post("/API/info_change",
                            String.format("uid=%s&nickname=%s&intro=%s",
                                    user_id, nickName, introduction),
                            "form");
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("user_info_state") == 1){
                        message.what = 0;
                    }
                    else{
                        message.what = -1;
                    }

                    if(new_uri != null){
                        JSONObject object = new JSONObject();
                        JSONArray image_list = new JSONArray();
                        JSONObject image_object = new JSONObject();
                        object.put("uid", user_id);
                        image_object.put("mul_type", "pic");
                        image_object.put("filename", String.format("%s.jpg",user_id));
                        image_object.put("data",
                                Codec.imageUriToBase64(new_uri, getActivity().getContentResolver(),
                                        true));
                        image_list.put(image_object);
                        object.put("source_data", image_list);


                        String profile_photo_result = HttpRequest.post("/API/update",
                                object.toString(), "json");
                        JSONObject profile_photo_object = new JSONObject(profile_photo_result);
                        if(profile_photo_object.getInt("update_state") == 1
                            && message.what == 0){
                            message.what = 0;
                        }
                        else{
                            message.what = -1;
                        }
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