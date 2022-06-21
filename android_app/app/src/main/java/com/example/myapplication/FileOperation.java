package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

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

    public static void listFile(Context context, String path){
        File file = new File(context.getExternalFilesDir("").getPath()+path);
        if(!file.exists()){
            Log.d("", "文件不存在");
            return;
        }
        File[] files = file.listFiles();
        for(int i=0;i<files.length;i++){
            if(files[i].isFile()){
                Log.d("文件:", files[i].getPath());
            }
            else if(files[i].isDirectory()){
                Log.d("目录:", files[i].getName());
                listFile(context, "/"+files[i].getName());
            }
        }
    }

    public static Uri getUriFromFilepath(Context context, String path){
        Uri uri = FileProvider.getUriForFile(
                context,context.getPackageName()+".fileProvider",
                new File(path));
        return uri;
    }

    public static String generatePathFromSuffix(Context context, String dir,  String suffix){
        // dir eg: /tmp, /multimedia
        // suffix eg: .mp3,.jpg
        String path;
        path = String.format("%s/%s%s",context.getExternalFilesDir("").getPath()+dir,
                new Date().getTime(), suffix);
        return path;
    }

    public static String generatePath(Context context, String dir,  String file_name){
        String path;
        path = String.format("%s/%s",context.getExternalFilesDir("").getPath()+dir,
                file_name);
        return path;
    }

    public static void saveFileFromUrl(String url, String save_path){
        try {
            // 获取网络流
            final InputStream inputStream = new URL(url).openStream();
            File file = new File(save_path);
            if(!file.exists()){
                file.createNewFile();
            }
            else{
                inputStream.close();
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int n = 0;
            int len = 0;
            while((n = inputStream.read(buffer)) != -1){
                len += n;
                fileOutputStream.write(buffer, 0, n);
            }
            fileOutputStream.close();
            inputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
