package com.shichuang.HotelClient.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.shichuang.HotelClient.MainActivity;
import com.shichuang.HotelClient.common.Constants;
import com.shichuang.HotelClient.common.NewsCallback;
import com.shichuang.HotelClient.common.UserCache;
import com.shichuang.HotelClient.entify.AMBaseDto;
import com.shichuang.open.event.InitX5FinishedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by Administrator on 2017/11/24.
 */

public class SplashActivity extends AppCompatActivity {
    private boolean needFinish;
    private int index = 0;
    private boolean isFirstStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        isFirstStart = judgeFirstStart();
        if (!isFirstStart) {   // 如果是首次进入App，需等待X5加载完成再进入
            judgeIsLogin();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InitX5FinishedEvent event) {/* Do something */
        if (index == 0) {
            index++;
            judgeIsLogin();
        }
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
                        if (response.body() != null && response.body().data != null) {
                            startUp(response.body().data);
                        } else {
                            startUp(Constants.loginUrl);
                        }
                    }

                    @Override
                    public void onError(Response<AMBaseDto<String>> response) {
                        super.onError(response);
                        if (isFinishing()) return;
                        startUp(Constants.loginUrl);
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
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
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
