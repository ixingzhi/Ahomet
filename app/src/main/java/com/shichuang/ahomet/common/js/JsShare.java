package com.shichuang.ahomet.common.js;

import android.content.Context;
import android.util.Log;

import com.shichuang.ahomet.MainActivity;
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.entify.Share;
import com.shichuang.open.tool.RxToastTool;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * Created by Administrator on 2018/1/11.
 */

public class JsShare {
    private static JsShare instance;
    private Context mContext;

    public static JsShare getInstance() {
        if (instance == null) {
            instance = new JsShare();
        }
        return instance;
    }

    public void share(Context context, Share share) {
        if (share == null) {
            return;
        }
        mContext = context;
        String imgUrl = "";
        if (share.getImg().startsWith("http")) {
            imgUrl = share.getImg();
        } else {
            imgUrl = Constants.MAIN_ENGINE + share.getImg();
        }
        Log.d("test", share.getTitle() + "      " + imgUrl + "        " + share.getDigest() + "         " + share.getUrl());
        UMWeb web = new UMWeb(share.getUrl());
        web.setTitle(share.getTitle());//标题
        web.setThumb(new UMImage(context, imgUrl));  //缩略图
        web.setDescription(share.getDigest());//描述

        new ShareAction((MainActivity) context)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.SINA)
                .setCallback(shareListener)
                .open();
    }

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            RxToastTool.showShort("分享成功");
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            RxToastTool.showShort("分享出错：" + t.getMessage());
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };
}
