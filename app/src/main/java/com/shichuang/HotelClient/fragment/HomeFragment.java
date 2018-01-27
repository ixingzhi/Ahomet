package com.shichuang.HotelClient.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.shichuang.HotelClient.R;
import com.shichuang.HotelClient.interf.OnTabReselectListener;
import com.shichuang.open.base.BaseFragment;

/**
 * Created by Administrator on 2017/11/22.
 */

public class HomeFragment extends BaseFragment implements OnTabReselectListener {
    private Fragment mCurFragment;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onTabReselect() {
        if (mCurFragment != null && mCurFragment instanceof OnTabReselectListener) {
            ((OnTabReselectListener) mCurFragment).onTabReselect();
        }
    }
}
