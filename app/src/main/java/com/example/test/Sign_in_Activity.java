package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.naver.maps.map.NaverMapSdk;

public class Sign_in_Activity extends AppCompatActivity {
    //Member Variable 선언-------------------------------------------
    private String TAG = "MainActivity";
    private boolean D = true;
    private String fcnToken;
    //View Object----------------------------------------------------
    private EditText et_pgh_email;
    private EditText et_pgh_password;
    private Button bt_pgh_login;
    private TextView tv_pgh_signUp;
    private InputMethodManager im_pgh_imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        init();
        im_pgh_imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
    }

    // UI 초기화 Method------------------------------------------------
    public void init(){
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("jit743xg27"));
        et_pgh_email = findViewById(R.id.et_pgh_signUpemail);
        et_pgh_password = findViewById(R.id.et_pgh_pw);
        getToken();

    }

    // Login Button Click Event
    public void clickBTN(View v){
        //아이디 비밀번호 확인
        if(et_pgh_email.getText().length()<0){
            Toast.makeText(this, "email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            im_pgh_imm.hideSoftInputFromWindow(et_pgh_email.getWindowToken(),0);
            return;
        }
        if(et_pgh_password.getText().length()<0){
            Toast.makeText(this, "password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            im_pgh_imm.hideSoftInputFromWindow(et_pgh_email.getWindowToken(),0);
            return;
        }

        Intent intent = new Intent(Sign_in_Activity.this, MainActivity.class);
        startActivity(intent);

        //Auth 활용 로그인 소스 코드
       // login(et_pgh_email.getText().toString(),et_pgh_password.getText().toString());
    }

    //Sign_up 화면으로 이동하기 위한 텍스트 클릭 이벤트
    public void clickTXT(View v){
        Intent intent = new Intent(Sign_in_Activity.this,Sign_Up_Activity.class);
        startActivity(intent);
    }
    public void login(String email,String password)
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            //로그인 성공시 Uid를 넘겨 받음
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            //기기 토큰 정보 가져오기

                            int locationX = 100;
                            int locationY = 200;
                            UserDTO userDTO = new UserDTO(fcnToken,locationX,locationY);
                            FirebaseDatabase.getInstance().getReference("User").child(uid).setValue(userDTO);
                            Toast.makeText(getApplicationContext(), "로그인 성공!", Toast.LENGTH_LONG).show();
                            //temp activity move -> 수정 필요
                            Intent intent = new Intent(Sign_in_Activity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 실패!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        fcnToken = task.getResult();

                    }
                });

    }
}