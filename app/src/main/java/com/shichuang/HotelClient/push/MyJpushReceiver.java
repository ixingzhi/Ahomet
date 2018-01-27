package com.shichuang.HotelClient.push;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2018/1/19.
 */

public class MyJpushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyJpushReceiver";
    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            // Toast.makeText(context, "JPush用户注册成功",
            // Toast.LENGTH_SHORT).show();
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // Toast.makeText(context, "接收到推送下来的自定义消息",
            // Toast.LENGTH_SHORT).show();

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            // Toast.makeText(context, "接收到推送下来的通知", Toast.LENGTH_SHORT).show();

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            // Toast.makeText(context, "用户点击打开了通知", Toast.LENGTH_SHORT).show();
            //openNotification(context, bundle);
        } else {

        }

    }

}
