package com.dd.azusa.contact;

import com.dd.azusa.function.Control;
import com.dd.azusa.util.Server;

import java.io.IOException;

public class Group extends Contact{


    public Group(long chatId, String name, Control control) {
        super(chatId, name, control);
    }

    @Override
    public boolean ifRefresh() throws Exception {
        String serverRow= Server.Loadweb(control.url+"GroupIfRefresh.php?group="+chatId+"&row="+localRow);
        boolean refresh=!(serverRow.equals("null"));
        if(refresh){
            if(Integer.parseInt(serverRow)<row){
                row=0;
            }
            localRow=Integer.parseInt(serverRow);
        }
        return refresh;
    }

    @Override
    public String getMsg(String block, String row) throws Exception {
        return Server.Loadweb(control.url+"GetGroupContent.php?group="+chatId+"&block="+block+"&row="+row);
    }

    @Override
    public void seedMsg(String msg) {
        try {
            Server.Loadweb(control.url+"Send.php?user="+control.user+"&pass="+control.pass+"&group="+chatId+"&type=text"+"&msg="+msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}