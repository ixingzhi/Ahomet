package com.shichuang.HotelClient.common;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.shichuang.open.tool.RxToastTool;


/**
 * Created by Administrator on 2018/1/13.
 */

public class Utils {

    /**
     * 拨打电话
     */
    public static void makingCalls(final Context mContext, final String phone) {
        if (phone == null || phone.length() == 0) {
            RxToastTool.showLong("电话信息有误");
            return;
        } else {
            new AlertDialog.Builder(mContext)
                    .setMessage("拨打： " + phone + " ？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                RxToastTool.showLong("未获取到拨打电话权限，请到应用设置中设置");
                                return;
                            }
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                            mContext.startActivity(intent);
                        }
                    }).create().show();
        }
    }

}
