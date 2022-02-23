package com.dd.nanami;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.dd.nanami.function.Control;
import com.dd.nanami.util.Files;

import java.io.File;

public class FileList extends AppCompatActivity {

    public int obg;
    public String path="/storage/emulated/0";
    public TextView pathText;
    public Control control;
    public Activity activity;

    public  LinearLayout fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        LayoutInflater factory=LayoutInflater.from(FileList.this);     //获取主界面容器
        control = (Control) getApplication();       //获取控制方法
        //标题栏
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());      //主线程网络操作
        View actionBarEntryView=factory.inflate(R.layout.view_title,new LinearLayout(this),false);      //新建标题栏布局
        ActionBar actionBar=getSupportActionBar();          //获取标题栏对象
        assert actionBar!=null;                             //防止发生发空指针
        actionBar.setCustomView(actionBarEntryView);        //设置标题栏布局
        actionBar.setDisplayShowCustomEnabled(true);        //让自定义标题栏可以显示
        actionBar.setDisplayShowHomeEnabled(false);         //隐藏左上角的三个点
        actionBar.show();

        pathText=actionBarEntryView.findViewById(R.id.path);       //获取标题文本控件
        pathText.setText(path);

        setContentView(R.layout.activity_file_list);

        fileList = findViewById(R.id.FileList);

        refresh(path);
    }

    public void addFile(String fileName,int imageId,File file){
        LayoutInflater factory=LayoutInflater.from(activity);
        final LinearLayout entryView=(LinearLayout)factory.inflate(R.layout.view_news, null);
        ImageView avatar=entryView.findViewById(R.id.Avatar);
        avatar.setImageResource(imageId);
        TextView newsName=entryView.findViewById(R.id.Name);
        newsName.setText(fileName);
        fileList.addView(entryView);
        //System.out.println(file1.getPath());
        entryView.setOnClickListener((view)->{
            if (file.isDirectory()){
                refresh(file.toString());
            }else {
                String prefix = fileName.substring(fileName.lastIndexOf(".")+1);
                if (prefix.equals("png")){
                    Intent intent = new Intent();
                    intent.putExtra("filePath",file.toString());
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

    public void refresh(String path) {
        pathText.setText(path);
        fileList.removeAllViews();
        Files files = new Files();
        addFile("返回上一级文件夹",R.drawable.folde_up,new File(path).getParentFile());
        File[] file = files.orderByName(path);
        for (int i = 0; i <= file.length - 1; i++) {
            String filename = file[i].getName();
            if (file[i].isDirectory()) {
                addFile(filename, R.drawable.folde, file[i]);
            } else {
                addFile(filename, R.drawable.file, file[i]);
            }
        }
    }
}