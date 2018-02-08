package com.shichuang.open.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.shichuang.open.R;
import com.shichuang.open.tool.RxActivityTool;
import com.shichuang.open.tool.RxFileTool;
import com.shichuang.open.widget.X5ProgressBarWebView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.List;

/**
 * Created by Administrator on 2018/1/9.
 */

public class WebPageActivity extends BaseActivity {
    private Toolbar mToolbar;
    private X5ProgressBarWebView mWebView;
    private String mUrl;
    private String mTitle;
    private ValueCallback<Uri> mUploadCallbackBelow;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    @Override
    public int getLayoutId() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        return R.layout.biz_actvitiy_web_page;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mTitle = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mWebView = (X5ProgressBarWebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebClient());
        mWebView.setCallback(new X5ProgressBarWebView.Callback() {

            @Override
            public void setTitle(String title) {
                if (mToolbar != null && TextUtils.isEmpty(mTitle)) {
                    mToolbar.setTitle(title);
                }
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                mUploadCallbackBelow = valueCallback;
                takePhoto();
            }

            @Override
            public void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                takePhoto();
            }
        });
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void initData() {
    }

    private class WebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String s) {
            return super.shouldOverrideUrlLoading(webView, s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            RxActivityTool.finish(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void takePhoto() {
        String[] items = {"从相册选择", "取消"};
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this).setCancelable(false).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (i == 0) {
                    PictureSelector.create(WebPageActivity.this)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
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
        super.onDestroy();
    }

    public static void newInstance(Context context, String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("url", url);
        RxActivityTool.skipActivity(context, WebPageActivity.class, bundle);
    }

}
