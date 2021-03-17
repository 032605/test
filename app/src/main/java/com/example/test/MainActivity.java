package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity<queryString> extends AppCompatActivity {

    private final boolean       D = true;
    private final String        TAG = "MainActivity";

    private EditText yearETXT, monthETXT, dayETXT;
    private Button  button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_layout);

        yearETXT = findViewById(R.id.yearETXT);
        monthETXT = findViewById(R.id.monthETXT);
        dayETXT = findViewById(R.id.dayETXT);
        button = findViewById(R.id.button);

    }

    public void onClick (View v) {
        Log.i(TAG, "onClick()");

        switch (v.getId()) {
            case R.id.img_knh_car:
                CustomDialog dialog = new CustomDialog(this);
                dialog.callDialog();

                break;

            case R.id.img_knh_fire:

                break;


            case  R.id.img_knh_const:
                break;


            case R.id.btn_knh_etc:
                break;
        }
    }


}