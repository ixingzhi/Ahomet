package com.shichuang.ahomet.common.js;

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
import com.shichuang.ahomet.entify.WxMakeOrder;
import com.shichuang.ahomet.entify.WxPay;
import com.shichuang.ahomet.widget.payment.OnRequestListener;
import com.shichuang.ahomet.widget.payment.wechat.pay.WechatPayTools;
import com.shichuang.open.tool.RxToastTool;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/1/11.
 */

public class JsWxPay {
    private static JsWxPay instance;
    private Context mContext;

    public static JsWxPay getInstance() {
        if (instance == null) {
            instance = new JsWxPay();
        }
        return instance;
    }

    public void pay(Context context, WxPay wxPay) {
        this.mContext = context;
        String orderNo = wxPay.getOrderNO();
        String price = wxPay.getPrice();
        String title = wxPay.getTitle();
        Log.d("test",orderNo+"   "+price+"    "+title);
        OkGo.<AMBaseDto<WxMakeOrder>>post(Constants.wxMakeOrderUrl)
                //.cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .tag(context)
                .params("out_trade_no", orderNo)
                .params("subject", title)
                .params("total_fee", price)
                .execute(new NewsCallback<AMBaseDto<WxMakeOrder>>() {
                    @Override
                    public void onStart(Request<AMBaseDto<WxMakeOrder>, ? extends Request> request) {
                        super.onStart(request);
                        ((MainActivity) mContext).showLoading();
                    }

                    @Override
                    public void onSuccess(Response<AMBaseDto<WxMakeOrder>> response) {
                        if (response.body().code == 0) {
                            if (response.body().data != null) {
                                pay(response.body().data);
                            }
                        } else {
                            RxToastTool.showShort(response.body().msg);
                        }
                    }

                    @Override
                    public void onError(Response<AMBaseDto<WxMakeOrder>> response) {
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

    private void pay(WxMakeOrder data) {
        WechatPayTools.wechatPayApp(mContext,
                data.getAppId(),
                data.getPartnerId(),
                data.getPrepayId(),
                data.getPackageValue(),
                data.getNonceStr(),
                data.getTimeStamp(),
                data.getSign(),
                onRequestListener);
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
