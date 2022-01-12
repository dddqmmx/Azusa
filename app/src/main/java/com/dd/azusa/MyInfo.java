package com.dd.azusa;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.azusa.function.Control;

public class MyInfo extends AppCompatActivity {
    Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = (Control) getApplication();       //获取控制方法
        setContentView(R.layout.activity_my_info);
        ImageView myHead = findViewById(R.id.HeadView);
        control.setUserHead(control.user,myHead,this);
        TextView myName = findViewById(R.id.NameView);
        myName.setText(control.getUserName(control.user));
    }
}
