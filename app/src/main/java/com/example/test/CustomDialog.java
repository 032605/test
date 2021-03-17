package com.example.test;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;


public class CustomDialog {
    private Context context;

    public CustomDialog(Context context) {
        this.context = context;
    }

    public void callDialog() {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final Button Confirm = dialog.findViewById(R.id.btn_knh_Confirm);
        final Button Cancel = dialog.findViewById(R.id.btn_knh_Cancle);
        final RadioButton rbtn_knh_op1 = dialog.findViewById(R.id.rbtn_knh_op1);
        final RadioButton rbtn_knh_op2 = dialog.findViewById(R.id.rbtn_knh_op2);


        rbtn_knh_op1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbtn_knh_op1.isChecked()) {
                    String opmsg = rbtn_knh_op1.getText().toString();
                    Toast.makeText(context,opmsg, Toast.LENGTH_SHORT).show();
                }
            }

        });


        // Confirm 버튼
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"알림을 보냅니다.", Toast.LENGTH_LONG).show();
            }
        });

        // Cancle 버튼
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(context,"취소하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
