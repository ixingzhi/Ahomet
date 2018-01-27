package com.shichuang.HotelClient.common.js;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.sdu.didi.openapi.DiDiWebActivity;
import com.shichuang.HotelClient.activity.SettingActivity;
import com.shichuang.HotelClient.common.Convert;
import com.shichuang.HotelClient.common.JpushUtils;
import com.shichuang.HotelClient.common.UserCache;
import com.shichuang.HotelClient.common.Utils;
import com.shichuang.HotelClient.entify.ALiPay;
import com.shichuang.HotelClient.entify.Platform;
import com.shichuang.HotelClient.entify.Share;
import com.shichuang.HotelClient.entify.User;
import com.shichuang.HotelClient.entify.WxPay;
import com.shichuang.open.tool.RxActivityTool;

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
            Log.d("test",e.getMessage());
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
//            Type type = new TypeToken<JsData<String>>() {
//            }.getType();
//            JsData<String> jsData = Convert.fromJson(tag, type);
//           EventBus.getDefault().post(new MessageEvent(jsData.data));

        } else if (platformType.equals(LOGIN_TYPE)) {  // 自动登录

            Type type = new TypeToken<JsData<User>>() {
            }.getType();
            JsData<User> jsData = Convert.fromJson(tag, type);
            // 保存用户信息
            if (jsData != null && jsData.data != null) {
                UserCache.update(context, jsData.data);
                // 设置极光推送别名
                JpushUtils.setJpushAlias(context, jsData.data.getPhone());
            }

        } else if (platformType.equals(CALL_TYPE)) {  // 拨打电话

            Type type = new TypeToken<JsData<String>>() {
            }.getType();
            JsData<String> jsData = Convert.fromJson(tag, type);
            Utils.makingCalls(context, jsData.data);

        } else if (platformType.equals(SETTING_TYPE)) {

            RxActivityTool.skipActivity(context, SettingActivity.class);

        }
    }
}
