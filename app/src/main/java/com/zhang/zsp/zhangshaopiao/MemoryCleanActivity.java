package com.zhang.zsp.zhangshaopiao;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.zhang.zsp.zhangshaopiao.adapter.ClearMemoryAdapter;
import com.zhang.zsp.zhangshaopiao.model.AppProcessInfo;
import com.zhang.zsp.zhangshaopiao.model.StorageSize;
import com.zhang.zsp.zhangshaopiao.service.CoreService;
import com.zhang.zsp.zhangshaopiao.swipeback.BaseSwipeBackActivity;
import com.zhang.zsp.zhangshaopiao.views.textcounter.CounterView;
import com.zhang.zsp.zhangshaopiao.views.textcounter.formatters.DecimalFormatter;

import java.util.ArrayList;
import java.util.List;

import utils.StorageUtil;
import utils.T;


public class MemoryCleanActivity extends BaseSwipeBackActivity implements CoreService.OnPeocessActionListener,View.OnClickListener{


    private static final String TAG = "MemoryCleanActivity";
    private CoreService mCoreService;
    private ClearMemoryAdapter mClearMemoryAdapter;
    private List<AppProcessInfo> mAppProcessInfos = new ArrayList<>();
    private ListView mListView;
    private CounterView textCounter;
    private LinearLayout bottom_lin;
    private TextView mProgressBarText;
    private View mProgressBar;
    private RelativeLayout header;
    private TextView sufix;
    private Button mCleanButton;

    public long Allmemory;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCoreService = ((CoreService.ProcessServiceBinder) service).getService();
            mCoreService.setOnActionListener(MemoryCleanActivity.this);
            mCoreService.scanRunProcess();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCoreService.setOnActionListener(null);
            mCoreService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_clean);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBarText = (TextView) findViewById(R.id.progressBarText);
        mListView = (ListView)findViewById(R.id.listview);
        bottom_lin = (LinearLayout)findViewById(R.id.bottom_lin);
        textCounter = (CounterView)findViewById(R.id.textCounter);
        mProgressBar = findViewById(R.id.progressBar);
        header = (RelativeLayout)findViewById(R.id.header);
        sufix = (TextView) findViewById(R.id.sufix);
        mCleanButton= (Button) findViewById(R.id.clear_button);
        mCleanButton.setOnClickListener(this);

        bindService(new Intent(this, CoreService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

        mClearMemoryAdapter = new ClearMemoryAdapter(this, mAppProcessInfos);
        mListView.setAdapter(mClearMemoryAdapter);

        int footerHeight = this.getResources().getDimensionPixelSize(R.dimen.footer_height);
        mListView.setOnScrollListener(new QuickReturnListViewOnScrollListener(QuickReturnType.FOOTER, null, 0, bottom_lin, footerHeight));

        textCounter.setAutoFormat(false);
        textCounter.setFormatter(new DecimalFormatter());
        textCounter.setAutoStart(false);
        textCounter.setIncrement(5f);
        textCounter.setTimeInterval(50);
    }

    @Override
    public void onScanStarted(Context context) {
        mProgressBarText.setText("扫描中");
        showProgressBar(true);
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {
        mProgressBarText.setText(getString(R.string.scanning_m_of_n, current, max));
    }

    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {
        mAppProcessInfos.clear();

        Allmemory = 0;
        for (AppProcessInfo appInfo : apps) {
            if (!appInfo.isSystem) {
                mAppProcessInfos.add(appInfo);
                Allmemory += appInfo.memory;
            }
        }


        refeshTextCounter();

        mClearMemoryAdapter.notifyDataSetChanged();
        showProgressBar(false);


        if (apps.size() > 0) {
            header.setVisibility(View.VISIBLE);
            bottom_lin.setVisibility(View.VISIBLE);
        } else {
            header.setVisibility(View.GONE);
            bottom_lin.setVisibility(View.GONE);
        }
    }

    private void refeshTextCounter() {
        StorageSize mStorageSize = StorageUtil.convertStorageSize(Allmemory);
        textCounter.setStartValue(0f);
        textCounter.setEndValue(mStorageSize.value);
        sufix.setText(mStorageSize.suffix);
        textCounter.start();
    }

    private void refeshTextCounter2(long killAppmemory) {
        StorageSize mStorageSize = StorageUtil.convertStorageSize(killAppmemory);
        textCounter.setStartValue(mStorageSize.value);
        mStorageSize = StorageUtil.convertStorageSize(Allmemory);
        textCounter.setEndValue(mStorageSize.value);
        textCounter.start();
    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {

    }

    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clear_button:
                onClickClear();
                break;
            default:
                break;
        }
    }

    public void onClickClear() {
        long killAppmemory = 0;
        long  Allmemory_ = Allmemory;

        for (int i = mAppProcessInfos.size() - 1; i >= 0; i--) {
            if (mAppProcessInfos.get(i).checked) {
                killAppmemory += mAppProcessInfos.get(i).memory;
                mCoreService.killBackgroundProcesses(mAppProcessInfos.get(i).processName);
                mAppProcessInfos.remove(mAppProcessInfos.get(i));
                mClearMemoryAdapter.notifyDataSetChanged();
            }
        }
        Allmemory = Allmemory - killAppmemory;
        T.showLong(this, "共清理" + StorageUtil.convertStorage(killAppmemory) + "内存");

        /*if(Allmemory > 0) refeshTextCounter();
        else */
        refeshTextCounter2(Allmemory_);
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
