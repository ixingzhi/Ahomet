package com.shichuang.HotelClient.activity;

import android.os.Bundle;
import android.view.View;

import com.shichuang.HotelClient.R;
import com.shichuang.HotelClient.common.Constants;
import com.shichuang.open.base.BaseActivity;
import com.shichuang.open.base.WebPageActivity;

/**
 * Created by Administrator on 2018/1/27.
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
    }

    @Override
    public void initEvent() {
        findViewById(R.id.rl_function_introduction).setOnClickListener(this);
        findViewById(R.id.rl_copyright).setOnClickListener(this);
        findViewById(R.id.rl_privacy_policy).setOnClickListener(this);
        findViewById(R.id.rl_user_agreement).setOnClickListener(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_function_introduction:
                openWebPage("功能介绍", Constants.functionIntroductionUrl);
                break;
            case R.id.rl_copyright:
                openWebPage("法律声明", Constants.copyrightUrl);
                break;
            case R.id.rl_privacy_policy:
                openWebPage("隐私政策", Constants.privacyPolicyUrl);
                break;
            case R.id.rl_user_agreement:
                openWebPage("用户协议", Constants.userAgreementUrl);
                break;
            default:
                break;
        }
    }

    private void openWebPage(String title, String url) {
        WebPageActivity.newInstance(mContext, title, url);
    }
}
