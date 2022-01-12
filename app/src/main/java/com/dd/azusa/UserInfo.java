package com.dd.azusa;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.azusa.function.Control;

public class UserInfo extends AppCompatActivity {

    Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = (Control) getApplication();       //获取控制方法
        setContentView(R.layout.activity_dead_list);
    }
}
