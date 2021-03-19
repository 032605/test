package com.example.test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;

public class Alarm_Activity extends AppCompatActivity {

    private ImageView       img_knh_back;
    private final boolean D = true;
    private final String TAG = "Alarm_Activity";

    // View Object 관련
    private ListView hisview;
    private static boolean db_check = false;
    private MyAdapter myAdapter = new MyAdapter();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);

        img_knh_back = findViewById(R.id.img_knh_back);
        hisview= findViewById(R.id.hisview);

        dataSetting();
        if(db_check == false)
        {
            init_DB();
        }
        if (D) Log.i(TAG,"onCreate() OK");
    }
    private void init_DB() {
        FirebaseDatabase.getInstance().getReference("Alert").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (db_check == true) {
                    DialogActivity.alertDTOS.clear();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DialogActivity.alertDTOS.add(snapshot.getValue(AlertDTO.class));
                    Log.d("MMM", "방법1 : " + snapshot.getValue().toString() + snapshot.getKey());
                    // notifi랑 비교해서 값 체크해야함
                }
                if(db_check == false)
                {
                    db_check = true;
                    dataSetting();
                    myAdapter.notifyDataSetChanged();
                }
                else
                {
                    send_notification();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void send_notification() {
        AlertDTO temp;
        Log.d("QQQ","size : "+DialogActivity.alertDTOS.size());
        if(DialogActivity.alertDTOS.size()>0)
        {
            temp = DialogActivity.alertDTOS.get(DialogActivity.alertDTOS.size()-1);
        }
        else
        {
            return;
        }
        Log.d("QQQ","temp : "+MainActivity.Cal_distance(new LatLng(temp.getLatitude(), temp.getLongitude())));
        if (MainActivity.Cal_distance(new LatLng(temp.getLatitude(), temp.getLongitude())) >= 1)
        {
            return;
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 26) {
            //When sdk version is larger than26
            String id = "channel_1";
            String description = "143";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(getApplicationContext(), id)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("사고야!")
                    .setContentText(temp.getComment())
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);
        } else {
            //When sdk version is less than26
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("사고야!")
                    .setContentText(temp.getComment())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            manager.notify(1, notification);
        }
    }
    private void dataSetting() {
        Log.d("MMM","size"+DialogActivity.alertDTOS.size());
        for(int i=0;i<DialogActivity.alertDTOS.size();i++)
        {
            Log.d("MMM","거리계산값"+
                            MainActivity.Cal_distance(
                                    new LatLng(DialogActivity.alertDTOS.get(i).getLatitude(),
                                            DialogActivity.alertDTOS.get(i).getLongitude())));
            if(MainActivity.Cal_distance(
                    new LatLng(DialogActivity.alertDTOS.get(i).getLatitude(),
                            DialogActivity.alertDTOS.get(i).getLongitude()))<1);
            myAdapter.addItem(DialogActivity.alertDTOS.get(i));
        }

        /* 리스트뷰에 어댑터 등록 */
        hisview.setAdapter(myAdapter);
    }

    public void backClick (View V) {
            finish();
    }

}

