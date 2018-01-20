package com.shichuang.ahomet.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.shichuang.ahomet.entify.AMBaseDto;
import com.shichuang.ahomet.entify.Version;
import com.shichuang.ahomet.service.AppUpdateService;
import com.shichuang.open.tool.RxAppTool;
import com.shichuang.open.tool.RxToastTool;

/**
 * App更新工具类
 *
 * @author Administrator
 */
public class AppUpdateUtils {
    private static final String FILE_NAME = "Ahomet";
    private static AppUpdateUtils mInstance;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public AppUpdateUtils() {
    }

    public static AppUpdateUtils getInstance() {
        if (mInstance == null) {
            mInstance = new AppUpdateUtils();
        }
        return mInstance;
    }

    public void update(Context context) {
        this.mContext = context;
        checkUpdate();
    }

    /**
     * 检测更新
     */
    private void checkUpdate() {
        // TODO Auto-generated method stub
        String url = Constants.appUpdateUrl;
        OkGo.<AMBaseDto<String>>get(url)
                //.cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .tag(mContext)
                .execute(new NewsCallback<AMBaseDto<String>>() {
                    @Override
                    public void onStart(Request<AMBaseDto<String>, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<AMBaseDto<String>> response) {
                        if (response.body().code == 0) {
                            // 对比版本号
                            Version version = Convert.fromJson(response.body().data, Version.class);
                            if (version == null)
                                return;
                            if (!("").equals(version.getAppversion())) { // 防止版本号为空
                                int systemVersion = Integer.parseInt(RxAppTool.getAppVersionName(mContext).replace(".", ""));
                                int serviceVersion = Integer.parseInt(version.getAppversion().replace(".", ""));
                                String description = version.getAppdescription();
                                Log.d("test", "systemVersion：" + systemVersion + "  serviceVersion:" + serviceVersion);
                                if (serviceVersion > systemVersion) {
                                    startUpdate(version.getAppurl(), description);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<AMBaseDto<String>> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void startUpdate(final String url, String description) {
        AlertDialog mDialog = new AlertDialog.Builder(mContext)
                .setTitle("检测到新版本，需更新")
                .setMessage(description)
                //.setNegativeButton("取消", null)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startService(url);
                    }
                }).create();
        mDialog.show();
    }

    private void startService(String url) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("新版本下载中...");
        //mProgressDialog.setMessage("");
        mProgressDialog.setMax(100);
        mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "后台下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mProgressDialog.show();

        // 启动Service 开始下载
        AppUpdateService.startUpdate(mContext, url, FILE_NAME, new AppUpdateService.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                if (mProgressDialog != null) {
                    mProgressDialog.setProgress(progress);
                }
            }

            @Override
            public void onSuccess(boolean isSuccess) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                // 失败提示
                if (isSuccess) {
                    RxToastTool.showShort("下载成功");
                } else {
                    RxToastTool.showShort("下载失败");
                }
            }
        });
    }

}