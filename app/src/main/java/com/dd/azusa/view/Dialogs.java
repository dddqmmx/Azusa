package com.dd.azusa.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.azusa.R;

public class Dialogs extends Dialog {

    private LinearLayout buttonView;
    private Button yes;//确定按钮
    private View view;
    private Button no;//取消按钮
    private TextView titleView;
    private TextView messageView;

    private String title;
    private String message;
    private String yesText;
    private String noText;

    private boolean viewExists = true;

    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    private onNoOnclickListener noOnclickListener;//取消2按钮被点击了的监听器

    public Dialogs(Context context, int themeResId) {
        super(context, themeResId);
    }

    // 设置确定按钮监听
    public void setYesOnclickListener(onYesOnclickListener yesOnclickListener) {
        this.yesOnclickListener = yesOnclickListener;
    }

    // 设置取消按钮监听
    public void setNoOnclickListener(onNoOnclickListener noOnclickListener) {
        this.noOnclickListener = noOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog);
        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }

    // 初始化界面控件
    private void initView() {
        buttonView = findViewById(R.id.button_view);
        yes = findViewById(R.id.yes);
        view = findViewById(R.id.view);
        no = findViewById(R.id.no);
        titleView = findViewById(R.id.title);
        messageView = findViewById(R.id.message);
    }

    // 初始化界面控件的显示数据
    private void initData() {

        if (title != null) {
            titleView.setText(title);
        }

        if (message != null) {
            messageView.setText(message);
        }

        if (yesText != null) {
            yes.setText(yesText);
        } else {
            buttonView.removeView(yes);
            viewExists = false;
        }

        if (noText != null) {
            no.setText(noText);
        } else {
            buttonView.removeView(no);
            viewExists = false;
        }

        if (!viewExists) {
            buttonView.removeView(view);
        }

    }

    // 初始化界面的确定和取消按钮监听
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(v -> {
            if (yesOnclickListener != null) {
                yesOnclickListener.onYesOnclick();
            }
            this.dismiss();
        });

        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(v -> {
            if (noOnclickListener != null) {
                noOnclickListener.onNoOnclick();
            }
            this.dismiss();
        });
    }

    // 设置标题
    public void setTitle(String title) {
        this.title = title;
    }

    // 设置消息
    public void setMessage(String message) {
        this.message = message;
    }

    // 设置取消
    public void setYesText(String yesText){
        this.yesText=yesText;
    }

    // 设置取消
    public void setNoText(String noText){
        this.noText=noText;
    }

    public interface onYesOnclickListener {
        public void onYesOnclick();
    }

    public interface onNoOnclickListener {
        public void onNoOnclick();
    }
}
