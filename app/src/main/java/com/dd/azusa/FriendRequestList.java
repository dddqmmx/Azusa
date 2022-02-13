package com.dd.azusa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dd.azusa.function.Control;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class FriendRequestList extends AppCompatActivity {

    Activity activity;

    Control control;                    //控制方法
    LinearLayout friendRequestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = (Control) getApplication();
        activity = this;
        setContentView(R.layout.activity_friend_request_list);
        friendRequestList = findViewById(R.id.friendRequestList);

        new Thread(()->{
            try {
                JSONObject json = new JSONObject(control.getFriendRequest());
                Iterator iterator = json.keys();
                while (iterator.hasNext()){
                    String key = (String) iterator.next();
                    String value = json.getString(key);
                    activity.runOnUiThread(()->{
                        addFriendRequest(key);
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void addFriendRequest(String id) {
        LayoutInflater factory=LayoutInflater.from(FriendRequestList.this);
        @SuppressLint("InflateParams") View entryView=factory.inflate(R.layout.view_reques, null);
        ImageView headImage = entryView.findViewById(R.id.head);
        control.setUserHead(id,headImage,this);
        TextView newsName=entryView.findViewById(R.id.Name);
        newsName.setText(control.getUserName(id));
        friendRequestList.addView(entryView);
    }
}