package com.shichuang.ahomet.activity;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.shichuang.ahomet.R;
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.common.Convert;
import com.shichuang.ahomet.common.NewsCallback;
import com.shichuang.ahomet.entify.AMBaseDto;
import com.shichuang.ahomet.entify.Share;
import com.shichuang.open.base.BaseActivity;
import com.shichuang.open.tool.RxAppTool;
import com.shichuang.open.tool.RxFileTool;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Administrator on 2018/1/13.
 */

public class SettingActivity extends BaseActivity {
    private TextView tvCacheSize;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        tvCacheSize = (TextView) findViewById(R.id.tv_cache_size);
        ((TextView) findViewById(R.id.tv_version_number)).setText("v" + getVersionNumber());
    }

    @Override
    public void initEvent() {
        findViewById(R.id.rl_wipe_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext).setMessage("确定清空缓存吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cleanWipeCache();
                                tvCacheSize.setText("0KB");
                            }
                        })
                        .create().show();
            }
        });
        findViewById(R.id.rl_app_sharing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAppShareData();
            }
        });
    }

    @Override
    public void initData() {
        calculateCacheSize();
    }

    private String getVersionNumber() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return info.versionName;
    }

    private void getAppShareData() {
        OkGo.<AMBaseDto<String>>get(Constants.appShareUrl)
                //.cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .tag(this)
                .execute(new NewsCallback<AMBaseDto<String>>() {
                    @Override
                    public void onStart(Request<AMBaseDto<String>, ? extends Request> request) {
                        super.onStart(request);
                        showLoading();
                    }

                    @Override
                    public void onSuccess(Response<AMBaseDto<String>> response) {
                        if (response.body().code == 0) {
                            String str = response.body().data;
                            Share share = Convert.fromJson(str, Share.class);
                            if (share != null) {
                                appShare(share);
                            }
                        } else {
                            showToast(response.body().msg);
                        }
                    }

                    @Override
                    public void onError(Response<AMBaseDto<String>> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoading();
                    }
                });
    }

    private void appShare(Share data) {
        // 解码url
        String url = "";
        try {
            url = URLDecoder.decode(data.getUrl(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("test", url);
        UMWeb web = new UMWeb(url);
        web.setTitle(data.getTitle());//标题
        web.setThumb(new UMImage(mContext, Constants.MAIN_ENGINE + data.getImg()));  //缩略图
        web.setDescription(data.getDigest());//描述

        new ShareAction(SettingActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.SINA)
                .setCallback(null)
                .open();
    }

    /**
     * 计算缓存的大小
     */
    private void calculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = mContext.getFilesDir();
        File cacheDir = mContext.getCacheDir();

        fileSize += RxFileTool.getDirSize(filesDir);
        fileSize += RxFileTool.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (RxAppTool.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = RxFileTool
                    .getExternalCacheDir(mContext);
            fileSize += RxFileTool.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = RxFileTool.formatFileSize(fileSize);
        tvCacheSize.setText(cacheSize);
    }

    /**
     * 清空缓存
     */
    private void cleanWipeCache() {
        RxFileTool.cleanInternalCache(this);
    }

}
