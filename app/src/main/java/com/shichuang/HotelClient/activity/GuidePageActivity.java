package com.shichuang.HotelClient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shichuang.HotelClient.MainActivity;
import com.shichuang.HotelClient.R;
import com.shichuang.HotelClient.common.Constants;
import com.shichuang.open.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/12.
 */

public class GuidePageActivity extends BaseActivity {
    private ViewPager viewPager;
    private LinearLayout llIndicator;
    private Button btnEnter;
    private List<View> viewList;
    private ImageView[] indicatorImgs;
    private static final int GUIDE_PAGE_COUNT = 6;
    private int[] imgResArr = new int[]{R.drawable.ic_guide_page_01, R.drawable.ic_guide_page_02,
            R.drawable.ic_guide_page_03, R.drawable.ic_guide_page_04, R.drawable.ic_guide_page_05, R.drawable.ic_guide_page_06};

    private String url;

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_page;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        url = getIntent().getStringExtra("url");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        llIndicator = (LinearLayout) findViewById(R.id.ll_indicator);
        btnEnter = (Button) findViewById(R.id.btn_enter);

        indicatorImgs = new ImageView[GUIDE_PAGE_COUNT];
        viewList = new ArrayList<View>(GUIDE_PAGE_COUNT);
        for (int i = 0; i < GUIDE_PAGE_COUNT; i++) {
            ImageView backgroundImage = new ImageView(this);
            backgroundImage.setBackgroundResource(imgResArr[i]);
            //backgroundImage.setScaleType(ScaleType.CENTER_CROP);
            viewList.add(backgroundImage);
            indicatorImgs[i] = new ImageView(this);
            if (i == 0) {
                indicatorImgs[i].setBackgroundResource(R.drawable.ic_indicators_selected);
            } else {
                indicatorImgs[i].setBackgroundResource(R.drawable.ic_indicators_unselected);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                layoutParams.setMargins(20, 0, 0, 0);
                indicatorImgs[i].setLayoutParams(layoutParams);
            }
            llIndicator.addView(indicatorImgs[i]);
        }

        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                setIndicator(position);
                if (position == GUIDE_PAGE_COUNT - 1) {
                    btnEnter.setVisibility(View.VISIBLE);
                } else {
                    btnEnter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public class GuidePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            View view = viewList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView(viewList.get(position));
        }
    }

    private void setIndicator(int targetIndex) {
        for (int i = 0; i < indicatorImgs.length; i++) {
            indicatorImgs[i].setBackgroundResource(R.drawable.ic_indicators_selected);
            if (targetIndex != i) {
                indicatorImgs[i].setBackgroundResource(R.drawable.ic_indicators_unselected);
            }
        }
    }

    @Override
    public void initEvent() {
        btnEnter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(GuidePageActivity.this, MainActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                GuidePageActivity.this.finish();
            }
        });
    }

    @Override
    public void initData() {
    }
}
