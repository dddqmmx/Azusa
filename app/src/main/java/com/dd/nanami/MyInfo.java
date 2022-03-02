package com.dd.nanami;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dd.nanami.function.Control;
import com.dd.nanami.view.Dialogs;

import java.io.File;
import java.io.InputStream;

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
            TextView open_album=contentView.findViewById(R.id.open_album);
            TextView open_system_album = contentView.findViewById(R.id.open_system_album);
            open_system_album.setOnClickListener(view1->{
                Intent i = new Intent();
                /* 开启Pictures画面Type设定为image */
                i.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                i.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(i, 1);
            });
            open_album.setOnClickListener(view14 -> {
                startActivityForResult(new Intent(MyInfo.this,FileList.class),2);    //进入主界面
                popupWindow.dismiss();
                popupWindow=null;
            });
            TextView cancel=contentView.findViewById(R.id.cancel);
            cancel.setOnClickListener(view13 -> {
                popupWindow.dismiss();
                popupWindow=null;
            });
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 1) {
                if (data != null) {
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    try {
                        InputStream is = cr.openInputStream(uri);
                        control.uploadUserHead(is);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (requestCode == 2){
                if (data != null) {
                    control.uploadUserHead(new File(data.getStringExtra("filePath")));
                }
            }
        }
    }

}
