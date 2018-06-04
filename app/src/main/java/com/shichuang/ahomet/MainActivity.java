package com.shichuang.ahomet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.shichuang.ahomet.common.AppUpdateUtils;
import com.shichuang.ahomet.common.Constants;
import com.shichuang.ahomet.common.JpushUtils;
import com.shichuang.ahomet.common.LocationService;
import com.shichuang.ahomet.common.NewsCallback;
import com.shichuang.ahomet.common.UserCache;
import com.shichuang.ahomet.entify.AMBaseDto;
import com.shichuang.ahomet.entify.OauthLogin;
import com.shichuang.ahomet.entify.User;
import com.shichuang.ahomet.event.MessageEvent;
import com.shichuang.ahomet.event.NavIndexEvent;
import com.shichuang.ahomet.event.SkipUrlEvent;
import com.shichuang.ahomet.fragment.MainPageFragment;
import com.shichuang.ahomet.fragment.NavFragment;
import com.shichuang.ahomet.view.NavigationButton;
import com.shichuang.open.base.BaseActivity;
import com.shichuang.open.common.UserAgentBuilder;
import com.shichuang.open.tool.RxGlideTool;
import com.shichuang.open.tool.RxTimeTool;
import com.shichuang.open.tool.RxToastTool;
import com.shichuang.open.widget.X5ProgressBarWebView;
import com.shichuang.open.widget.slidingmenu.SlidingMenu;
import com.tencent.smtt.sdk.WebSettings;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements NavFragment.OnTabSelectedListener {
    private NavFragment mNavBar;
    private FragmentManager mFragmentManager;
    private LinearLayout mllNavBar;

    private View menuLayout;
    private SlidingMenu menu;
    // menu layout
    private RelativeLayout mRlMenuUserInformation;
    private ImageView mIvAvatar;
    private TextView mTvUserName;
    private TextView mTvMemberExpirationDate;
    private LinearLayout mLlMenuHome;
    private LinearLayout mLlMenuNearby;
    private LinearLayout mLlMenuMine;
    private LinearLayout mLlMenuOrder;
    private LinearLayout mLlMenuCollect;
    private LinearLayout mLlMenuLoginStatus;
    private TextView mTvLoginStatus;
    private LinearLayout mLlMenuFeedback;
    private LinearLayout mLlMenuService;

    private X5ProgressBarWebView mWebView;

    private String mUrl;
    private long mExitTime;

    private LocationService locationService;

    private String[] needPermissions = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,    //通过WiFi或移动基站的方式获取用户粗略的经纬度信息，定位精度大概误差在30~1500米
            android.Manifest.permission.ACCESS_FINE_LOCATION,   //通过GPS芯片接收卫星的定位信息，定位精度达10米以内
            android.Manifest.permission.READ_PHONE_STATE   //访问电话状态
    };
    private static final int PERMISSON_REQUESTCODE = 0;
    private boolean isNeedCheck = true;

    @Override
    public int getLayoutId() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mUrl = getIntent().getStringExtra("url");
        mllNavBar = (LinearLayout) findViewById(R.id.ll_nav_bar);
        mFragmentManager = getSupportFragmentManager();
        mNavBar = ((NavFragment) mFragmentManager.findFragmentById(R.id.fag_nav));
        mNavBar.setup(this, mFragmentManager, R.id.main_container, mUrl, this);
        getWebView();
        initMenu();
        initLocation();
        EventBus.getDefault().register(this);
        getUserInfoByToken();
    }

    private void getWebView() {
        new Handler().postDelayed(new Runnable() {  // 创建Fragment提交事务是异步，需延迟获取WebView
            @Override
            public void run() {
                MainPageFragment fragment = mNavBar.getMainPageFragemnt();
                if (fragment != null && fragment.getWebView() != null) {
                    mWebView = fragment.getWebView();
                }
            }
        }, 100);
    }

    private void initMenu() {
        menuLayout = LayoutInflater.from(this).inflate(R.layout.layout_sliding_menu, null);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        // 设置触摸模式，可以选择全屏划出，或者是边缘划出，或者是不可划出
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        // 滑出时主页面显示的剩余宽度(400)
        //menu.setBehindWidth(400);
        // 设置侧滑栏完全展开之后，距离另外一边的距离，单位px，设置的越大，侧滑栏的宽度越小
        menu.setBehindOffset(200);
        // 设置渐变的程度，范围是0-1.0f,设置的越大，则在侧滑栏刚划出的时候，颜色就越暗，1.0f的时候，颜色为全黑
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(menuLayout);

        mRlMenuUserInformation = menuLayout.findViewById(R.id.rl_menu_user_information);
        mIvAvatar = menuLayout.findViewById(R.id.iv_avatar);
        mTvUserName = menuLayout.findViewById(R.id.tv_user_name);
        mTvMemberExpirationDate = menuLayout.findViewById(R.id.tv_member_expiration_date);
        mLlMenuHome = menuLayout.findViewById(R.id.ll_menu_home);
        mLlMenuNearby = menuLayout.findViewById(R.id.ll_menu_nearby);
        mLlMenuMine = menuLayout.findViewById(R.id.ll_menu_mine);
        mLlMenuOrder = menuLayout.findViewById(R.id.ll_menu_order);
        mLlMenuCollect = menuLayout.findViewById(R.id.ll_menu_collect);
        mLlMenuLoginStatus = menuLayout.findViewById(R.id.ll_menu_login_status);
        mTvLoginStatus = menuLayout.findViewById(R.id.tv_login_status);
        mLlMenuFeedback = menuLayout.findViewById(R.id.ll_menu_feedback);
        mLlMenuService = menuLayout.findViewById(R.id.ll_menu_service);
    }

    @Override
    public void initEvent() {
        menuLayout.findViewById(R.id.iv_menu_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
            }
        });
        skipPage(mLlMenuHome, Constants.homeUrl);
        skipPage(mLlMenuNearby, Constants.nearbyUrl);
        skipPage(mLlMenuMine, Constants.mineUrl);
        skipPage(mLlMenuOrder, Constants.orderUrl);
        skipPage(mLlMenuCollect, Constants.collectUrl);
        skipPage(mLlMenuFeedback, Constants.feedbackUrl);
        skipPage(mLlMenuService, Constants.serviceUrl);

        mRlMenuUserInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
                if (!UserCache.isUserLogin(mContext)) {
                    if (mWebView != null)
                        mWebView.loadUrl(Constants.loginUrl);
                }
            }
        });
        mLlMenuLoginStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
                if (UserCache.isUserLogin(mContext)) {
                    UserCache.clear(mContext);
                    if (mWebView != null)
                        mWebView.loadUrl(Constants.MAIN_ENGINE + "/ahomet/personal/mobile/log_out");
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_LOGIN_STATUS));
                } else {
                    if (mWebView != null)
                        mWebView.loadUrl(Constants.loginUrl);
                }
            }
        });
    }

    @Override
    public void initData() {
        AppUpdateUtils.getInstance().update(mContext);
    }

    private void skipPage(View view, final String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
                if (mWebView != null) {
                    mWebView.loadUrl(url);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }


    private void initLocation() {
        locationService = ((AhometApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event == null) {
            return;
        }
        if (MessageEvent.NEED_GPS.equals(event.message)) {
            checkPermissions(needPermissions);
        } else if (MessageEvent.UPDATE_LOGIN_STATUS.equals(event.message)) {
            getUserInfoByToken();
        } else if (MessageEvent.SHOW_NAV_BAR.equals(event.message)) {
            mllNavBar.setVisibility(View.VISIBLE);
        } else if (MessageEvent.HIDE_NAV_BAR.equals(event.message)) {
            mllNavBar.setVisibility(View.GONE);
        } else if (MessageEvent.OPEN_MENU.equals(event.message)) {
            if (menu != null) {
                menu.showMenu();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventNavIndex(NavIndexEvent event) {
        if (event != null && mNavBar != null) {
            mNavBar.select(event.navIndex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventSkipUrl(SkipUrlEvent event) {
        if (event != null && event.url != null && mWebView != null) {
            mWebView.loadUrl(event.url);
        }
    }

    private void getUserInfoByToken() {
        OkGo.<AMBaseDto<User>>get(Constants.getUserModelUrl)
                //.cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .tag(mContext)
                .params("token", UserCache.getToken(mContext))
                .execute(new NewsCallback<AMBaseDto<User>>() {
                    @Override
                    public void onStart(Request<AMBaseDto<User>, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<AMBaseDto<User>> response) {
                        if (response.body().code == 0) {
                            UserCache.update(mContext, response.body().data);
                            // 设置极光推送别名
                            JpushUtils.setJpushAlias(mContext, response.body().data.getPhone_num());
                        } else {
                            UserCache.clear(mContext);
                        }
                    }

                    @Override
                    public void onError(Response<AMBaseDto<User>> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        changeMenuLoginStatus();
                    }
                });
    }

    private void changeMenuLoginStatus() {
        if (UserCache.isUserLogin(mContext)) {
            mLlMenuMine.setVisibility(View.VISIBLE);
            mLlMenuOrder.setVisibility(View.VISIBLE);
            mLlMenuCollect.setVisibility(View.VISIBLE);

            User mUser = UserCache.user(mContext);
            if (mUser != null) {
                RxGlideTool.loadImageView(mContext, Constants.MAIN_ENGINE + mUser.getHead_pic(), mIvAvatar, R.drawable.ic_avatar_default);
                mTvUserName.setText(mUser.getNickname());
                if (mUser.getIs_member() == 1) {
                    if (mUser.getMember_end_time() != null && !"".equals(mUser.getMember_end_time())) {
                        mTvMemberExpirationDate.setText(
                                ("到期时间：" + RxTimeTool.stringFormat(mUser.getMember_end_time(), new SimpleDateFormat("yyyy.MM.dd"))));
                    }
                } else {
                    mTvMemberExpirationDate.setVisibility(View.GONE);
                }
                mTvLoginStatus.setText("退出登录");
            }

        } else {
            mLlMenuMine.setVisibility(View.GONE);
            mLlMenuOrder.setVisibility(View.GONE);
            mLlMenuCollect.setVisibility(View.GONE);

            mIvAvatar.setImageResource(R.drawable.ic_avatar_default);
            mTvUserName.setText("未登录");
            mTvMemberExpirationDate.setVisibility(View.GONE);
            mTvLoginStatus.setText("注册、登录");
        }
    }


    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                if (location.getLatitude() != 4.9E-324 && location.getLongitude() != 4.9E-324) {
                    locationService.stop();
                    isNeedCheck = false;
                    Log.i("test", "定位成功");
                    String longitude = String.valueOf(location.getLongitude());
                    String latitude = String.valueOf(location.getLatitude());
                    String province = location.getProvince();
                    String city = location.getCity();
                    String district = location.getDistrict();
                    String addr = location.getAddrStr();

                    StringBuffer sb = new StringBuffer(256);
                    sb.append(longitude);
                    sb.append(",");
                    sb.append(latitude);
                    sb.append(",");
                    sb.append(province);
                    sb.append(",");
                    sb.append(city);
                    sb.append(",");
                    sb.append(district);
                    sb.append(",");
                    sb.append(addr);

                    //UserAgentBuilder.setAddressInfo(addressInfo);
                    UserAgentBuilder.setAddressInfo(sb.toString());
                    if (mWebView != null) {
                        WebSettings webSettings = mWebView.getSettings();
                        String userAgent = webSettings.getUserAgentString();
                        if (userAgent != null) {
                            if (userAgent.indexOf("[") > 0) {
                                String subString = userAgent.substring(userAgent.indexOf("["), userAgent.length());
                                userAgent = userAgent.replace("ahometandroid" + subString, UserAgentBuilder.ua());
                            } else {
                                userAgent = userAgent.replace("ahometandroid", UserAgentBuilder.ua());
                            }
                        }
                        webSettings.setUserAgentString(userAgent);
                        Log.e("test", "定位成功，设置UserAgent：" + webSettings.getUserAgentString());
                    }
                }
            }
        }
    };

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        } else {
            if (locationService != null) {
                locationService.start();// 定位SDK
            }
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog("打开定位权限，获取更精确的数据");
                isNeedCheck = false;
            }
        } else if (requestCode == AppUpdateUtils.PERMISSON_REQUESTCODE) {
            if (paramArrayOfInt[0] != PackageManager.PERMISSION_GRANTED) {
                showMissingPermissionDialog("请打开读写手机存储权限");
            } else {
                AppUpdateUtils.getInstance().startDownOnGetPermission();
            }
        }
    }

    private void showMissingPermissionDialog(String message) {
        new AlertDialog.Builder(mContext).setTitle("提示").setMessage(message)
                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isNeedCheck = true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);//完成回调
    }

    @Override
    public void onTabSelected(NavigationButton navigationButton) {

//        MainPageFragment fragment = mNavBar.getMainPageFragemnt();
//        if (fragment != null && fragment.getWebView() != null) {
//            showToast("xxx");
//            mWebView = fragment.getWebView();
//        }

//        final MainPageFragment fragment = (MainPageFragment) navigationButton.getFragment();
//        new Handler().postDelayed(new Runnable() {  // 创建Fragment提交事务是异步，需延迟获取
//            @Override
//            public void run() {
//                if (fragment != null && fragment.getWebView() != null) {
//                    mWebView = fragment.getWebView();
//                    Log.d("test", mWebView.getX5WebViewExtension() != null ? "X5内核" : "不是 X5内核");
//                }
//            }
//        }, 200);

    }

    @Override
    public void onTabReselected(NavigationButton navigationButton) {
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            OkGo.getInstance().cancelTag(mContext);  // 防止内存溢出，关闭当前页面时，防止有分享，登录等等一些网络操作
            mWebView.goBack();
            return;
        } else {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                showToast("再按一次离开");
                mExitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
            return;
        }
    }

    @Override
    protected void onDestroy() {
        UMShareAPI.get(this).release();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
