package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class FileOperation {
    public static void createDir(Context context, String path){
        File file = new File(context.getExternalFilesDir("").getPath()+path);
        if(file.exists()){
            Log.d("", "文件夹已存在");
        }
        else{
            if(file.mkdir()){
                Log.d("", "创建文件夹成功");
            }
            else{
                Log.d("", "创建文件夹失败");
            }
        }
    }

    // 清空文件夹
    public static void clearDir(Context context, String path){
        File file = new File(context.getExternalFilesDir("").getPath()+path);
        if(file.exists()){
            File[] files = file.listFiles();
            for(int i=0;i<files.length;i++){
                if(files[i].delete()){
                    Log.d("", files[i].getPath()+"删除成功");
                }
                else{
                    Log.d("", files[i].getPath()+"删除失败");
                }
            }
        }
        else{
            Log.d("", "文件夹不存在");
        }
    }

    // 删除文件夹
    public static void deleteDir(Context context, String path){
        clearDir(context, path);
        File file = new File(context.getExternalFilesDir("").getPath()+path);
        if(file.exists()){
            if(file.delete()){
                Log.d("", file.getPath()+"删除成功");
            }
            else{
                Log.d("", file.getPath()+"删除失败");
            }
        }
    }

    public static Uri getUriFromFilepath(Context context, String path){
        Uri uri = FileProvider.getUriForFile(
                context,context.getPackageName()+".fileProvider",
                new File(path));
        return uri;
    }
}
