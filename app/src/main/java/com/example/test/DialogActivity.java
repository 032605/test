package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

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
    private ImageView   img_knh_accbtn1, img_knh_accbtn2, img_knh_accbtn3, img_knh_accbtn4;



    //private String TAG = "tempAcitivity";
    private ArrayList<UserDTO> userList;
    private ArrayList<String> userKeyList;
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

    private Dialog makeDialog (String title, String type) {
        dialog = new Dialog(DialogActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RadioButton rbtn_knh_op1 = dialog.findViewById(R.id.rbtn_knh_op1);
        rbtn_knh_op1.setText(title);

        RadioButton rbtn_knh_op2 = dialog.findViewById(R.id.rbtn_knh_op2);
        EditText et_knh_custominfo = dialog.findViewById(R.id.et_knh_custominfo);
        et_knh_custominfo.setEnabled(false);

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

                double x =  MainActivity.Click_coord.latitude;
                double y = MainActivity.Click_coord.longitude;

                if (rbtn_knh_op1.isChecked()) {
                    dialog.dismiss();

                    String basic = rbtn_knh_op1.getText().toString();
                    Log.i(TAG, "Dialog checkedButton : " + basic +  " : " + type);

                    sendPostToFCM("AN",basic,type,"2021.03.18",x+"",y+"");

                } else if (rbtn_knh_op2.isChecked()) {
                    dialog.dismiss();

                    String info = et_knh_custominfo.getText().toString();
                    Log.i(TAG, "Dialog checkedButton : " + info +  " : " + type);

                    sendPostToFCM("AN",info,type,"2021.03.18",x+"",y+"");

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
            case R.id.img_knh_accbtn1:
                makeDialog("인근에서 교통사고 발생","교통사고").show();
                break;

            case R.id.img_knh_accbtn2:
                makeDialog("인근에서 화재 발생","화재사고").show();
                break;

            case  R.id.img_knh_accbtn3:
                makeDialog("인근에서 공사 중","공사 중").show();
                break;

            case R.id.btn_knh_accbtn4:
                makeDialog("기타","기타").show();
                break;
        }
    }


    //-------------------------------------

    public void backClick (View V) {
        finish();
    }

    // 초기화 메서드
    public void init() {
        Toast.makeText(getApplicationContext()," 위도 "+MainActivity.Click_coord.latitude+ " 경도 " + MainActivity.Click_coord.longitude,Toast.LENGTH_SHORT).show();
        userList = new ArrayList<UserDTO>();
        userKeyList = new ArrayList<String>();
    }


    //모든 유저 정보를 가져오는 메서드
    public void getAllUser()
    {
        FirebaseDatabase.getInstance().getReference().child("User").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userKeyList.add(dataSnapshot.getKey());
                UserDTO userDTO =dataSnapshot.getValue(UserDTO.class);
                Log.i(TAG,userDTO.getFcnToken()+"");
                userList.add(userDTO);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //사용자 아이디의 기기가 변경될 시 그에 대한 list 변환 반영을 위한 메서드
                //변한 유저 정보를 가져옴
                UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                //유저 키를 가져옴
                String key = dataSnapshot.getKey();
                // 그 키와 맞는 유저 리스트의 인덱스 가져옴
                int userIndex = userKeyList.indexOf(key);
                UserDTO user =userList.get(userIndex);
                //유저 정보 수정
                userList.set(userIndex,userDTO);
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
    private void sendPostToFCM(String title, String body, String type, String time,String locationX,String locationY)
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
                        notification.put("body", body);
                        notification.put("title", title);
                        data.put("type",type);
                        data.put("time",time);
                        data.put("locationX",locationX);
                        data.put("locationY",locationY);
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
