package com.dd.azusa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dd.azusa.function.Control;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {

    private int Menu = R.id.chat;
    private BottomNavigationView btmNavView;
    private ViewPager fragment_vp;
    private List<View> mViews;  //存放视图的数组

    Control control;                    //控制方法

    @SuppressLint({"InflateParams", "NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        control = (Control) getApplication();       //获取控制方法

        setContentView(R.layout.activity_main);

        fragment_vp = findViewById(R.id.fragment_vp);
        btmNavView = findViewById(R.id.bottomNavigation);

        fragment_vp.setLayerPaint(new Paint());

        LayoutInflater factory=LayoutInflater.from(Main.this);     //获取主界面容器
        //标题栏
        View actionBarEntryView=factory.inflate(R.layout.view_title,new LinearLayout(this),false);      //新建标题栏布局
        ActionBar actionBar=getSupportActionBar();          //获取标题栏对象
        assert actionBar!=null;                             //防止发生发空指针
        actionBar.setCustomView(actionBarEntryView);        //设置标题栏布局
        actionBar.setDisplayShowCustomEnabled(true);        //让自定义标题栏可以显示
        actionBar.setDisplayShowHomeEnabled(false);         //隐藏左上角的三个点
        actionBar.show();

        final TextView actionText=actionBarEntryView.findViewById(R.id.path);       //获取标题文本控件
        actionText.setText("消息");

        LayoutInflater inflater = getLayoutInflater();//获取布局对象管理

        //消息界面
        View view1 = inflater.inflate(R.layout.view_chat, null);//实例化view
        LinearLayout newsList = view1.findViewById(R.id.list);

        newsList.addView(newsView("世界聊天","Group",1));
        newsList.addView(newsView("桐生可可粉丝群","Group",2));
        newsList.addView(newsView("神奈川冲浪里群","Group",3));
        newsList.addView(newsView("ASoul黑粉群","Group",4));
        newsList.addView(newsView("阿梓粉丝群","Group",5));
        newsList.addView(newsView("原神交流群","Group",6));
        newsList.addView(newsView("陈睿粉丝群","Group",7));
        newsList.addView(newsView("反二次元群","Group",8));
        newsList.addView(newsView("顶真粉丝群","Group",9));

        //好友界面
        View view2 = inflater.inflate(R.layout.view_buddy, null);
        RelativeLayout friendRequestList = view2.findViewById(R.id.request);
        friendRequestList.setOnClickListener((view)->{
            startActivity(new Intent(Main.this,FriendRequestList.class));
        });


        //个人信息加设置的缝合界面
        View view3 = inflater.inflate(R.layout.view_account, null);

        RelativeLayout userView = view3.findViewById(R.id.UserView);
        userView.setOnClickListener((view)->{
            startActivity(new Intent(Main.this,MyInfo.class));
        });

        ImageView headImage = view3.findViewById(R.id.head);

        control.setUserHead(control.user,headImage,this);

        TextView nameText = view3.findViewById(R.id.name);
        nameText.setText(control.getUserName(control.user));

        TextView idText =view3.findViewById(R.id.id);
        idText.setText("ID : " + control.user);


        RelativeLayout deadList = view3.findViewById(R.id.DeadList);
        deadList.setOnClickListener((view)->{
            startActivity(new Intent(Main.this,DeadList.class));
        });

        RelativeLayout author = view3.findViewById(R.id.Author);
        author.setOnClickListener((view)->{
            startActivity(new Intent(Main.this,Author.class));
        });

        RelativeLayout setUp = view3.findViewById(R.id.set);
        setUp.setOnClickListener((view)->{
            startActivity(new Intent(Main.this,SetUp.class));
        });

        mViews= new ArrayList<>();//将要显示的布局存放到list数组
        mViews.add(view1);
        mViews.add(view2);
        mViews.add(view3);

        //从当前container中删除指定位置（position）的View;
        //第一：将当前视图添加到container中，第二：返回当前View
        //滑动到某个视图的事件
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override  //从当前container中删除指定位置（position）的View;
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(mViews.get(position));
            }

            @NonNull
            @Override  //第一：将当前视图添加到container中，第二：返回当前View
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(mViews.get(position));
                return mViews.get(position);
            }

            //滑动到某个视图的事件
            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.setPrimaryItem(container, position, object);
                switch (position) {
                    case 0:
                        btmNavView.setSelectedItemId(R.id.chat);
                        break;
                    case 1:
                        btmNavView.setSelectedItemId(R.id.buddy);
                        break;
                    case 2:
                        btmNavView.setSelectedItemId(R.id.account);
                        break;
                }
            }
        };
        fragment_vp.setAdapter(pagerAdapter);

        btmNavView.setOnNavigationItemSelectedListener(item -> {
            if (Menu != item.getItemId()) {
                switch (item.getItemId()){
                    case R.id.chat:
                        actionText.setText("消息");
                        fragment_vp.setCurrentItem(0);
                        Menu = R.id.chat;
                        return true;
                    case R.id.buddy:
                        actionText.setText("好友");
                        fragment_vp.setCurrentItem(1);
                        Menu = R.id.buddy;
                        return true;
                    case R.id.account:
                        actionText.setText("信息");
                        fragment_vp.setCurrentItem(2);
                        Menu = R.id.account;
                        return true;
                }
            }
            return false;
        });
    }

    public View newsView(String name,String type,long charId) {
        LayoutInflater factory=LayoutInflater.from(Main.this);
        @SuppressLint("InflateParams") View EntryView=factory.inflate(R.layout.view_news, null);
        ImageView avatar=EntryView.findViewById(R.id.Avatar);
        control.setGroupHead(String.valueOf(charId),avatar,this);
        avatar.setImageResource(R.drawable.world);
        TextView newsName=EntryView.findViewById(R.id.Name);
        EntryView.setOnClickListener(view -> {
            Intent intent = new Intent(Main.this,Chat.class);
            intent.putExtra("chatName",name);
            intent.putExtra("type",type);
            intent.putExtra("chatId",charId);
            startActivity(intent);
        });
        newsName.setText(name);
        return EntryView;
    }
}