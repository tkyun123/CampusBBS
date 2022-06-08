package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class AskForNoticeService extends Service {
    @Nullable

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;
    private int user_id;

    private JSONArray data;
    private NotificationManager notificationManager;
    private Notification notification;

    private myBinder binder = new myBinder();

    public class myBinder extends Binder {
        AskForNoticeService getService(){
            return AskForNoticeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                checkNotice();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        user_id = intent.getIntExtra("user_id", -1);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(Consts.channelId,
                    Consts.channelName, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        timer.schedule(timerTask, 0, Consts.NOTICE_CHECK_PERIOD);
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkNotice(){
        try {
            String result = HttpRequest.post("/API/send_notice",
                    String.format("uid=%s", user_id), "form");
            JSONObject jsonObject = new JSONObject(result);
            data = jsonObject.getJSONArray("data");
            Log.d("checkNotice:", String.valueOf(jsonObject));
            for(int i=0;i<data.length();i++){
                notification = new NotificationCompat
                        .Builder(getApplicationContext(), Consts.channelId)
                        .setSmallIcon(R.drawable.notice_icon)
                        .setContentText(data.getJSONObject(i).getString("text"))
                        .build();
                notificationManager.notify(i, notification);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
