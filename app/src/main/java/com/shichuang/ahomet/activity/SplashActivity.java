package com.shichuang.ahomet.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.shichuang.ahomet.MainActivity;
import com.shichuang.ahomet.R;
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.common.NewsCallback;
import com.shichuang.ahomet.common.UserCache;
import com.shichuang.ahomet.entify.AMBaseDto;
import com.shichuang.ahomet.entify.MessageEvent;
import com.shichuang.ahomet.entify.OauthLogin;
import com.shichuang.open.tool.RxToastTool;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Administrator on 2017/11/24.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (judgeFirstStart()) {
//            startActivity(new Intent(SplashActivity.this, GuidePageActivity.class));
//            SplashActivity.this.finish();
//        } else {
//            judgeIsLogin();
//        }
        judgeIsLogin();
    }

//    private void checkRuntimePermission() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            startUp();
//            return;
//        }
//    }

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
        if (UserCache.isUserLogin(this)) {
            login(UserCache.getToken(this));
        } else {
            login("");
            //startUp(Constants.loginUrl);
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
                        if (response.body() != null && response.body().data != null) {
                            startUp(response.body().data);
                        } else {
                            startUp(Constants.loginUrl);
                        }
                    }

                    @Override
                    public void onError(Response<AMBaseDto<String>> response) {
                        super.onError(response);
                        startUp(Constants.loginUrl);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void startUp(final String url) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                SplashActivity.this.finish();
                overridePendingTransition(R.anim.push_center_in, 0);
            }
        }, 1000);
    }
}
