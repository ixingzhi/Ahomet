package com.shichuang.ahomet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.shichuang.ahomet.fragment.NavFragment;
import com.shichuang.ahomet.interf.OnTabReselectListener;
import com.shichuang.ahomet.view.NavigationButton;
import com.shichuang.open.base.BaseActivity;

public class MainActivity extends BaseActivity implements NavFragment.OnNavigationReselectListener {
    private NavFragment mNavBar;
    private FragmentManager mFragmentManager;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mFragmentManager = getSupportFragmentManager();
        mNavBar = ((NavFragment) mFragmentManager.findFragmentById(R.id.fag_nav));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNavBar.setup(this, mFragmentManager, R.id.main_container, this);   // 考虑到Fragment onActivityCreated 生命周期原因
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void onReselect(NavigationButton navigationButton) {
        Fragment fragment = navigationButton.getFragment();
        if (fragment != null
                && fragment instanceof OnTabReselectListener) {
            OnTabReselectListener listener = (OnTabReselectListener) fragment;
            listener.onTabReselect();
        }
    }
}
