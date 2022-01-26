package com.dd.azusa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.azusa.function.Control;
import com.dd.azusa.view.Dialogs;

public class MyInfo extends AppCompatActivity {
    public Activity activity;
    Control control;
    public PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = (Control) getApplication();       //获取控制方法
        setContentView(R.layout.activity_my_info);
        activity = this;
        ImageView myHead = findViewById(R.id.HeadView);
        control.setUserHead(control.user,myHead,this);
        RelativeLayout userNameView = findViewById(R.id.UserView);
        TextView myName = findViewById(R.id.NameView);
        myName.setText(control.getUserName(control.user));
        userNameView.setOnClickListener((view)->{
            Dialogs dialogs=new Dialogs(this,R.style.MyDialog);
            dialogs.setTitle("设置名称");
            dialogs.setEditText(control.getUserName(control.user));
            dialogs.setYesText("确定");
            dialogs.setYesOnclickListener(()->{
                String newName = dialogs.getEditText();
                myName.setText(newName);
                control.setUserName(newName);
                control.nameMap.put(control.user,newName);
            });
            dialogs.setNoText("取消");
            dialogs.show();
        });
        RelativeLayout UserV=findViewById(R.id.UserV);
        UserV.setOnClickListener(view -> {
            @SuppressLint("InflateParams") View contentView= LayoutInflater.from(this).inflate(R.layout.view_set_head,null,false);
            popupWindow=new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
            TextView cancel=contentView.findViewById(R.id.cancel);
            cancel.setOnClickListener(view13 -> {
                popupWindow.dismiss();
                popupWindow=null;
            });
            TextView open_album=contentView.findViewById(R.id.open_album);
            open_album.setOnClickListener(view14 -> {
                popupWindow.dismiss();
                popupWindow=null;
            });
        });
        RelativeLayout UserView=findViewById(R.id.UserView);
    }
}
