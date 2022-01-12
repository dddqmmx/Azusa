package com.dd.azusa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.azusa.function.Control;

public class DeadList extends AppCompatActivity {

    Activity activity;      //当前Activity
    Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = (Control) getApplication();       //获取控制方法
        activity = this;                            //设置当前Activity
        setContentView(R.layout.activity_dead_list);
        TextView textView = findViewById(R.id.Dead);
        new Thread(()->{
            String text = control.deadList();
            activity.runOnUiThread(()->{
               textView.setText(text);
            });
        }).start();
    }
}