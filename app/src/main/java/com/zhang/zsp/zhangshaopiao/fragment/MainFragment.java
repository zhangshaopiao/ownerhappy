package com.zhang.zsp.zhangshaopiao.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.zhang.zsp.zhangshaopiao.MainActivity;
import com.zhang.zsp.zhangshaopiao.MemoryCleanActivity;
import com.zhang.zsp.zhangshaopiao.R;
import com.zhang.zsp.zhangshaopiao.RubbishCleanActivity;
import com.zhang.zsp.zhangshaopiao.SoftwareManageActivity;
import com.zhang.zsp.zhangshaopiao.WelcomeActivity;
import com.zhang.zsp.zhangshaopiao.adapter.ViewPagerScroller;
import com.zhang.zsp.zhangshaopiao.model.SDCardInfo;

import java.util.Timer;
import java.util.TimerTask;

import utils.AppUtil;
import utils.StorageUtil;
import utils.T;


public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private TextView capacity;

    private Context mContext;

    private Timer timer;
    private Timer timer2;

    private com.zhang.zsp.zhangshaopiao.widget.ArcProgress mArcProcess;
    private com.zhang.zsp.zhangshaopiao.widget.ArcProgress nArcStore;

    private View mCardView1;
    private View mCardView2;
    private View mCardView3;
    private View mCardView4;

    private LinearLayout mLlDot;
    private View mIvRed;
    private ImageView mIvRed_image;
    private int mPitch;

    private ViewPager mViewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getActivity();
        init(view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        fillData();
    }

    private void init(View view) {
        mArcProcess = (com.zhang.zsp.zhangshaopiao.widget.ArcProgress) view.findViewById(R.id.arc_process);
        nArcStore = (com.zhang.zsp.zhangshaopiao.widget.ArcProgress) view.findViewById(R.id.arc_store);
        capacity = (TextView) view.findViewById(R.id.capacity);
        mViewPager = (ViewPager) view.findViewById(R.id.main_pager);
        mLlDot = view.findViewById(R.id.ll_dot);
        mIvRed = view.findViewById(R.id.iv_red);
        mIvRed_image = (ImageView) view.findViewById(R.id.iv_red_image);
        initDot();

        mViewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 2));
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        new ViewPagerScroller(this.getActivity()).initViewPagerScroll(mViewPager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int pixel = Math.round(mPitch*positionOffset)+position*mPitch;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIvRed.getLayoutParams();
                //通过改变小红点左边距，来移动小红点
                params.leftMargin = pixel;

                mIvRed.setLayoutParams(params);

                RelativeLayout.LayoutParams params_image = (RelativeLayout.LayoutParams) mIvRed_image.getLayoutParams();
                //通过改变小红点大小
                int size = getResources().getDimensionPixelSize(R.dimen.dot_size);
                params_image.width = size - (int)((0.5-Math.abs(positionOffset-0.5))*size/2);
                params_image.height = params_image.width;

                mIvRed_image.setLayoutParams(params_image);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initDot() {

        //屏幕适配，将dp值转换成Pixel值
        int size = getResources().getDimensionPixelSize(R.dimen.dot_size);
        //根据轮播图的数量添加小黑点
        for (int i = 0; i < 2; i++) {

            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.dot_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);

            if (i!=0) {
                //小黑点的间距
                params.leftMargin = size;
            }
            image.setLayoutParams(params);
            mLlDot.addView(image);

            //当添加到第二个小黑点时，计算两个小黑点间的距离，为小红点的移动做准备
            if (mLlDot.getChildCount()==2) {
                mLlDot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        mPitch = mLlDot.getChildAt(1).getLeft()-mLlDot.getChildAt(0).getLeft();
                        mLlDot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

            }

        }
    }


    private class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            final View view;
            view = getActivity().getLayoutInflater().inflate(position == 0 ? R.layout.include_main_card : R.layout.include_main_card2, null);
            mCardView1 = view.findViewById(R.id.card1);
            mCardView2 = view.findViewById(R.id.card2);
            mCardView3 = view.findViewById(R.id.card3);
            mCardView4 = view.findViewById(R.id.card4);
            mCardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position ==0) startActivity(MemoryCleanActivity.class,null);
                    else  T.showShort(getActivity(), position == 0? "1":"5");
                }
            });
            mCardView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position ==0) startActivity(RubbishCleanActivity.class,null);
                    else  T.showShort(getActivity(), position == 0? "2":"6");
                }
            });
            mCardView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position ==0) ((MainActivity)getActivity()).MainFragmentAction_radio();
                    else  T.showShort(getActivity(), position == 0? "3":"7");
                }
            });
            mCardView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position ==0) startActivity(SoftwareManageActivity.class,null);
                    else ((MainActivity)getActivity()).MainFragmentAction_relax();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    };


    private void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mContext, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void fillData() {
        timer = null;
        timer2 = null;
        timer = new Timer();
        timer2 = new Timer();

        long l = AppUtil.getAvailMemory(mContext);
        long y = AppUtil.getTotalMemory(mContext);
        final double x = (((y - l) / (double) y) * 100);

        mArcProcess.setProgress(0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mArcProcess.getProgress() >= (int) x) {
                            timer.cancel();
                        } else {
                            mArcProcess.setProgress(mArcProcess.getProgress() + 1);
                        }

                    }
                });
            }
        }, 50, 20);

        SDCardInfo mSDCardInfo = StorageUtil.getSDCardInfo();
        SDCardInfo mSystemInfo = StorageUtil.getSystemSpaceInfo(mContext);

        long nAvailaBlock;
        long TotalBlocks;
        if (mSDCardInfo != null) {
            nAvailaBlock = mSDCardInfo.free + mSystemInfo.free;
            TotalBlocks = mSDCardInfo.total + mSystemInfo.total;
        } else {
            nAvailaBlock = mSystemInfo.free;
            TotalBlocks = mSystemInfo.total;
        }

        final double percentStore = (((TotalBlocks - nAvailaBlock) / (double) TotalBlocks) * 100);

        capacity.setText(StorageUtil.convertStorage(TotalBlocks - nAvailaBlock) + "/" + StorageUtil.convertStorage(TotalBlocks));
        nArcStore.setProgress(0);

        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (nArcStore.getProgress() >= (int) percentStore) {
                            timer2.cancel();
                        } else {
                            nArcStore.setProgress(nArcStore.getProgress() + 1);
                        }

                    }
                });
            }
        }, 50, 20);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        timer.cancel();
        timer2.cancel();
        super.onDestroy();
    }

}
