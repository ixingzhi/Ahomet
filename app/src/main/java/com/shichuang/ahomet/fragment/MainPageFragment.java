package com.shichuang.ahomet.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.shichuang.ahomet.R;
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.common.js.JsDataParse;
import com.shichuang.ahomet.event.MessageEvent;
import com.shichuang.ahomet.event.NavIndexEvent;
import com.shichuang.open.base.BaseFragment;
import com.shichuang.open.tool.RxAppTool;
import com.shichuang.open.tool.RxFileTool;
import com.shichuang.open.widget.RxEmptyLayout;
import com.shichuang.open.widget.X5ProgressBarWebView;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/11/22.
 */

public class MainPageFragment extends BaseFragment {
    private X5ProgressBarWebView mWebView;
    private RxEmptyLayout mEmptyLayout;
    private ValueCallback<Uri> mUploadCallbackBelow;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    private String mUrl;
    //    private int mPosition;
    private boolean firstEnter = true;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main_page;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mUrl = getArguments().getString("url");
//        mPosition = getArguments().getInt("position");
        mEmptyLayout = view.findViewById(R.id.empty_layout);
        initWebPage();
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void initData() {
    }

    private void initWebPage() {
        mWebView = mContentView.findViewById(R.id.web_view);
        mWebView.addJavascriptInterface(new JsFunction(), "jsobj");
        mWebView.setWebViewClient(new WebClient());
        mWebView.clearHistory();
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
    }

    private class WebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            if (firstEnter) {
                mEmptyLayout.show(RxEmptyLayout.NETWORK_LOADING);
            }
            Log.d("test", "加载开始，当前url：" + s);
            if (s.contains(Constants.homeUrl) || s.contains(Constants.nearbyUrl) || s.contains(Constants.memberUrl) || s.contains(Constants.mineUrl)) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.SHOW_NAV_BAR));
                if (s.contains(Constants.homeUrl)) {
                    EventBus.getDefault().post(new NavIndexEvent(0));
                } else if (s.contains(Constants.nearbyUrl)) {
                    EventBus.getDefault().post(new NavIndexEvent(1));
                } else if (s.contains(Constants.memberUrl)) {
                    EventBus.getDefault().post(new NavIndexEvent(2));
                } else if (s.contains(Constants.mineUrl)) {
                    EventBus.getDefault().post(new NavIndexEvent(3));
                }
            } else {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.HIDE_NAV_BAR));
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

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
//            String s = webResourceRequest.getUrl().toString();
//            if (s.contains(Constants.homeUrl) || s.contains(Constants.nearbyUrl) || s.contains(Constants.memberUrl) || s.contains(Constants.mineUrl)) {
//                try {
//                    URL url = new URL(injectIsParams());
//                    URLConnection connection = url.openConnection();
//                    return new WebResourceResponse(connection.getContentType(), connection.getHeaderField("encoding"), connection.getInputStream());
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
            return super.shouldInterceptRequest(webView, webResourceRequest);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
            return super.shouldInterceptRequest(webView, s);
        }
    }

//    public String injectIsParams() {
//        String url;
//        if (mPosition == 0) {
//            url = Constants.homeUrl;
//        } else if (mPosition == 1) {
//            url = Constants.nearbyUrl;
//        } else if (mPosition == 2) {
//            url = Constants.memberUrl;
//        } else if (mPosition == 3) {
//            url = Constants.mineUrl;
//        } else {
//            url = "";
//        }
//        return url;
//    }

    private class JsFunction {
        @JavascriptInterface
        public void jsCallOC(final String str) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (!TextUtils.isEmpty(str)) {
                        Log.e("test", "js交互：" + str);
                        JsDataParse.parse(mContext, str);
                    }
                }
            });
        }
    }

    private void takePhoto() {
        String[] items = {"从相册选择", "取消"};
        AlertDialog.Builder mDialog = new AlertDialog.Builder(getActivity()).setCancelable(false).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (i == 0) {
                    PictureSelector.create(MainPageFragment.this)
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == RESULT_OK) {   // 有选择结果
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
            } else {  // 没有选择结果，关闭回调
                cancelFilePathCallback();
            }
        }
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

    public X5ProgressBarWebView getWebView() {
        return mWebView;
    }

    @Override
    public void onDestroyView() {
//        if (mWebView != null) {
//            try {
//                ViewGroup parent = (ViewGroup) mWebView.getParent();
//                if (parent != null) {
//                    parent.removeView(mWebView);
//                }
//                mWebView.removeAllViews();
//                mWebView.destroy();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        super.onDestroyView();
    }
}
