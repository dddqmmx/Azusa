package com.dd.nanami.util;

import android.app.Activity;
import android.widget.Toast;

public class ActivityUtil {
    public static void toast(Activity activity, String text){
        Toast.makeText(activity,text,Toast.LENGTH_LONG).show();
    }
}
