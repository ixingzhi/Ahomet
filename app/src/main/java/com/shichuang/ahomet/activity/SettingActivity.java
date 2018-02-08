package com.shichuang.ahomet.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.shichuang.ahomet.R;
import com.shichuang.ahomet.common.AppUpdateUtils;
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.common.Convert;
import com.shichuang.ahomet.common.NewsCallback;
import com.shichuang.ahomet.entify.AMBaseDto;
import com.shichuang.ahomet.entify.Share;
import com.shichuang.open.base.BaseActivity;
import com.shichuang.open.base.WebPageActivity;
import com.shichuang.open.tool.RxActivityTool;
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

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private static final int PERMISSIONS_CODE = 0x11;
    private TextView tvCacheSize;

    private String[] needPermissions = {
            Manifest.permission.CALL_PHONE};

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        tvCacheSize = (TextView) findViewById(R.id.tv_cache_size);
        ((TextView) findViewById(R.id.tv_version_number)).setText("当前版本：" + getVersionNumber());
    }

    @Override
    public void initEvent() {
        findViewById(R.id.rl_wipe_cache).setOnClickListener(this);
        findViewById(R.id.rl_version_number).setOnClickListener(this);
        findViewById(R.id.rl_app_sharing).setOnClickListener(this);
        findViewById(R.id.rl_official_weibo).setOnClickListener(this);
        findViewById(R.id.rl_feedback).setOnClickListener(this);
        findViewById(R.id.rl_customer_service_telephone).setOnClickListener(this);
        findViewById(R.id.rl_merchant_service_telephone).setOnClickListener(this);
        findViewById(R.id.rl_about_us).setOnClickListener(this);
    }

    @Override
    public void initData() {
        calculateCacheSize();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_wipe_cache:
                showWipeCacheDialog();
                break;
            case R.id.rl_version_number:
                AppUpdateUtils.getInstance().update(mContext);
                break;
            case R.id.rl_app_sharing:
                getAppShareData();
                break;
            case R.id.rl_official_weibo:
                skipWeiBo();
                break;
            case R.id.rl_feedback:
                //showToast("暂无开放");
                WebPageActivity.newInstance(mContext, "用户反馈", Constants.feedbackUrl);
                break;
            case R.id.rl_customer_service_telephone:
                makingCalls("400-960-9456");
                break;
            case R.id.rl_merchant_service_telephone:
                makingCalls("400-889-0345");
                break;
            case R.id.rl_about_us:
                RxActivityTool.skipActivity(mContext, AboutUsActivity.class);
                break;
            default:
                break;
        }
    }


    private void showWipeCacheDialog() {
        new AlertDialog.Builder(mContext).setMessage("确定清空缓存吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cleanWipeCache();
                        tvCacheSize.setText("0KB");
                    }
                })
                .create().show();
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

    private void skipWeiBo() {
        if (RxAppTool.isInstallApp(mContext, "com.sina.weibo")) {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.sina.weibo", "com.sina.weibo.page.ProfileInfoActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            intent.putExtra("uid", "5679376657");
            startActivity(intent);
        } else {
            showToast("请安装新浪微博客户端");
        }
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

    /**
     * 拨打电话
     */
    private void makingCalls(final String phone) {
        new AlertDialog.Builder(mContext)
                .setMessage("拨打： " + phone + " ？")
                .setNegativeButton("取消", null)
                .setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SettingActivity.this, needPermissions, PERMISSIONS_CODE);
                            return;
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                        mContext.startActivity(intent);
                    }
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSIONS_CODE) {
            if (paramArrayOfInt[0] != PackageManager.PERMISSION_GRANTED) {
                showMissingPermissionDialog("请打开拨打电话权限");
            }
        }
    }

    private void showMissingPermissionDialog(String message) {
        new AlertDialog.Builder(mContext).setTitle("提示").setMessage(message)
                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startAppSettings();
                    }
                })
                .setNegativeButton("取消", null).create().show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}
