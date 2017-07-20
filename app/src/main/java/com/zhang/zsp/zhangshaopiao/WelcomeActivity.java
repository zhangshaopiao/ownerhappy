package com.zhang.zsp.zhangshaopiao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.zhang.zsp.zhangshaopiao.service.CleanerService;
import com.zhang.zsp.zhangshaopiao.service.CoreService;

import java.util.Random;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView mImageView;

    private Animation mFadeIn;
    private Animation mFadeInScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        mImageView = (ImageView)findViewById(R.id.welcome_image);
        startService(new Intent(this, CoreService.class));
        startService(new Intent(this, CleanerService.class));
        int i = new Random().nextInt(3);
        if(i == 0) mImageView.setImageResource(R.mipmap.welcome_1);
        else if(i == 1) mImageView.setImageResource(R.mipmap.welcome_2);
        else  mImageView.setImageResource(R.mipmap.welcome_3);
        initAnim();
    }

    private void initAnim() {
        mFadeIn = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in);
        mFadeIn.setDuration(1000);
        mFadeInScale = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in_scale);
        mFadeInScale.setDuration(2000);
        //mFadeInScale.setFillEnabled(true);
        //mFadeInScale.setFillAfter(true);
        mImageView.startAnimation(mFadeIn);
        mFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImageView.startAnimation(mFadeInScale);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFadeInScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
