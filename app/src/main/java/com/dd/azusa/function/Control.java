package com.dd.azusa.function;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.dd.azusa.util.Files;
import com.dd.azusa.util.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Control extends Application {

    public String filePath = "/storage/emulated/0/Azusa";        //文件储存位置
    public String protocol = "39.107.229.253";                   //服务器地址
    public String url = "http://" + protocol + "/api/";              //api地址
    public String configPath = filePath + "/ConfigurationFile";    //配置文件地址
    Files files = new Files();                                   //文件操作

    public String user = null;                                     //用户名
    public String pass = null;                                     //密码

    public Map<String, String> nameMap = new HashMap<>();          //名字存储

    /**
     * 初始化
     *
     * @param activity 这是加载界面的Activity对象
     */
    public void initialize(Activity activity) {
        //需要获取的权限列表
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,      //读取文件权限
                Manifest.permission.WRITE_EXTERNAL_STORAGE,     //写入文件权限
        };
        if (!files.exists(filePath)) {       //判断软件存储根目录是否存在
            //不存在
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {   //判断是不是Android 11
                //判断有没有权限
                if (!Environment.isExternalStorageManager()) {
                    //申请权限
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + this.getPackageName()));
                    activity.startActivityForResult(intent, 1);
                }
            } else {
                //判断有没有权限
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //这是判断有没有权限
                    ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);    //申请权限
                }
            }
            files.mkdirs(filePath);     //创建软件根目录
        }
    }

    /**
     * 获取否需要更新
     *
     * @param version 这是当前本地的版本
     * @return 是否需要更新
     */
    public boolean getUpDate(int version) {
        int serverVersion = getVersion();
        return version < serverVersion;
    }

    /**
     * 获取服务器的版本号
     *
     * @return 服务器上的版本号
     */
    public int getVersion() {
        try {
            String version = Server.Loadweb(url + "GetVersion.php");     //获取服务器内最新版本
            if (version.equals("null")) {                                  //判断获取是否出错
                //出现获取错误
                System.out.println("没有网络或者代码错误");                  //在调试时输出错误详情
                return 0;                                                //返回版本为0
            }
            //没有获取错误
            return Integer.parseInt(version);                            //返回服务器版本
        } catch (Exception e) {
            e.printStackTrace();    //输出异常
            return 0;               //返回0
        }
    }

    public String getUpDateInfo() {
        try {
            return Server.Loadweb(url + "GetUpDateInfo.php");
        } catch (IOException e) {
            e.printStackTrace();
            return "无法获取服务器信息";
        }
    }

    /**
     * 设置用户(id)
     *
     * @param user 要设置的用户
     */
    public void setUser(String user) {
        this.user = user;   //设置用户
    }

    /**
     * 设置密码
     *
     * @param pass 要设置的密码
     */
    public void setPass(String pass) {
        this.pass = pass;   //设置密码
    }

    /**
     * 注册新账号
     *
     * @param name 用户昵称
     * @param pass 用户密码
     * @return 注册后的id
     */
    public String registered(String name, String pass) {
        try {
            String id = Server.Loadweb(url + "Registered.php?name=" + name + "&pass=" + pass);    //获取服务器返回的信息
            //判断受否无法访问服务器
            if (!id.equals("null")) {
                //可以访问服务区
                setUser(id);            //设置本地用户id
                setPass(pass);          //设置本地密码
            }
            return id;
        } catch (Exception e) {
            //运行出错
            e.printStackTrace();        //输出异常
            return null;                //返回错误代码
        }
    }

    /**
     * 登录
     *
     * @return 返回登录结果
     */
    public boolean login() {
        try {
            return Server.Loadweb(url + "Login.php?user=" + user + "&pass=" + pass).equals("true");    //返回登录结果
        } catch (IOException e) {
            e.printStackTrace();    //输出异常
            return false;           //返回登录失败
        }
    }

    /**
     * 保存配置文件或者密码到配置文件里面
     */
    public void saveInfo() {
        Files.Rwrite(configPath, "user", user);
        Files.Rwrite(configPath, "pass", pass);
    }

    /**
     * 判断是否自动登录
     *
     * @return 是否自动登录
     */
    public boolean getUserSave() {
        return !("0".equals(Files.Rget(configPath, "user")));   //返回是否自动登录
    }

    /**
     * 获取配置文件内的账号密码并且应用于软件内
     */
    public void fileSet() {
        user = Files.Rget(configPath, "user"); //用户名
        pass = Files.Rget(configPath, "pass"); //密码
    }


    /**
     * 判断服务器内有没有头像
     *
     * @param id 用户ID
     * @return 服务器中有没有头像
     */
    public boolean getUserHeadExists(String id) {
        try {
            return !Server.Loadweb(url + "GetUserHeadExists.php?id=" + id).equals("null");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 下载头像
     *
     * @param id 用户ID
     */
    public void downloadUserHead(String id) {
        files.DownloadImage(url + "GetUserHead.php?id=" + id, filePath + "/User/" + id + "/head.png");
    }

    /**
     * 判断路径中有没有头像
     *
     * @param id 用户ID
     * @return 路径中有没有头像
     */
    public boolean fileUserHeadExists(String id) {
        return files.exists(filePath + "/User/" + id + "/head.png");
    }

    /**
     * 获取头像的BitMap
     *
     * @param id 用户ID
     * @return 头像的BitMap
     */
    public Bitmap getUserHead(String id) {
        if (!fileUserHeadExists(id) && getUserHeadExists(id)) {
            downloadUserHead(id);
        }
        if (fileUserHeadExists(id)) {
            return BitmapFactory.decodeFile(filePath + "/User/" + id + "/head.png");
        } else {
            return null;
        }
    }

    public void setUserHead(String id, ImageView imageView, Activity activity) {
        new Thread(() -> {
            Bitmap userHead = getUserHead(id);
            if (userHead != null) {
                activity.runOnUiThread(() -> imageView.setImageBitmap(userHead));
            }
        }).start();
    }

    public boolean getGroupHeadExists(String group) {
        try {
            return !Server.Loadweb(url + "GetGroupHeadExists.php?group=" + group).equals("null");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void downloadGroupHead(String group) {
        files.DownloadImage(url + "GetGroupHead.php?group=" + group, filePath + "/Group/" + group + "/head.png");
    }

    public boolean fileGroupHeadExists(String group) {
        return files.exists(filePath + "/Group/" + group + "/head.png");
    }

    public Bitmap getGroupHead(String group) {
        if (!fileGroupHeadExists(group) && getGroupHeadExists(group)) {
            downloadGroupHead(group);
        }
        if (fileGroupHeadExists(group)) {
            return BitmapFactory.decodeFile(filePath + "/Group/" + group + "/head.png");
        } else {
            return null;
        }
    }

    public void setGroupHead(String group, ImageView imageView, Activity activity) {
        new Thread(() -> {
            Bitmap groupHead = getGroupHead(group);
            if (groupHead != null) {
                activity.runOnUiThread(() -> imageView.setImageBitmap(groupHead));
            }
        }).start();
    }

    public void downloadMessageImage(String id) {
        files.DownloadImage(url + "GetImage.php?id=" + id, filePath + "/Image/" + id + ".png");
    }

    public boolean fileMessageImageExists(String id) {
        return files.exists(filePath + "/Image/" + id + ".png");
    }

    public Bitmap getMessageImage(String id) {
        if (!fileMessageImageExists(id)) {
            downloadMessageImage(id);
        }
        if (fileMessageImageExists(id)) {
            return BitmapFactory.decodeFile(filePath + "/Image/" + id + ".png");
        } else {
            return null;
        }
    }

    public void setMessageImage(String id, ImageView imageView, Activity activity) {
        new Thread(() -> {
            Bitmap messageImage = getMessageImage(id);
            if (messageImage != null) {
                activity.runOnUiThread(() -> imageView.setImageBitmap(messageImage));
            }
        }).start();
    }

    /**
     * 按ID获取名字
     *
     * @param id 用户ID
     * @return 名字
     */
    public String getUserName(String id) {
        if (!nameMap.containsKey(id)) {
            try {
                nameMap.put(id, Server.Loadweb(url + "GetUserName.php?id=" + id));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nameMap.get(id);
    }

    /**
     * 获取冥人堂里的信息
     *
     * @return
     */
    public String deadList() {
        try {
            return Server.Loadweb(url + "GetDead.php");
        } catch (IOException e) {
            e.printStackTrace();
            return "网络错误";
        }
    }

    public void setUserName(String name){
        try{
            Server.Loadweb(url+"SetUserName.php?user="+user+"&pass="+pass+"&name="+name);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean uploadUserHead(File file) {
        return Server.Upload(file,url+"UploadUserHead.php?user="+user+"&pass="+pass);
    }

    //发送图片
    public void sendImage(File file,String group){
        try {
            System.out.println(file.getPath());
            Server.Upload(file,url+"Send.php?user="+user+"&pass="+pass+"&group="+group+"&type=image");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendImage(InputStream is, String group){
        try {
            Server.Upload(is,url+"Send.php?user="+user+"&pass="+pass+"&group="+group+"&type=image");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
