package com.dd.azusa;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.dd.azusa.function.Control;
import com.dd.azusa.view.Dialogs;

public class Login extends AppCompatActivity {

    Control control;                    //控制方法
    boolean mode=true;                  //模式(登录true,注册false)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = (Control) getApplication();           //获取控制方法
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());      //主线程网络操作
        setContentView(R.layout.activity_login);                    //设置登录界面布局
        LayoutInflater factory=LayoutInflater.from(Login.this);     //获取主界面容器

        //标题栏
        View actionBarEntryView=factory.inflate(R.layout.view_title,new LinearLayout(this),false);      //新建标题栏布局
        ActionBar actionBar=getSupportActionBar();          //获取标题栏对象
        assert actionBar!=null;                             //防止发生发空指针
        actionBar.setCustomView(actionBarEntryView);        //设置标题栏布局
        actionBar.setDisplayShowCustomEnabled(true);        //让自定义标题栏可以显示
        actionBar.setDisplayShowHomeEnabled(false);         //隐藏左上角的三个点
        actionBar.show();                                   //显示新的自定义标题栏

        final TextView actionText=actionBarEntryView.findViewById(R.id.path);       //获取标题文本控件

        actionText.setText("登录");                          //默认登录模式

        final TextView userText=findViewById(R.id.userT);   //显示登录id或注册名字的文本
        final EditText userEdit=findViewById(R.id.user);    //输入登录id或注册名字的输入框
        final EditText passEdit=findViewById(R.id.pass);    //输入密码的输入框
        Button loginButton=findViewById(R.id.login);        //登录按钮
        final CheckBox auto=findViewById(R.id.auto);        //勾选自动登录按钮

        //登录按钮点击事件
        loginButton.setOnClickListener(view->{
            //设置暂时用户名
            String user=userEdit.getText().toString();      //用户输入的用户名
            String pass=passEdit.getText().toString();      //用户输入的密码

            //实例化新弹窗
            Dialogs dialogs = new Dialogs(this,R.style.MyDialog);

            //判断是登录模式还是注册模式
            if (mode) {
                //登录模式
                control.setUser(user);                                    //设置暂时id保存到控制类
                control.setPass(pass);                                    //设置暂时密码保存到控制类
                if(control.login()){                                      //登录并且判断是否成功
                    //登录成功
                    dialogs.setTitle("登陆成功");                           //登录成功弹窗标题
                    dialogs.setMessage("点击确定进入主界面");                 //登录成功副标题
                    dialogs.setYesOnclickListener(()->{
                        startActivity(new Intent(Login.this,Main.class));
                        Login.this.finish();
                    });
                }else{
                    //如果登录失败
                    dialogs.setTitle("登陆失败");                           //登录失败弹窗标题
                    dialogs.setMessage("账号或密码错误,也有可能是你没联网");     //登录失败弹窗副标题
                }
            } else {                                                      //注册模式
                control.registered(user,pass);                            //调用注册方法并且传入名字和密码
                if(!control.user.equals("null")){                         //判断注册是否成功
                    dialogs.setTitle("注册完成");                           //标题
                    dialogs.setMessage("你的ID是: "+control.user);         //副标题
                    dialogs.setYesOnclickListener(()->{
                        startActivity(new Intent(Login.this,Main.class));
                        Login.this.finish();
                    });
                }else{                                                    //注册失败提示
                    dialogs.setTitle("错误");                              //注册失败弹窗标题
                    dialogs.setMessage("有可能是你没联网,或者服务器爆炸了(悲)"); //注册失败弹窗副标题
                }
            }
            dialogs.setCancelable(false);                                 //是否可以让用户手动关闭
            dialogs.setYesText("确定");                                    //确认按钮
            dialogs.show();                                               //将弹窗弹出

            if (auto.isChecked()) {
                control.saveInfo();
            };

        });

        TextView registered=findViewById(R.id.Registered);      //切换模式

        //注册文本的点击事件
        registered.setOnClickListener(view -> {
            //判断是登录模式还是注册模式
            if (mode){
                //更改注册模式
                userText.setText("名字 ");                       //名字或id控件文字
                loginButton.setText("注册");                     //按钮文字
                actionText.setText("注册");                      //标题文字
                registered.setText("已经有账号了？点击登录");        //切换模式文字
                mode=false;                                     //切换到注册模式
            } else {
                //更改登录模式
                userText.setText("账号 ");                       //名字或id控件文字
                loginButton.setText("登陆");                     //按钮文字
                actionText.setText("登陆");                      //标题文字
                registered.setText("没有账号？点击注册");           //切换模式文字
                mode=true;                                      //切换登录模式
            }
        });
    }

}