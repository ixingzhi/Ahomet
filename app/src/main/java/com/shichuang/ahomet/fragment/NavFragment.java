package com.shichuang.ahomet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.shichuang.ahomet.R;
import com.shichuang.ahomet.view.NavigationButton;
import com.shichuang.open.base.BaseFragment;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */

public class NavFragment extends BaseFragment implements View.OnClickListener {
    private NavigationButton mNavHome;
    private NavigationButton mNavNearby;
    private ImageView mNavDynamicPub;
    private NavigationButton mNavDynamic;
    private NavigationButton mNavMine;
    private Context mContext;
    private int mContainerId;
    private FragmentManager mFragmentManager;
    private NavigationButton mCurrentNavButton;
    private OnNavigationReselectListener mOnNavigationReselectListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_nav;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mNavHome = view.findViewById(R.id.nav_item_home);
        mNavNearby = view.findViewById(R.id.nav_item_nearby);
        mNavDynamicPub = view.findViewById(R.id.nav_item_dynamic_pub);
        mNavDynamic = view.findViewById(R.id.nav_item_dynamic);
        mNavMine = view.findViewById(R.id.nav_item_mine);

        mNavHome.init(R.drawable.tab_icon_new,
                R.string.main_tab_name_home,
                HomeFragment.class);
        mNavNearby.init(R.drawable.tab_icon_new,
                R.string.main_tab_name_nearby,
                NearbyFragment.class);
        mNavDynamic.init(R.drawable.tab_icon_new,
                R.string.main_tab_name_dynamic,
                HomeFragment.class);
        mNavMine.init(R.drawable.tab_icon_new,
                R.string.main_tab_name_mine,
                HomeFragment.class);
    }

    @Override
    public void initEvent() {
        mNavHome.setOnClickListener(this);
        mNavNearby.setOnClickListener(this);
        mNavDynamic.setOnClickListener(this);
        mNavMine.setOnClickListener(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View view) {
        if (view instanceof NavigationButton) {
            NavigationButton nav = (NavigationButton) view;
            doSelect(nav);
        } else if (view.getId() == R.id.nav_item_dynamic_pub) {

        }
    }

    public void setup(Context context, FragmentManager fragmentManager, int contentId, OnNavigationReselectListener listener) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mContainerId = contentId;
        mOnNavigationReselectListener = listener;

        // do clear
        clearOldFragment();
        // do select first
        doSelect(mNavHome);
    }

//    public void select(int index) {
//        if (mNavMe != null)
//            doSelect(mNavMe);
//    }

    @SuppressWarnings("RestrictedApi")
    private void clearOldFragment() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (transaction == null || fragments == null || fragments.size() == 0)
            return;
        boolean doCommit = false;
        for (Fragment fragment : fragments) {
            if (fragment != this && fragment != null) {
                transaction.remove(fragment);
                doCommit = true;
            }
        }
        if (doCommit)
            transaction.commitNow();
    }

    private void doSelect(NavigationButton newNavButton) {
        NavigationButton oldNavButton = null;
        if (mCurrentNavButton != null) {
            oldNavButton = mCurrentNavButton;
            if (oldNavButton == newNavButton) {
                onReselect(oldNavButton);
                return;
            }
            oldNavButton.setSelected(false);
        }
        Log.e("test","4");
        newNavButton.setSelected(true);
        doTabChanged(oldNavButton, newNavButton);
        mCurrentNavButton = newNavButton;
    }

    private void doTabChanged(NavigationButton oldNavButton, NavigationButton newNavButton) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (oldNavButton != null) {
            if (oldNavButton.getFragment() != null) {
                ft.detach(oldNavButton.getFragment());
            }
        }
        if (newNavButton != null) {
            if (newNavButton.getFragment() == null) {
                Fragment fragment = Fragment.instantiate(mContext,
                        newNavButton.getClx().getName(), null);
                ft.add(mContainerId, fragment, newNavButton.getTag());
                newNavButton.setFragment(fragment);
            } else {
                ft.attach(newNavButton.getFragment());
            }
        }
        ft.commit();
    }

    private void onReselect(NavigationButton navigationButton) {
        OnNavigationReselectListener listener = mOnNavigationReselectListener;
        if (listener != null) {
            listener.onReselect(navigationButton);
        }
    }

    public interface OnNavigationReselectListener {
        void onReselect(NavigationButton navigationButton);
    }
}
