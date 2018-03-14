package com.shichuang.ahomet.common.js;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.shichuang.ahomet.entify.Navigation;
import com.shichuang.open.tool.RxAppTool;
import com.shichuang.open.tool.RxLogTool;
import com.shichuang.open.tool.RxToastTool;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 导航数据处理
 * Created by Administrator on 2018/3/13.
 */

public class JsNavigation {

    private static JsNavigation instance;
    private Context mContext;

    public static JsNavigation getInstance() {
        if (instance == null) {
            instance = new JsNavigation();
        }
        return instance;
    }


    public void navigation(Context context, final Navigation data) {
        mContext = context;
        if (data == null || data.getLat() == null || data.getLon() == null) {
            RxToastTool.showShort("位置信息为空");
            return;
        }
        // 判断手机安装了哪些导航
        final List<String> mList = new ArrayList<>();
        if (RxAppTool.isInstalled(context, "com.baidu.BaiduMap")) {
            mList.add("百度地图");
        }
        if (RxAppTool.isInstalled(context, "com.autonavi.minimap")) {
            mList.add("高德地图");
        }

        if (mList.size() == 0) {
            RxToastTool.showShort("您没有安装百度或高德地图");
        } else if (mList.size() == 1) {
            if ("百度地图".equals(mList.get(0))) {
                baidu(data.getLon(), data.getLat(), data.getAddress());
            } else {
                gaode(data.getLon(), data.getLat(), data.getAddress());
            }
        } else {
            final String[] items = new String[mList.size()];
            mList.toArray(items);
            new AlertDialog.Builder(context).setTitle("使用以下地图导航").setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if ("百度地图".equals(items[which])) {
                        baidu(data.getLon(), data.getLat(), data.getAddress());
                    } else {
                        gaode(data.getLon(), data.getLat(), data.getAddress());
                    }
                }
            }).create().show();
        }
    }

    private void baidu(String longitude, String latitude, String address) {
        String mLocationLat = "";
        String mLocationLon = "";
        String mLocationAddr = "";
        try {
            Intent intent = new Intent();
            intent = Intent.parseUri("intent://map/direction?" +
                    "origin=latlng:" + mLocationLat + "," + mLocationLon +
                    "|name:" + mLocationAddr +
                    "&destination=latlng:" + latitude + "," + longitude +
                    "|name:" + address +
                    "&mode=driving" +
                    "&src=Name|AppName" +
                    "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", 0);
            mContext.startActivity(intent);
        } catch (URISyntaxException e) {
            RxLogTool.d("URISyntaxException : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void gaode(String longitude, String latitude, String address) {
        String mLocationLat = "";
        String mLocationLon = "";
        String mLocationAddr = "";
        Intent intent = new Intent();
        intent.setData(Uri
                .parse("androidamap://route?" +
                        "sourceApplication=softname" +
                        "&slat=" + mLocationLat +
                        "&slon=" + mLocationLon +
                        "&dlat=" + latitude +
                        "&dlon=" + longitude +
                        "&dname=" + address +
                        "&dev=0" +
                        "&m=0" +
                        "&t=2"));
        mContext.startActivity(intent);
    }

}
