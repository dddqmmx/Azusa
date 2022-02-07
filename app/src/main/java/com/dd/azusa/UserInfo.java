package com.dd.azusa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.azusa.function.Control;

public class UserInfo extends AppCompatActivity {

    Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = (Control) getApplication();       //获取控制方法
        Intent intent = getIntent();
        long id = intent.getLongExtra("id",0);
        System.out.println(id);
        setContentView(R.layout.activity_user_info);

        ImageView headImage = findViewById(R.id.head);

        control.setUserHead(String.valueOf(id),headImage,this);

        TextView nameText = findViewById(R.id.name);
        nameText.setText(control.getUserName(String.valueOf(id)));

        TextView idText = findViewById(R.id.id);
        idText.setText("ID : " + id);
    }
}
