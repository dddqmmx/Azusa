package com.dd.nanami;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.dd.nanami.function.Control;
import com.dd.nanami.util.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class UpDate extends AppCompatActivity {

    Control control;                //控制方法
    RelativeLayout upLoadLayout;    //界面主布局
    TextView textView;              //提示文本控件
    ProgressBar progressBar;        //当前进度条控件
    Activity activity;              //当前界面控件
    int up=0;                       //上一秒共下载了多少数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;                                      //获取当前界面
        control = (Control) getApplication();               //获取获取控制方法
        setContentView(R.layout.activity_up_date);          //设置初始布局
        upLoadLayout = findViewById(R.id.up_load_layout);   //获取界面主布局
        textView = findViewById(R.id.textView);             //获取更新提示文本
        progressBar = findViewById(R.id.progressBar);       //获取当前进度条
        new Thread(() -> {
            try {
                //判断更新是否成功
                if (downloadFile("http://"+control.protocol+"/file/test.apk")) {
                    //更新完成
                    System.out.println("更新完成");
                    activity.runOnUiThread(()-> {
                        textView.setText("更新完成,等待安装");
                        install(this);
                    });
                } else {
                    //更新失败
                    System.out.println("更新失败");
                    activity.runOnUiThread(()-> {
                        textView.setText("更新失败,请重启软件");
                    });
                }
                upLoadLayout.removeView(progressBar);       //去掉进度条
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    //是否更新完成
    private int size = 1;                           //文件大小
    private int hasRead = 0;                        //下载了多少字节
    private final byte[] buffer = new byte[1024*4]; //下载时的缓存
    private int index = 0;                          //下载进度

    /**
     *
     * 下载安装包
     * ( 已经暴毙了,不能用待修复 )
     *
     * @param urlStr 服务器上安装包地址
     * @return       返回是否更新成功
     */

    @SuppressLint("SetTextI18n")
    public boolean downloadFile(final String urlStr) throws Exception {
        //下载线程
        Files files=new Files();                                                    //文件操作方法
        if(!files.exists(control.filePath+"/Update/app.apk")){                 //判断安装包是否存在
            new Thread(()->{
                try {
                    while (index!=100) {                                            //循环更新下载进度直到下载完成
                        final int speed=(hasRead-up);                               //计算下载速度
                        activity.runOnUiThread(()-> textView.setText(
                            "下载中...\n" +
                            "速度: "+getPrintSize(speed)+"\n" +
                            "已下载: "+getPrintSize(hasRead)+
                            "大小: "+getPrintSize(size)+"\n" +
                            "进度: "+index+"%"
                        ));
                        up=hasRead;                                                 //更新上一秒下载
                        sleep(1000);                                          //一秒延迟
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();                                            //抛异常
                }
            }).start();
            URL url = new URL(urlStr);                                              //设置下载地址
            HttpURLConnection connection = (HttpURLConnection)url.openConnection(); //打开http连接
            size = connection.getContentLength();                                   //获取文件大小
            InputStream inputStream = connection.getInputStream();                  //获取下载流
            if(!files.exists(control.filePath+"/Update/")){                    //下载后判断目录是否存在
                files.mkdirs(control.filePath+"/Update/");                     //创建下载目录
            }
            OutputStream outputStream = new FileOutputStream(control.filePath+"/Update/app.apk");  //获取文件流
            //缓存大小
            int len;
            while((len =inputStream.read(buffer))!=-1){                             //如果还未下载完成就循环执行
                outputStream.write(buffer);                                         //文件流写入缓存
                hasRead+= len;                                                      //更新总下载
                index = (hasRead*100) /size;                                        //下载百分比
                activity.runOnUiThread(()-> progressBar.setProgress(index));        //更新进度条
            }
            inputStream.close();                                                    //关闭下载流
            outputStream.close();                                                   //关闭我文件流
        }
        return true;
    }

    /**
     *
     * 字节转换成最大单位大小
     *
     * @param size 字节数量
     * @return  返回大小
     */
    public static String getPrintSize(long size) {

        //网上偷来的代码不知道怎么写注释
        //网上偷来的代码不知道怎么写注释
        //网上偷来的代码不知道怎么写注释

        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
    public static void install(Context context) {
        File file = new File("/storage/emulated/0/Azusa/Update/app.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.dd.nanami.provider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}