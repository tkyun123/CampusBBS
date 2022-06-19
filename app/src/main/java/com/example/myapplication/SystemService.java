package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.example.myapplication.datatype.UserInfoStorage;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class SystemService {
    public static final String DEFAULT_LOCATION = "地球";

    // 高德地图Api key
    private final static String LOCATION_API_KEY = "a87e48cae9a216077a37c492ed0c591e";

    public static void getLocation(Context context, Handler handler) {
            Message message = new Message();

            LocationManager locationManager = (LocationManager) context.getSystemService(
                    Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW); // 设置相对省电
            criteria.setAltitudeRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(false);

            String best_provider = locationManager.getBestProvider(criteria, true);
            List<String> providers = locationManager.getProviders(true);
            if (best_provider == null) {
                if (providers != null && providers.size() > 0) {
                    best_provider = providers.get(0);
                }
            }
            if (best_provider == null) {
                Log.d("", "未找到provider");
                message.what = -1;
                handler.sendMessage(message);
                return;
            }


            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("", "权限不支持");
                message.what = -1;
                handler.sendMessage(message);
                return;
            }
            Location location = locationManager.getLastKnownLocation(best_provider);
            if(location == null){
                // 若best_provider(一般是gps)定位失败，则使用网络定位
                location = locationManager.getLastKnownLocation("network");
            }
            if(location == null){
                // 若网络定位也失败，则使用best_provider请求位置更新
                Log.d("", "未找到location");
                locationManager.requestLocationUpdates(best_provider, 3600 * 1000,
                        100, location1 -> {
                            if (location1 != null) {
                                getFromLocation(context, location1, handler);
                            }
                        });
            }
            else{
                getFromLocation(context, location, handler);
            }
    }

    private static void getFromLocation(Context context, Location location, Handler handler){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    ServiceSettings.getInstance().setApiKey(LOCATION_API_KEY);
                    ServiceSettings.updatePrivacyShow(context, true, true);
                    ServiceSettings.updatePrivacyAgree(context, true);

                    GeocodeSearch geocodeSearch = new GeocodeSearch(context);

                    Log.d("Location", String.format("latitude:%s, longitude:%s",
                            location.getLatitude(), location.getLongitude()));

//                    LatLonPoint latLonPoint = new LatLonPoint(
//                            39.92, 116.46);
                    LatLonPoint latLonPoint = new LatLonPoint(
                            location.getLatitude(), location.getLongitude());
                    RegeocodeQuery regeocodeQuery = new RegeocodeQuery(
                            latLonPoint, 25, GeocodeSearch.GPS);
                    RegeocodeAddress address = geocodeSearch.getFromLocation(regeocodeQuery);
                    Log.d("Address:", address.getFormatAddress());
                    Bundle bundle = new Bundle();
                    bundle.putString("location", address.getProvince());
                    message.what = 0;
                    message.setData(bundle);
                }catch (AMapException e){
                    e.printStackTrace();
                    message.what = -1;
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    public static boolean checkLogin(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1) != -1;
    }

    public static void addInfo(Activity activity, UserInfoStorage user){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", user.token);
        editor.putInt("user_id", user.user_id);
//        editor.putString("nickName", user.nickName);
//        editor.putString("introduction", user.introduction);
//        editor.putString("profile_photo_url", user.profile_photo_url);
        editor.apply();
    }

    public static void clearInfo(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static int getUserId(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1);
    }

    public static void clearJsonArray(JSONArray jsonArray){
        while(jsonArray.length()>0){
            jsonArray.remove(0);
        }
    }

    // 保持原图比例进行缩放
    public static Bitmap bitmapResize(Bitmap bitmap, int n_width, int n_height){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width <= n_width && height <= n_height){
            return bitmap;
        }
        float scale = (float) n_width / width;
        float height_scale = (float) n_height / height;
        if(height_scale < scale){
            scale = height_scale;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static String getBaseUrl(){
        return "http://183.172.180.32:5000";
    }

    public static Bitmap getBitmapFromUrl(String url){
        try{
            final InputStream inputStream = new URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void getImage(String url, Handler handler){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                Bitmap bitmap = getBitmapFromUrl(url);
                Bundle bundle = new Bundle();
                bundle.putParcelable("image", bitmap);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }


    // 根据url获取网络流并将数据保存在本地，返回文件路径uri
    public static void getVideo(Context context, String url, String file_name, Handler handler){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                String save_path = FileOperation.generatePath(context, "/tmp", file_name);
                FileOperation.saveFileFromUrl(url, save_path);
                Bundle bundle = new Bundle();
                bundle.putParcelable("uri", FileOperation.getUriFromFilepath(context, save_path));
                message.setData(bundle);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    public static void getAudio(Context context, String url,  String file_name, Handler handler){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                String save_path = FileOperation.generatePath(context, "/tmp", file_name);
                FileOperation.saveFileFromUrl(url, save_path);
                Bundle bundle = new Bundle();
                bundle.putParcelable("uri", FileOperation.getUriFromFilepath(context, save_path));
                message.setData(bundle);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}
