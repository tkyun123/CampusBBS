package com.example.myapplication;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpRequest {
    private final static String BASE_URL = SystemService.getBaseUrl();

    public static String post(String url0, String data, String content_type){
        try{
            URL url = new URL(BASE_URL+url0);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setReadTimeout(3000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            if(content_type.equals("json")){
                httpURLConnection.setRequestProperty("Content-type",
                        "application/json;charset=UTF-8");
            }
            httpURLConnection.connect();

            DataOutputStream dataOutputStream = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dataOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            dataOutputStream.flush();
            dataOutputStream.close();

            int code = httpURLConnection.getResponseCode();
            if (code==200){
                final InputStream inputStream = httpURLConnection.getInputStream();
                String result = resolveResponseStream(inputStream);
                return result;
            }
            else{
                return "error";
            }
        } catch (IOException e){
            e.printStackTrace();
            return "error";
        }
    }

    public static String get(String url0){
        try{
            URL url = new URL(BASE_URL+url0);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setReadTimeout(3000);

            int code = httpURLConnection.getResponseCode();
            if (code==200){
                final InputStream inputStream = httpURLConnection.getInputStream();
                String result = resolveResponseStream(inputStream);
                return result;
            }
            else{
                return "error";
            }
        } catch (IOException e){
            e.printStackTrace();
            return "error";
        }
    }

    private static String resolveResponseStream(InputStream inputStream){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((str=bufferedReader.readLine())!=null){
                stringBuilder.append(str);
            }
            bufferedReader.close();
            inputStream.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
