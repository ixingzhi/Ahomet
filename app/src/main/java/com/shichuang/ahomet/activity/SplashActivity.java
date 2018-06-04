package com.shichuang.ahomet.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.shichuang.ahomet.MainActivity;
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.common.NewsCallback;
import com.shichuang.ahomet.common.UserCache;
import com.shichuang.ahomet.entify.AMBaseDto;
import com.shichuang.open.event.InitX5FinishedEvent;
import com.shichuang.open.tool.RxDeviceTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/11/24.
 */

public class SplashActivity extends AppCompatActivity {
    private final int REQUEST_CODE_ASK_RUNTIME_PERMISSIONS = 124;

    private boolean needFinish;
    private int index = 0;
    private boolean isFirstStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        isFirstStart = judgeFirstStart();
        if (!isFirstStart) {   // 如果是首次进入App，需等待X5加载完成再进入
            checkRuntimePermission();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InitX5FinishedEvent event) {/* Do something */
        Log.e("test1", "onMessageEvent:" + index);
        if (index == 0 && isFirstStart) {
            index++;
            checkRuntimePermission();
        }
    }

    private void checkRuntimePermission() {
        // 6.0 以下手机直接启动
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            judgeIsLogin();
            return;
        }

        final List<String> permissionsList = new ArrayList<>();
        addPermissionIfRequired(permissionsList, Manifest.permission.READ_PHONE_STATE);
        boolean basePermission = addPermissionIfRequired(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // 不需要额外权限或者最基本的权限（存储）保证后，直接启动
        if (permissionsList.isEmpty() || basePermission) {
            judgeIsLogin();
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("我们需要一些基本权限来保互联家的正常运行")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] parr = permissionsList.toArray(new String[permissionsList.size()]);
                        ActivityCompat.requestPermissions(SplashActivity.this, parr,
                                REQUEST_CODE_ASK_RUNTIME_PERMISSIONS);
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE_ASK_RUNTIME_PERMISSIONS == requestCode) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }

            // 如果获得了基本存储权限，则允许执行
            if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                judgeIsLogin();
                return;
            }

            String msg = "需要存储权限，否则将无法正常使用互联家";
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage(msg).setPositiveButton("去设置", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean addPermissionIfRequired(List<String> permissionsList, String permission) {
        boolean allowed = true;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            allowed = false;
        }
        return allowed;
    }

    /**
     * 判断是否首次进入App
     *
     * @return
     */
    private boolean judgeFirstStart() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("startData", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            editor.putBoolean("isFirstRun", false);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    private void judgeIsLogin() {
        if (!RxDeviceTool.hasInternet()) {
            new AlertDialog.Builder(this).setMessage("没有网络连接，请检查网络")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SplashActivity.this.finish();
                        }
                    }).create().show();
            return;
        }
        if (UserCache.isUserLogin(this)) {
            login(UserCache.getToken(this));
        } else {
            login("");
        }
    }

    private void login(String token) {
        String url = Constants.stepLoginUrl + "?token=" + token;
        OkGo.<AMBaseDto<String>>get(url)
                //.cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .tag(this)
                .execute(new NewsCallback<AMBaseDto<String>>() {
                    @Override
                    public void onStart(Request<AMBaseDto<String>, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<AMBaseDto<String>> response) {
                        if (isFinishing()) return;
                        if (response.body().code == 0) {
                            startUp(response.body().data);
                        }else{
                            startUp(Constants.homeUrl);
                        }
                    }

                    @Override
                    public void onError(Response<AMBaseDto<String>> response) {
                        super.onError(response);
                        if (isFinishing()) return;
                        startUp(Constants.homeUrl);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void startUp(final String url) {
        if (isFirstStart) {
            Intent intent = new Intent(SplashActivity.this, GuidePageActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
            Log.e("test", "Go MainActivity");
        }
        needFinish = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needFinish) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        EventBus.getDefault().unregister(this);
    }
}
