package com.shichuang.ahomet.common.js;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.shichuang.ahomet.MainActivity;
import com.shichuang.ahomet.R;
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.common.NewsCallback;
import com.shichuang.ahomet.entify.AMBaseDto;
import com.shichuang.ahomet.entify.MessageEvent;
import com.shichuang.ahomet.entify.OauthLogin;
import com.shichuang.open.tool.RxToastTool;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * 三方登录
 * Created by Administrator on 2018/1/10.
 */

public class JsOauthLogin {
    private static JsOauthLogin instance;
    private static final String OAUTH_SINA = "app_sina";
    private static final String OAUTH_QQ = "app_qq";
    private static final String OAUTH_WECHAT = "app_weixin";

    private Context mContext;
    private String oauthName = "";

    public static JsOauthLogin getInstance() {
        if (instance == null) {
            instance = new JsOauthLogin();
        }
        return instance;
    }

    /**
     * QQ登录
     */
    public void qqLogin(Context context) {
        mContext = context;
        oauthName = OAUTH_QQ;
        UMShareAPI.get(mContext).getPlatformInfo((Activity) mContext, SHARE_MEDIA.QQ, authListener);
    }

    /**
     * 微信登录
     */
    public void wechatLogin(Context context) {
        mContext = context;
        if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.WEIXIN)) {
            oauthName = OAUTH_WECHAT;
            UMShareAPI.get(mContext).getPlatformInfo((Activity) mContext, SHARE_MEDIA.WEIXIN, authListener);
        } else {
            RxToastTool.showLong("请安装微信客户端");
        }
    }

    /**
     * 微博登录
     */
    public void sinaLogin(Context context) {
        mContext = context;
        oauthName = OAUTH_SINA;
        UMShareAPI.get(mContext).getPlatformInfo((Activity) mContext, SHARE_MEDIA.SINA, authListener);
    }


    private UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            ((MainActivity) mContext).showLoading();
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            ((MainActivity) mContext).dismissLoading();
            if (data != null) {
                String uid = data.get("uid");
                String name = data.get("name");
                String gender = data.get("gender");
                String iconUrl = data.get("iconurl");
                oauthLogin(uid, name, gender, iconUrl);
            }
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ((MainActivity) mContext).dismissLoading();
            RxToastTool.showLong("onError：" + t.getMessage());
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ((MainActivity) mContext).dismissLoading();
            RxToastTool.showLong("onCancel");
        }
    };


    /**
     * 登录第三方登录信息接口
     */
    private void oauthLogin(String uid, String name, String gender, String iconUrl) {
        String url = Constants.oauthLoginUrl + "?oauth_name=" + oauthName + "&oauth_openid=" + uid + "&head_portrait=" + iconUrl + "&nickname=" + name;
        Log.d("test", url);
        OkGo.<AMBaseDto<OauthLogin>>get(url)
                //.cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .tag(mContext)
                .execute(new NewsCallback<AMBaseDto<OauthLogin>>() {
                    @Override
                    public void onStart(Request<AMBaseDto<OauthLogin>, ? extends Request> request) {
                        super.onStart(request);
                        ((MainActivity) mContext).showLoading();
                    }

                    @Override
                    public void onSuccess(Response<AMBaseDto<OauthLogin>> response) {
                        if (response.body().code == 0) {
                            EventBus.getDefault().post(new MessageEvent(response.body().data.getUrl()));
                            //EventBus.getDefault().post(new MessageEvent("login"));
                        } else {
                            RxToastTool.showLong(response.body().msg);
                        }
                    }

                    @Override
                    public void onError(Response<AMBaseDto<OauthLogin>> response) {
                        super.onError(response);
                        RxToastTool.showLong(mContext.getResources().getString(R.string.network_error));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ((MainActivity) mContext).dismissLoading();
                    }
                });
    }
}
