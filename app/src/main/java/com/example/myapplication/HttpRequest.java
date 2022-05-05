package com.example.myapplication;

import android.util.JsonReader;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.io.IOException;

public class HttpRequest {
    private final static String const_url = "";

    public static String post(String url0, String data){
        try{
            URL url = new URL(url0);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setReadTimeout(3000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            DataOutputStream dataOutputStream = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dataOutputStream.write(data.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            int code = httpURLConnection.getResponseCode();
            if (code==200){
                final InputStream inputStream = httpURLConnection.getInputStream();
                return resolveResponseStream(inputStream);
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
            URL url = new URL(url0);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setReadTimeout(3000);

            int code = httpURLConnection.getResponseCode();
            if (code==200){
                final InputStream inputStream = httpURLConnection.getInputStream();
                return resolveResponseStream(inputStream);
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
            StringBuffer stringBuffer = new StringBuffer();
            String str=null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((str=bufferedReader.readLine())!=null){
                stringBuffer.append(str);
            }
            bufferedReader.close();
            inputStream.close();
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
}