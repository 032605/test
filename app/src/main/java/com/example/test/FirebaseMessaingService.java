package com.example.test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    private String msg,title,type,time,locationX,locationY;
    /* 토큰이 새로 만들어질때나 refresh 될때  */
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);

        /* DB서버로 새토큰을 업데이트시킬수 있는 부분 */
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG,"onMessageReceived");

        if(remoteMessage.getNotification()!=null) {
            //notification을 통해 전달된 title, msg, type 등의 데이터를 받는 부분
            title = remoteMessage.getNotification().getTitle();
            Log.i(TAG, "=> onMessageReceived Title"+ title);
            msg = remoteMessage.getNotification().getBody();
            type = remoteMessage.getData().get("type");
            time = remoteMessage.getData().get("time");
            locationX = remoteMessage.getData().get("locationX");
            locationY = remoteMessage.getData().get("locationY");
            Log.i(TAG, "=> onMessageReceived getType"+ type);
            Log.i(TAG, "=> onMessageReceived getTime"+ time);
            Log.i(TAG, "=> onMessageReceived getLocationX"+ locationX);
            Log.i(TAG, "=> onMessageReceived getLocationY"+ locationY);
            //그 사람의 고유한 키를 받음
            String uid =FirebaseAuth.getInstance().getCurrentUser().getUid();
            AlertDTO alertDTO = new AlertDTO(type,time,locationX,locationY);
            FirebaseDatabase.getInstance().getReference("Alert").child(uid).push().setValue(alertDTO);
            Log.i(TAG, "=> onMessageReceived getBody"+ msg);
            Intent intent = new Intent(this, DialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //notification의 행위
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
            String channelId = "Channel ID";
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,channelId).setSmallIcon(R.drawable.logo2)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{1, 1000});
            //1초 진동
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                String channel_name = "Channel Name";
                NotificationChannel channel =new NotificationChannel(channelId,channel_name,NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, mBuilder.build());
            mBuilder.setContentIntent(contentIntent);
        }
    }
}