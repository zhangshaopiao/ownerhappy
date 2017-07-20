package com.zhang.zsp.zhangshaopiao;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zhang.zsp.zhangshaopiao.fragment.MainFragment;
import com.zhang.zsp.zhangshaopiao.fragment.RadioFragment;
import com.zhang.zsp.zhangshaopiao.fragment.RelaxFragment;
import com.zhang.zsp.zhangshaopiao.swipeback.Utils;

import utils.AppUtil;
import utils.T;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private MainFragment mMainFragment;
    private RelaxFragment mRelaxFragment;
    private RadioFragment mRadioFragment;

    private int finish = 0;
    private static final int HANDLER_FINISH = 0;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case HANDLER_FINISH:
                    if(finish == 0){
                        finish = 1;
                        mHandler.sendEmptyMessageDelayed(HANDLER_FINISH,1000);
                        T.showShort(MainActivity.this,"再按一次返回键退出！");
                    } else{
                        finish = 0;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(finish == 1) super.onBackPressed();
            else  mHandler.sendEmptyMessage(HANDLER_FINISH);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            T.showShort(this,"请稍后！");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mMainFragment == null) {
            mMainFragment = new MainFragment();
            transaction.add(R.id.container, mMainFragment);
        } else {
            transaction.show(mMainFragment);
        }
        transaction.commit();*/
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (mMainFragment == null) {
                mMainFragment = new MainFragment();
                transaction.add(R.id.container, mMainFragment);
            } else {
                transaction.show(mMainFragment);
            }
            transaction.commit();
        } else if (id == R.id.nav_music) {
            startMarket();
        } else if (id == R.id.nav_video) {
            shareSend();
        } else if (id == R.id.nav_file) {
            T.showShort(this, "请稍后！");
        } else if (id == R.id.nav_happy) {
            if (mRelaxFragment == null) {
                mRelaxFragment = new RelaxFragment();
                transaction.add(R.id.container, mRelaxFragment);
            } else {
                transaction.show(mRelaxFragment);
            }
            transaction.commit();
        } else if (id == R.id.nav_set) {
            T.showShort(this, "请稍后！");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mMainFragment != null) {
            transaction.hide(mMainFragment);
        }
        if (mRelaxFragment != null) {
            transaction.hide(mRelaxFragment);
        }
        if (mRadioFragment != null) {
            transaction.hide(mRadioFragment);
        }
    }

    public  void startMarket() {
        Uri uri = Uri.parse(String.format("market://details?id=%s", AppUtil.getPackageInfo(this).packageName));
        if (AppUtil.isIntentSafe(this, uri)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            T.showLong(this,"无法打开应用市场");
        }
    }

    private void shareSend(){
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT,"This is my text to send");
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent,"share"));
        }catch (Exception e){
            T.showLong(this,"分享失败！");
        }
    }

    public  void MainFragmentAction_relax(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        onNavigationItemSelected(navigationView.getMenu().getItem(1));
        navigationView.setCheckedItem(R.id.nav_happy);
    }

    public  void MainFragmentAction_radio(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragments(transaction);
        if (mRadioFragment == null) {
            mRadioFragment = new RadioFragment();
            transaction.add(R.id.container, mRadioFragment);
        } else {
            transaction.show(mRadioFragment);
        }
        transaction.commit();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_happy);
    }

}
