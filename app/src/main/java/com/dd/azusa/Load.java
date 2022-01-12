package com.dd.azusa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.azusa.function.Control;
import com.dd.azusa.view.Dialogs;

/**
 * 加载界面
 */

public class Load extends AppCompatActivity {

    Activity activity;      //当前Activity
    Control control;        //控制方法

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());      //主线程网络操作

        activity = this;                            //设置当前Activity
        control = (Control) getApplication();       //获取控制方法
        setContentView(R.layout.activity_load);     //设置加载界面

        new Thread(()->{
            try {
                int version=activity.getPackageManager().getPackageInfo(this.getPackageName(),0).versionCode;   //客户端版本号
                Thread.sleep(2000);           //停止线程
                control.initialize(activity);       //初始化
                if ( control.getUpDate(version) ) {
                    //需要更新
                    activity.runOnUiThread(()->{
                        Dialogs dialogs = new Dialogs(this,R.style.MyDialog);
                        dialogs.setCancelable(false);      //是否可以让用户手动关闭
                        dialogs.setTitle("更新");
                        dialogs.setMessage(
                            "本次更新内内容:\n" +
                            control.getUpDateInfo()
                        );
                        dialogs.setNoText("不更新");
                        dialogs.setYesText("更新");
                        dialogs.setYesOnclickListener(() -> {
                            startActivity(new Intent(Load.this,UpDate.class));
                            Load.this.finish();
                        });
                        dialogs.setNoOnclickListener(this::login);
                        dialogs.show();
                    });
                    //进入更新界面
                } else {
                    //无需更
                    login();
                }
            } catch (Exception e) {
                //抛出异常
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 登录
     */

    public void login(){
        if (control.getUserSave()) {    //判断
            control.fileSet();          //设置用户信息
        }
        if (control.login()){           //登录
            //登录成功
            startActivity(new Intent(Load.this,Main.class));    //进入主界面
        } else {
            //登录失败
            startActivity(new Intent(Load.this,Login.class));   //进入登录界面
        }
        Load.this.finish(); //关闭当前界面
    }

}