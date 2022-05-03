package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import androidx.core.app.ActivityCompat;

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

public class SystemService {
    private static final String default_location = "地球";
    public static String getLocation(Context context) {
        // 模拟器调试存在问题
        final String[] return_loc = {default_location};

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
            return return_loc[0];
        }

        Log.d("", best_provider);

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("", "权限不支持");
            return return_loc[0];
        }
        Location location = locationManager.getLastKnownLocation(best_provider);
        if(location == null){
            Log.d("", "未找到location");
//            locationManager.requestLocationUpdates(best_provider, 3600 * 1000,
//                    100, location1 -> {
//                        if (location1 != null) {
//                            return_loc[0] = getFromLocation(context, location1);
//                        }
//                    });
        }
        else{
            Log.d("location", String.valueOf(location));
            return_loc[0] =getFromLocation(context, location);
        }
        return return_loc[0];

    }

    private static String getFromLocation(Context context, Location location){
        Geocoder geocoder = new Geocoder(context, Locale.CHINESE);

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1
            );
            Address address = addresses.get(0);
            Log.d("地址:", address.getAddressLine(1));
            return address.getAddressLine(1);
        }catch (IOException e){
            Log.d("error:", String.valueOf(e));
            return default_location;
        }
    }

    public static boolean checkLogin(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                "login", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("user", null) == null){
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
