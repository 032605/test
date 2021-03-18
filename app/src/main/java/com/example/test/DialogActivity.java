package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DialogActivity<queryString> extends AppCompatActivity {

    private final boolean       D = true;
    private final String        TAG = "DialogActivity";

    public Dialog  dialog;

    private RadioButton     rbtn_knh_op1, rbtn_knh_op2;
    private  Button btn_knh_Confirm;
    //private String TAG = "tempAcitivity";
    private ArrayList<UserDTO> userList;
    //Notification 을 위한 서버 키 값
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAv0So2UA:APA91bGUIx8g9J15CH-vOymVe8JSNVkDaOsdeww2cRD6oZHG0TvlnB2cax6MKutyL6TAMdZZgjZvxeZ_2fadYaLn8t8565AzH2Gp9JRVuSseIHDP1YwSLHdgyZOPqSFitKChOlosboXZ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_layout);


        //temp method init
        init();
        getAllUser();

    }

    private Dialog makeDialog (String title) {
        dialog = new Dialog(DialogActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RadioButton rbtn_knh_op1 = dialog.findViewById(R.id.rbtn_knh_op1);
        rbtn_knh_op1.setText(title);
        RadioButton rbtn_knh_op2 = dialog.findViewById(R.id.rbtn_knh_op2);
        EditText et_knh_custominfo = dialog.findViewById(R.id.et_knh_custominfo);

        Button btn_knh_Confirm = dialog.findViewById(R.id.btn_knh_Confirm);
        Button btn_knh_Cancle = dialog.findViewById(R.id.btn_knh_Cancle);


        // radio button 속성 --------------------------------------------------------------

        rbtn_knh_op1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbtn_knh_op1.isChecked()) {

                    et_knh_custominfo.setEnabled(false);  // editText 비활성화
                    rbtn_knh_op1.setTextColor(Color.BLACK);
                    rbtn_knh_op2.setTextColor(Color.GRAY);
                }
            }
        });

        rbtn_knh_op2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbtn_knh_op2.isChecked()) {
                    et_knh_custominfo.setEnabled(true);  // editText 활성화

                    rbtn_knh_op2.setTextColor(Color.BLACK); // 색상
                    rbtn_knh_op1.setTextColor(Color.GRAY);
                }
            }

        });
        // -------------------------- radio button 속성 끝 -------------------------------

        // dialog 버튼

        btn_knh_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbtn_knh_op1.isChecked() || rbtn_knh_op2.isChecked()) {
                    dialog.dismiss();
                    Toast.makeText(DialogActivity.this, "알림을 보냅니다.", Toast.LENGTH_LONG).show();

                } else
                    Toast.makeText(DialogActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        btn_knh_Cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DialogActivity.this,"취소하였습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        return dialog;
    }


    public void onClick (View v) {
        Log.i(TAG, "onClick()");


        switch (v.getId()) {
            case R.id.img_knh_car:
                makeDialog("인근에서 교통사고 발생").show();
                break;

            case R.id.img_knh_fire:
                makeDialog("인근에서 화재 발생").show();
                break;

            case  R.id.img_knh_const:
                makeDialog("인근에서 공사 중").show();
                break;

            case R.id.btn_knh_etc:
                makeDialog("기타").show();
                break;
        }
    }


    //-------------------------------------

    // 초기화 메서드
    public void init() {
        userList = new ArrayList<UserDTO>();
    }


    //모든 유저 정보를 가져오는 메서드
    public void getAllUser()
    {
        FirebaseDatabase.getInstance().getReference().child("User").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserDTO userDTO =dataSnapshot.getValue(UserDTO.class);
                Log.i(TAG,userDTO.getFcnToken()+"");
                userList.add(userDTO);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // Client단에서 FCM 보내는 메서드
    private void sendPostToFCM(final String message)
    {

        for (UserDTO user : userList) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // FMC 메시지 생성 start
                        JSONObject root = new JSONObject();
                        JSONObject notification = new JSONObject();
                        JSONObject data = new JSONObject();
                        notification.put("body", "경상북도 김천시");
                        notification.put("title", "AN");
                        data.put("nick","이번에야 말로");
                        root.put("data",data);
                        root.put("notification", notification);
                        root.put("to", user.getFcnToken());
                        // FMC 메시지 생성 end
                        URL Url = new URL(FCM_MESSAGE_URL);
                        HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setRequestProperty("Content-type", "application/json");
                        OutputStream os = conn.getOutputStream();
                        os.write(root.toString().getBytes("utf-8"));
                        os.flush();
                        conn.getResponseCode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

}