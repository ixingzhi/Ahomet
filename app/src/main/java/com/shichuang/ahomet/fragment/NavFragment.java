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
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.view.NavigationButton;
import com.shichuang.open.base.BaseFragment;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */

public class NavFragment extends BaseFragment implements View.OnClickListener {
    private MainPageFragment mMainPageFragment;
    private NavigationButton mNavHome;
    private NavigationButton mNavNearby;
    private ImageView mNavDynamicPub;
    private NavigationButton mNavMember;
    private NavigationButton mNavMine;
    private Context mContext;
    private int mContainerId;
    private FragmentManager mFragmentManager;
    private NavigationButton mCurrentNavButton;
    private OnTabSelectedListener mOnTabSelectedListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_nav;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mNavHome = view.findViewById(R.id.nav_item_home);
        mNavNearby = view.findViewById(R.id.nav_item_nearby);
        mNavDynamicPub = view.findViewById(R.id.nav_item_dynamic_pub);
        mNavMember = view.findViewById(R.id.nav_item_member);
        mNavMine = view.findViewById(R.id.nav_item_mine);

        mNavHome.init(R.drawable.tab_icon_home,
                R.string.main_tab_name_home,
                null);
        mNavNearby.init(R.drawable.tab_icon_nearby,
                R.string.main_tab_name_nearby,
                null);
        mNavMember.init(R.drawable.tab_icon_member,
                R.string.main_tab_name_member,
                null);
        mNavMine.init(R.drawable.tab_icon_mine,
                R.string.main_tab_name_mine,
                null);
    }

    @Override
    public void initEvent() {
        mNavHome.setOnClickListener(this);
        mNavNearby.setOnClickListener(this);
        mNavDynamicPub.setOnClickListener(this);
        mNavMember.setOnClickListener(this);
        mNavMine.setOnClickListener(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View view) {
        if (view instanceof NavigationButton) {
            NavigationButton nav = (NavigationButton) view;
            doSelect(nav, true);
        } else if (view.getId() == R.id.nav_item_dynamic_pub) {
            showToast("暂未开放");
        }
    }

    public void setup(Context context, FragmentManager fragmentManager, int contentId, String url, OnTabSelectedListener listener) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mContainerId = contentId;
        mOnTabSelectedListener = listener;

        addFragment(url);
        // do clear
        // clearOldFragment();
        // do select first
        doSelect(mNavHome, false);
    }

    public void select(int index) {
        switch (index) {
            case 0:
                if (mNavHome != null)
                    doSelect(mNavHome, false);
                break;
            case 1:
                if (mNavNearby != null)
                    doSelect(mNavNearby, false);
                break;
            case 2:
                if (mNavMember != null)
                    doSelect(mNavMember, false);
                break;
            case 3:
                if (mNavMine != null)
                    doSelect(mNavMine, false);
                break;
            default:
                if (mNavHome != null)
                    doSelect(mNavHome, false);
                break;
        }
    }

    private void addFragment(String url) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("url", url != null && !"".equals(url) ? url : Constants.homeUrl);
        mMainPageFragment = (MainPageFragment) Fragment.instantiate(mContext,
                MainPageFragment.class.getName(), bundle);
        ft.add(mContainerId, mMainPageFragment, MainPageFragment.class.getName());
        ft.commit();
    }


//    @SuppressWarnings("RestrictedApi")
//    private void clearOldFragment() {
//        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        List<Fragment> fragments = mFragmentManager.getFragments();
//        if (transaction == null || fragments == null || fragments.size() == 0)
//            return;
//        boolean doCommit = false;
//        for (Fragment fragment : fragments) {
//            if (fragment != this && fragment != null) {
//                transaction.remove(fragment);
//                doCommit = true;
//            }
//        }
//        if (doCommit)
//            transaction.commitNow();
//    }

    private void doSelect(NavigationButton newNavButton, boolean isLoading) {
        NavigationButton oldNavButton = null;
        if (mCurrentNavButton != null) {
            oldNavButton = mCurrentNavButton;
            if (oldNavButton == newNavButton) {
                onReselect(oldNavButton);
                return;
            }
            oldNavButton.setSelected(false);
        }
        newNavButton.setSelected(true);
        //doTabChanged(oldNavButton, newNavButton);
        if (isLoading) {
            doTabPageLoad(newNavButton);
        }
        onSelected(newNavButton);
        mCurrentNavButton = newNavButton;
    }

    private void doTabPageLoad(NavigationButton newNavButton) {
        if (mMainPageFragment == null || mMainPageFragment.getWebView() == null) return;

        if (newNavButton == mNavHome) {
            mMainPageFragment.getWebView().loadUrl(Constants.homeUrl);
        } else if (newNavButton == mNavNearby) {
            mMainPageFragment.getWebView().loadUrl(Constants.nearbyUrl);
        } else if (newNavButton == mNavMember) {
            mMainPageFragment.getWebView().loadUrl(Constants.memberUrl);
        } else if (newNavButton == mNavMine) {
            mMainPageFragment.getWebView().loadUrl(Constants.mineUrl);
        }
    }

//    private void doTabChanged(NavigationButton oldNavButton, NavigationButton newNavButton) {
//        FragmentTransaction ft = mFragmentManager.beginTransaction();
//        if (oldNavButton != null) {
//            if (oldNavButton.getFragment() != null) {
//                ft.detach(oldNavButton.getFragment());
//            }
//        }
//        if (newNavButton != null) {
//            if (newNavButton.getFragment() == null) {
//                Bundle bundle = new Bundle();
//                if (newNavButton == mNavHome) {
//                    bundle.putString("url", Constants.homeUrl);
//                    bundle.putInt("position", 0);
//                } else if (newNavButton == mNavNearby) {
//                    bundle.putString("url", Constants.nearbyUrl);
//                    bundle.putInt("position", 1);
//                } else if (newNavButton == mNavMember) {
//                    bundle.putString("url", Constants.memberUrl);
//                    bundle.putInt("position", 2);
//                } else if (newNavButton == mNavMine) {
//                    bundle.putString("url", Constants.mineUrl);
//                    bundle.putInt("position", 3);
//                } else {
//                    bundle.putString("url", "");
//                }
//                Fragment fragment = Fragment.instantiate(mContext,
//                        newNavButton.getClx().getName(), bundle);
//                ft.add(mContainerId, fragment, newNavButton.getTag());
//                newNavButton.setFragment(fragment);
//            } else {
//                ft.attach(newNavButton.getFragment());
//            }
//        }
//        ft.commit();
//    }

    public MainPageFragment getMainPageFragemnt() {
        if (mMainPageFragment != null) {
            return mMainPageFragment;
        }
        return null;
    }

    private void onSelected(NavigationButton newNavButton) {
        OnTabSelectedListener listener = mOnTabSelectedListener;
        if (listener != null) {
            listener.onTabSelected(newNavButton);
        }
    }

    private void onReselect(NavigationButton navigationButton) {
        OnTabSelectedListener listener = mOnTabSelectedListener;
        if (listener != null) {
            listener.onTabReselected(navigationButton);
        }
    }

    public interface OnTabSelectedListener {
        void onTabSelected(NavigationButton navigationButton);

        void onTabReselected(NavigationButton navigationButton);
    }

}
