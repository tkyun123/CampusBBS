package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.logging.LogRecord;

public class SystemService {
    public static final String DEFAULT_LOCATION = "地球";

    public static void getLocation(Context context, Handler handler) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
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

                Log.d("", best_provider);

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
                                    getFromLocation(context, location1, message);
                                }
                            });
                }
                else{
                    Log.d("location", String.valueOf(location));
                    getFromLocation(context, location, message);
                }
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private static void getFromLocation(Context context, Location location, Message message){
        try {
            ServiceSettings.getInstance().setApiKey("a87e48cae9a216077a37c492ed0c591e");
            ServiceSettings.updatePrivacyShow(context, true, true);
            ServiceSettings.updatePrivacyAgree(context, true);

            GeocodeSearch geocodeSearch = new GeocodeSearch(context);
            LatLonPoint latLonPoint = new LatLonPoint(
                    39.92, 116.46);
            RegeocodeQuery regeocodeQuery = new RegeocodeQuery(
                    latLonPoint, 25, GeocodeSearch.GPS);
            RegeocodeAddress address = geocodeSearch.getFromLocation(regeocodeQuery);

            Bundle bundle = new Bundle();
            bundle.putString("location", address.getProvince());
            message.what = 0;
            message.setData(bundle);
        }catch (AMapException e){
            e.printStackTrace();
            message.what = -1;
        }
    }

    public static boolean checkLogin(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("nickName", null) == null){
            return false;
        }
        return true;
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
}
