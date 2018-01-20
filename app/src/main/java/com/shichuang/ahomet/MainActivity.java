package com.shichuang.ahomet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.shichuang.ahomet.common.AppUpdateUtils;
import com.shichuang.ahomet.common.js.JsDataParse;
import com.shichuang.ahomet.entify.MessageEvent;
import com.shichuang.open.base.BaseActivity;
import com.shichuang.open.tool.RxFileTool;
import com.shichuang.open.widget.RxEmptyLayout;
import com.shichuang.open.widget.X5ProgressBarWebView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends BaseActivity {
    private X5ProgressBarWebView mWebView;
    private RxEmptyLayout mEmptyLayout;
    private String mUrl;
    private long mExitTime;
    private boolean firstEnter = true;
    private ValueCallback<Uri> mUploadCallbackBelow;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;


    @Override
    public int getLayoutId() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mUrl = getIntent().getStringExtra("url");
        mEmptyLayout = (RxEmptyLayout) findViewById(R.id.empty_layout);
        mWebView = (X5ProgressBarWebView) findViewById(R.id.web_view);
        mWebView.addJavascriptInterface(new JsFunction(), "jsobj");
        mWebView.setWebViewClient(new MainActivity.WebClient());
        mWebView.setCallback(new X5ProgressBarWebView.Callback() {

            @Override
            public void setTitle(String title) {
            }

            /**
             * 16(Android 4.1.2) <= API <= 20(Android 4.4W.2)回调此方法
             */
            @Override
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                mUploadCallbackBelow = valueCallback;
                takePhoto();
            }

            /**
             * API >= 21(Android 5.0.1)回调此方法
             */
            @Override
            public void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                takePhoto();
            }
        });
        mWebView.loadUrl(mUrl);

        EventBus.getDefault().register(this);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void initData() {
        AppUpdateUtils.getInstance().update(mContext);
    }

    private class WebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            Log.d("test", "加载开始，当前url：" + s);
            if (firstEnter) {
                mEmptyLayout.show(RxEmptyLayout.NETWORK_LOADING);
            }
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            Log.d("test", "加载结束");
            if (firstEnter) {
                firstEnter = false;
                mEmptyLayout.hide();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String s) {
            return super.shouldOverrideUrlLoading(webView, s);
        }
    }

    private class JsFunction {
        @JavascriptInterface
        public void jsCallOC(final String str) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (!TextUtils.isEmpty(str)) {
                        Log.e("test", str);
                        JsDataParse.parse(mContext, str);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event != null && event.message != null && mWebView != null) {
            mWebView.clearHistory();
            mWebView.loadUrl(event.message);
            Log.d("test", "onEventMainThread:" + event.message);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);//完成回调
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList != null && selectList.size() > 0) {
                //针对5.0以上, 以下区分处理方法
                Uri uri = RxFileTool.getMediaUriFromPath(mContext, selectList.get(0).getPath());
                if (mUploadCallbackBelow != null) {
                    chooseBelow(uri);
                } else if (mUploadCallbackAboveL != null) {
                    chooseAbove(uri);
                } else {
                    showToast("发生错误");
                }
            }
        }
    }


    private void takePhoto() {
        String[] items = {"从相册选择", "取消"};
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this).setCancelable(false).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (i == 0) {
                    PictureSelector.create(MainActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(1)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else if (i == 1) {
                    cancelFilePathCallback();
                }
            }
        });
        mDialog.create().show();
    }

    /**
     * Android API >= 21(Android 5.0) 版本的回调处理
     */
    private void chooseAbove(Uri uri) {
        if (uri != null) {
            // 这里是针对从文件中选图片的处理, 区别是一个返回的URI, 一个是URI[]
            Uri[] results;
            results = new Uri[]{uri};
            mUploadCallbackAboveL.onReceiveValue(results);
        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
        }
        mUploadCallbackAboveL = null;
    }

    /**
     * Android API < 21(Android 5.0)版本的回调处理
     */
    private void chooseBelow(Uri uri) {
        if (uri != null) {
            // 这里是针对文件路径处理
            mUploadCallbackBelow.onReceiveValue(uri);
        } else {
            mUploadCallbackBelow.onReceiveValue(null);
        }
        mUploadCallbackBelow = null;
    }

    /**
     * 取消回调，目的：第二次点击可打开
     */
    private void cancelFilePathCallback() {
        if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        } else if (mUploadCallbackBelow != null) {
            mUploadCallbackBelow.onReceiveValue(null);
            mUploadCallbackBelow = null;
        }
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            OkGo.getInstance().cancelTag(mContext);  // 防止内存溢出，关闭当前页面时，防止有分享，登录等等一些网络操作
            mWebView.goBack();
            return;
        } else {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                showToast("再按一次离开");
                mExitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
            return;
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            try {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
                    parent.removeView(mWebView);
                }
                mWebView.removeAllViews();
                mWebView.destroy();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        UMShareAPI.get(this).release();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public static void newInstance(Context context, String url) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("url", url);
        context.startActivity(i);
    }
}
