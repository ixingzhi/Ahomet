package com.shichuang.open.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.shichuang.open.common.UserAgentBuilder;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Administrator on 2018/1/9.
 */

public class X5WebView extends WebView {
    public X5WebView(Context context) {
        super(context);
        initWebViewSettings();
    }

    public X5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebViewSettings();
    }

    public X5WebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWebViewSettings();
    }

    private void initWebViewSettings() {
        WebSettings webSettings = getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setUserAgentString(webSettings.getUserAgentString() + UserAgentBuilder.ua());
        Log.d("test", "UserAgent:" + webSettings.getUserAgentString());
    }
}
