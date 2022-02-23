package com.dd.nanami.contact;

import com.dd.nanami.function.Control;

public abstract class Contact {
    long chatId;
    String name;
    Control control;

    public int row=0;
    public int localRow=0;
    public int block=0;

    public Contact(long chatId, String name, Control control) {
        this.chatId = chatId;
        this.name = name;
        this.control = control;
    }

    //判断群消息有没有变化
    public abstract boolean ifRefresh() throws Exception;

    //获取群内最新区块的全部消息
    public String getRefresh() throws Exception {
        return getMsg("new",""+row);
    }

    //获取群内上一个区块的消息
    public String upRefresh() throws Exception {
        block+=1;
        return getMsg(""+block,"0");
    }

    //获取群内消息
    public abstract String getMsg(String block,String row) throws Exception;

    public abstract void seedMsg(String text);
}
