package com.shichuang.ahomet;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mobstat.StatService;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.sdu.didi.openapi.DiDiWebActivity;
import com.shichuang.ahomet.common.LocationService;
import com.shichuang.open.Open;
import com.shichuang.open.base.BaseApplication;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/11/22.
 */

public class AhometApplication extends BaseApplication {
    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Open.getInstance().init(this);     // 初始化Open
        initOKGO();
        initPlatformConfig();
        initDiDi();
        initJpush();
        initLocation();
        initBaiDuStat();
    }

    private void initOKGO() {
        OkGo.getInstance().init(this)                               //必须调用初始化
                //.setOkHttpClient(builder.build())                 //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)                   //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)       //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                                  //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                        //全局公共头
//                .addCommonParams(params);                         //全局公共参数
    }

    private void initPlatformConfig() {
        Config.DEBUG = true;
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wx1773b107a60f360b", "a9615d31c1f66ab3e670b17338b2cb58");
        PlatformConfig.setQQZone("1105543666", "75cuUQ0EuKGqABhk");
        PlatformConfig.setSinaWeibo("2006352818", "cc508e298010aad2592c6cf10012da87", "http://www.creatrue.net");
    }

    private void initDiDi() {
        DiDiWebActivity.registerApp(this, "didi6348486261537437306D71735463534E",
                "8ac491819e8a81b866e7331f44af7f8f");
    }


    private void initJpush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    private void initLocation() {
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
    }

    private void initBaiDuStat() {
        // 开启自动埋点统计，为保证所有页面都能准确统计，建议在Application中调用。
        // 第三个参数：autoTrackWebview：
        // 如果设置为true，则自动track所有webview；如果设置为false，则不自动track webview，
        // 如需对webview进行统计，需要对特定webview调用trackWebView() 即可。
        // 重要：如果有对webview设置过webchromeclient，则需要调用trackWebView() 接口将WebChromeClient对象传入，
        // 否则开发者自定义的回调无法收到。
        StatService.autoTrace(this, true, true);
    }


}
