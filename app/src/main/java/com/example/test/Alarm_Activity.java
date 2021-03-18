package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Alarm_Activity extends AppCompatActivity {

    private ImageView       img_knh_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);

        img_knh_back = findViewById(R.id.img_knh_back);
    }

    public void backClick (View V) {
            finish();
    }

}

