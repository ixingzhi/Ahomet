package com.shichuang.ahomet.common.js;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.sdu.didi.openapi.DiDiWebActivity;
import com.shichuang.ahomet.activity.SettingActivity;
import com.shichuang.ahomet.common.Convert;
import com.shichuang.ahomet.common.JpushUtils;
import com.shichuang.ahomet.common.UserCache;
import com.shichuang.ahomet.common.Utils;
import com.shichuang.ahomet.entify.ALiPay;
import com.shichuang.ahomet.entify.AutoLogin;
import com.shichuang.ahomet.event.MessageEvent;
import com.shichuang.ahomet.entify.Navigation;
import com.shichuang.ahomet.entify.Platform;
import com.shichuang.ahomet.entify.Share;
import com.shichuang.ahomet.entify.User;
import com.shichuang.ahomet.entify.WxPay;
import com.shichuang.open.tool.RxActivityTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/10.
 */

public class JsDataParse {
    // 登录
    private static final String OAUTH_TYPE = "oauthlogin";
    private static final String QQ_LOGIN = "qq";
    private static final String WECHAT_LOGIN = "weixin";
    private static final String SINA_LOGIN = "sina";
    // 支付宝支付
    private static final String ALIPAY_TYPE = "alipay";
    // 微信支付
    private static final String WXPAY_TYPE = "wxpay";
    // 分享
    private static final String SHARE_TYPE = "share";
    // 滴滴
    private static final String DIDI_TYPE = "didi";
    // 退出登录
    private static final String LOGOUT_TYPE = "logout";
    // 自动登录
    private static final String LOGIN_TYPE = "login";
    // 拨打电话
    private static final String CALL_TYPE = "phonecall";
    // 设置
    private static final String SETTING_TYPE = "settings";
    // GPS定位
    private static final String GPS_TYPE = "needGPS";
    // 地图导航
    private static final String NAVIGATION_TYPE = "navigation";
    // 打开右侧菜单
    private static final String OPEN_MENU_TYPE = "rightScroll";

    public static class JsData<T> {
        public String type;
        public T data;
    }

    public static void parse(Context context, String tag) {
        // 解析数据，data类型可能是Model，也可能是String，需先判断type
        String platformType = "";
        try {
            JSONObject jsonObject = new JSONObject(tag);
            platformType = jsonObject.getString("type");
        } catch (JSONException e) {
            Log.d("test", e.getMessage());
            e.printStackTrace();
        }
        if ("".equals(platformType)) {
            throw new IllegalArgumentException("type 类型错误");
        }
        if (platformType.equals(OAUTH_TYPE)) {  // 第三方登录
            Type type = new TypeToken<JsData<Platform>>() {
            }.getType();
            JsData<Platform> jsData = Convert.fromJson(tag, type);
            if (jsData == null || jsData.type == null || jsData.data == null || jsData.data.getPlatform() == null)
                return;
            switch (jsData.data.getPlatform()) {
                case QQ_LOGIN:
                    JsOauthLogin.getInstance().qqLogin(context);
                    break;
                case WECHAT_LOGIN:
                    JsOauthLogin.getInstance().wechatLogin(context);
                    break;
                case SINA_LOGIN:
                    JsOauthLogin.getInstance().sinaLogin(context);
                    break;
                default:

                    break;
            }
        } else if (platformType.equals(ALIPAY_TYPE)) {   // 支付宝支付

            Type type = new TypeToken<JsData<ALiPay>>() {
            }.getType();
            JsData<ALiPay> jsData = Convert.fromJson(tag, type);
            JsALiPay.getInstance().pay(context, jsData.data);

        } else if (platformType.equals(WXPAY_TYPE)) {  // 微信支付

            Type type = new TypeToken<JsData<WxPay>>() {
            }.getType();
            JsData<WxPay> jsData = Convert.fromJson(tag, type);
            JsWxPay.getInstance().pay(context, jsData.data);

        } else if (platformType.equals(SHARE_TYPE)) {    // 分享

            Type type = new TypeToken<JsData<Share>>() {
            }.getType();
            JsData<Share> jsData = Convert.fromJson(tag, type);
            JsShare.getInstance().share(context, jsData.data);

        } else if (platformType.equals(DIDI_TYPE)) {   // 滴滴打车

            DiDiWebActivity.showDDPage(context, new HashMap<String, String>());

        } else if (platformType.equals(LOGOUT_TYPE)) {  // 退出登录

            UserCache.clear(context);
            JpushUtils.delJpushAlias(context);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_LOGIN_STATUS));
//            Type type = new TypeToken<JsData<String>>() {
//            }.getType();
//            JsData<String> jsData = Convert.fromJson(tag, type);
//           EventBus.getDefault().post(new MessageEvent(jsData.data));

        } else if (platformType.equals(LOGIN_TYPE)) {  // 自动登录
            Type type = new TypeToken<JsData<AutoLogin>>() {
            }.getType();
            JsData<AutoLogin> jsData = Convert.fromJson(tag, type);
            // 保存用户信息
            if (jsData != null && jsData.data != null) {
                User user = new User();
                user.setToken(jsData.data.getToken());
                UserCache.update(context, user);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_LOGIN_STATUS));
            }

        } else if (platformType.equals(CALL_TYPE)) {  // 拨打电话

            Type type = new TypeToken<JsData<String>>() {
            }.getType();
            JsData<String> jsData = Convert.fromJson(tag, type);
            Utils.makingCalls(context, jsData.data);

        } else if (platformType.equals(SETTING_TYPE)) {

            RxActivityTool.skipActivity(context, SettingActivity.class);

        } else if (platformType.equals(GPS_TYPE)) {

            EventBus.getDefault().post(new MessageEvent(MessageEvent.NEED_GPS));

        } else if (platformType.equals(NAVIGATION_TYPE)) {
            Type type = new TypeToken<JsData<Navigation>>() {
            }.getType();
            JsData<Navigation> jsData = Convert.fromJson(tag, type);
            if (jsData != null && jsData.data != null) {
                JsNavigation.getInstance().navigation(context, jsData.data);
            }
        } else if (platformType.equals(OPEN_MENU_TYPE)) {

            EventBus.getDefault().post(new MessageEvent(MessageEvent.OPEN_MENU));

        }
    }
}
