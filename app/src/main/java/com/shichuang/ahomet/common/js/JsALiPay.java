package com.shichuang.ahomet.common.js;

import android.app.Activity;
import android.content.Context;

import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.entify.ALiPay;
import com.shichuang.ahomet.entify.MessageEvent;
import com.shichuang.ahomet.widget.payment.OnRequestListener;
import com.shichuang.ahomet.widget.payment.alipay.AliPayModel;
import com.shichuang.ahomet.widget.payment.alipay.AliPayTools;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/1/11.
 */

public class JsALiPay {
    private static JsALiPay instance;

    public static JsALiPay getInstance() {
        if (instance == null) {
            instance = new JsALiPay();
        }
        return instance;
    }

    public void pay(Context context, ALiPay aLiPay) {
        String orderNo = aLiPay.getOrderNO();
        String price = aLiPay.getPrice();
        String title = aLiPay.getTitle();
        //RxToastTool.showLong(orderNo+"     "+price+"    "+title);
        // 新版支付
        AliPayTools.aliPayV2((Activity) context,
                Constants.ALIPAY_APP_ID,//支付宝分配的APP_ID
                true,//是否是 RSA2 加密
                Constants.ALIPAY_RSA_PRIVATE,// RSA 或 RSA2 字符串
                Constants.ALIPAY_NOTIFY_URL,
                new AliPayModel(orderNo,//订单ID (唯一)
                        price,//价格
                        title,//商品名称
                        title),//商品描述详情 (用于显示在 支付宝 的交易记录里)
                onRequestListener);

//        // 旧版支付
//        AliPayTools.aliPayV1((Activity) context,
//                Constants.ALIPAY_PID, Constants.ALIPAY_SELLER,
//                Constants.ALIPAY_RSA_PRIVATE, Constants.ALIPAY_NOTIFY_URL,
//                new AliPayModel(orderNo,//订单ID (唯一)
//                        price,//价格
//                        title,//商品名称
//                        title),//商品描述详情 (用于显示在 支付宝 的交易记录里));
//                onRequestListener);
    }

    private OnRequestListener onRequestListener = new OnRequestListener() {

        @Override
        public void onSuccess(String s) {
            EventBus.getDefault().post(new MessageEvent(Constants.payResultUrl));
        }

        @Override
        public void onError(String s) {

        }
    };
}
