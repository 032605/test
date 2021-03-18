package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Sign_Up_Activity extends AppCompatActivity {
    //Member Variable 선언-------------------------------------------
    private String TAG = "MainActivity";
    private boolean D = true;

    //View Object----------------------------------------------------
    private EditText et_pgh_email;
    private EditText et_pgh_password;
    private Button bt_pgh_createAccountBTN;
    private TextView tv_pgh_moveMainActivityTXT;
    private InputMethodManager im_pgh_imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        init();

        bt_pgh_createAccountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
        tv_pgh_moveMainActivityTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move();
            }
        });
    }
    // UI 초기화 Method------------------------------------------------
    public void init(){
        et_pgh_email = findViewById(R.id.et_pgh_signUpemail);
        et_pgh_password = findViewById(R.id.et_pgh_signUpw);
        bt_pgh_createAccountBTN = findViewById(R.id.bt_pgh_createAccountBTN);
        tv_pgh_moveMainActivityTXT = findViewById(R.id.tv_pgh_LoginMoveTXT);

    }

    // createAccount Button Click Event
    public void createAccount(){

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
        //firebase sign_up Check process
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(et_pgh_email.getText().toString(),et_pgh_password.getText().toString())
                .addOnCompleteListener(Sign_Up_Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            //이메일 인증
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //이메일 형식이 안맞는 경우
                                if (!task.isSuccessful()) {
                                    //Email fail
                                    Toast.makeText(Sign_Up_Activity.this, "SignUp Fail Email ", Toast.LENGTH_SHORT).show();
                                } else {
                                    //로그인 성공할 경우
                                    Toast.makeText(Sign_Up_Activity.this, "SignUp Success", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

    //Move to MainActivity

}
    public void move(){
        finish();
    }
}