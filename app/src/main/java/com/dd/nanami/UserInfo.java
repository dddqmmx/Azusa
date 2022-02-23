package com.dd.nanami;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.nanami.function.Control;

public class UserInfo extends AppCompatActivity {

    Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());      //主线程网络操作

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

        Button addFriendButton = findViewById(R.id.addFriend);
        addFriendButton.setOnClickListener((view)->{
            control.addFriend(id);
            Toast.makeText(this,"好友请求已发送",Toast.LENGTH_LONG).show();
        });

        Button seedMessage = findViewById(R.id.sendMessage);
        seedMessage.setOnClickListener((view)->{

        });

    }
}
