package com.dd.nanami;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dd.nanami.contact.Contact;
import com.dd.nanami.contact.Group;
import com.dd.nanami.function.Control;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class Chat extends AppCompatActivity {

    String chatName;
    String type;
    long chatId;

    Control control;                    //控制方法
    Activity activity;

    LayoutInflater factory;

    Thread refreshThread;

    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = (Control) getApplication();       //获取控制方法
        activity = this;
        Intent intent = getIntent();
        chatName = intent.getStringExtra("chatName");
        type = intent.getStringExtra("type");
        chatId = intent.getLongExtra("chatId",0);
        switch (type){
            case "Group":
                contact = new Group(chatId,chatName,control);
                break;
            default:
                System.out.println("未知错误");
                break;
        }

        factory=LayoutInflater.from(this);     //获取主界面容器

        //标题栏
        View actionBarEntryView=factory.inflate(R.layout.view_title,new LinearLayout(this),false);      //新建标题栏布局
        ActionBar actionBar=getSupportActionBar();          //获取标题栏对象
        assert actionBar!=null;                             //防止发生发空指针
        actionBar.setCustomView(actionBarEntryView);        //设置标题栏布局
        actionBar.setDisplayShowCustomEnabled(true);        //让自定义标题栏可以显示
        actionBar.setDisplayShowHomeEnabled(false);         //隐藏左上角的三个点
        actionBar.show();                                   //显示新的自定义标题栏

        final TextView actionText=actionBarEntryView.findViewById(R.id.path);       //获取标题文本控件

        actionText.setText(chatName);

        setContentView(R.layout.activity_chat);

        ScrollView scrollView = findViewById(R.id.Scroll);;
        LinearLayout messageList= findViewById(R.id.MessageList);

        //下拉
        final SwipeRefreshLayout Refresh=findViewById(R.id.Refresh);
        Refresh.setRefreshing(false);
        Refresh.setOnRefreshListener(() -> {
            try{
                String upMsg = contact.upRefresh();
                JSONObject jsonObject =new JSONObject(upMsg);
                if (jsonObject.getInt("row")!=0){
                    int height=scrollView.getChildAt(0).getHeight();
                    LinearLayout historyView=new LinearLayout(this);
                    historyView.setOrientation(LinearLayout.VERTICAL);
                    System.out.println(upMsg);
                    addMessage(jsonObject,historyView);
                    messageList.addView(historyView,0);
                    new Handler().postDelayed(() -> scrollView.scrollTo(0, scrollView.getChildAt(0).getHeight() -height - 200),200);
                }else {
                    System.out.println("区块到顶");
                }
            }catch(Exception e){
                Toast.makeText(getApplication(), ""+e, Toast.LENGTH_SHORT).show();
            }
            Refresh.setRefreshing(false);//刷新完成
        });

        refreshThread=new Thread(() -> {
            while (true) {
                try {
                    if (contact.ifRefresh()){
                        System.out.println("执行了");
                        final String msg=contact.getRefresh();
                        System.out.println(msg);
                        final JSONObject jsonObj=new JSONObject(msg);
                        activity.runOnUiThread(() -> {
                            try {
                                addMessage(jsonObj,messageList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            new Handler().postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN),200);
                        });
                        contact.row=contact.localRow;
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        refreshThread.start();

        final EditText content=findViewById(R.id.content);
        content.setOnClickListener(view -> {
            new Handler().postDelayed(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN),200);
        });
        Button send=findViewById(R.id.send);
        send.setOnClickListener(view -> {
            contact.seedMsg(content.getText().toString());
            content.setText("");
        });

        ImageView imageView = findViewById(R.id.image);
        imageView.setOnClickListener((view)->{
            Intent i = new Intent();
            /* 开启Pictures画面Type设定为image */
            i.setType("image/*");
            /* 使用Intent.ACTION_GET_CONTENT这个Action */
            i.setAction(Intent.ACTION_GET_CONTENT);
            /* 取得相片后返回本画面 */
            startActivityForResult(i, 1);
            //control.sendImage(new File("/storage/emulated/0/-0/Screenshot_2021-05-12-08-10-24-688_com.google.android.youtube.png"),String.valueOf(chatId));
        });
    }

    public void addMessage(JSONObject messageJson,LinearLayout messageList) throws JSONException {
        int row = messageJson.getInt("row");
        for(int i=1;i<=row;i++){
            try{
                JSONObject json=new JSONObject(messageJson.getString(String.valueOf(i)));
                String id= json.getString("id");
                String msg= json.getString("msg");
                String type= json.getString("type");
                System.out.println("id="+id+";消息="+msg+";消息类型="+type);
                View msgView = null;
                switch (type){
                    case "text":
                        msgView = textMessage(msg);
                        break;
                    case "image":
                        msgView = imageMessage(msg);
                        break;
                    default:
                        msgView = textMessage("未知消息格式");
                        break;
                }
                if (!id.equals(control.user)){
                    addMessageB(id,messageList,msgView);
                }else {
                    addMessageA(id,messageList,msgView);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("更新完成");
    }

    public void addMessageA(String id,LinearLayout messageList,View message){
        View entryView=factory.inflate(R.layout.view_message_a,null);
        ImageView head=entryView.findViewById(R.id.head_a);
        control.setUserHead(id,head,this);
        head.setOnClickListener((view)->{
            Intent intent = new Intent(this,UserInfo.class);
            intent.putExtra("id",Long.parseLong(id));
            startActivity(intent);
        });
        TextView nameText=entryView.findViewById(R.id.name_a);
        nameText.setText(control.getUserName(id));
        LinearLayout news = entryView.findViewById(R.id.news_a);
        news.addView(message);
        messageList.addView(entryView);
    }

    public void addMessageB(String id,LinearLayout messageList,View message){
        View entryView=factory.inflate(R.layout.view_message_b,null);
        ImageView head=entryView.findViewById(R.id.head_b);
        control.setUserHead(id,head,this);
        head.setOnClickListener((view)->{
            Intent intent = new Intent(this,UserInfo.class);
            intent.putExtra("id",Long.parseLong(id));
            startActivity(intent);
        });
        TextView nameText=entryView.findViewById(R.id.name_b);
        nameText.setText(control.getUserName(id));
        LinearLayout news = entryView.findViewById(R.id.news_b);
        news.addView(message);
        messageList.addView(entryView);
    }

    public View textMessage(String msg){
        TextView textView = new TextView(this);
        textView.setText(msg);
        return textView;
    }

    public View imageMessage(String imageId){
        View view = factory.inflate(R.layout.view_image, null);
        ImageView image=view.findViewById(R.id.ima);
        control.setMessageImage(imageId,image,this);
        return view;
    }

    @Override
    protected void onDestroy(){
        try {
            refreshThread.stop();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                InputStream is = cr.openInputStream(uri);
                control.sendImage(is,String.valueOf(chatId));
                System.out.println(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}