package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.animation.AnimationUtils;

import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.example.myapplication.datatype.UserInfoStorage;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
            if (best_provider == null) {
                List<String> providers = locationManager.getProviders(true);
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

                    LatLonPoint latLonPoint = new LatLonPoint(
                            39.92, 116.46);
//                    LatLonPoint latLonPoint = new LatLonPoint(
//                            location.getLatitude(), location.getLongitude());
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
        if(sharedPreferences.getString("nickName", null) == null){
            return false;
        }
        return true;
    }

    public static void addInfo(Activity activity, UserInfoStorage user){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", user.token);
        editor.putInt("user_id", user.user_id);
        editor.putString("nickName", user.nickName);
        editor.putString("introduction", user.introduction);
        editor.putString("profile_photo_url", user.profile_photo_url);
        editor.apply();
    }

    public static void getInfo(Activity activity, UserInfoStorage user) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        user.user_id = sharedPreferences.getInt("user_id", -1);
        user.nickName = sharedPreferences.getString("nickName", null);
        user.introduction = sharedPreferences.getString("introduction", null);
        user.profile_photo_url = sharedPreferences.getString("profile_photo_url", null);
    }

    public static int getUserId(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1);
    }



    public static String imageUriToBase64(Uri uri, ContentResolver cr) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static String videoUriToBase64(Uri uri, ContentResolver cr){
        try {
            int byte_size = 1024;
            InputStream inputStream = cr.openInputStream(uri);
            byte[] bytes = new byte[byte_size];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int len;
            while((len=inputStream.read(bytes))!=-1){
                byteArrayOutputStream.write(bytes,0 ,len);
            }

            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static void clearJsonArray(JSONArray jsonArray){
        while(jsonArray.length()>1){
            jsonArray.remove(0);
        }
    }
}
