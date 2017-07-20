package com.zhang.zsp.zhangshaopiao;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.zhang.zsp.zhangshaopiao.adapter.RublishMemoryAdapter;
import com.zhang.zsp.zhangshaopiao.dialog.ProgressDialogFragment;
import com.zhang.zsp.zhangshaopiao.model.CacheListItem;
import com.zhang.zsp.zhangshaopiao.model.StorageSize;
import com.zhang.zsp.zhangshaopiao.service.CleanerService;
import com.zhang.zsp.zhangshaopiao.swipeback.BaseSwipeBackActivity;
import com.zhang.zsp.zhangshaopiao.views.textcounter.CounterView;
import com.zhang.zsp.zhangshaopiao.views.textcounter.formatters.DecimalFormatter;

import java.util.ArrayList;
import java.util.List;

import utils.StorageUtil;

public class RubbishCleanActivity extends BaseSwipeBackActivity implements CleanerService.OnActionListener{

    private CleanerService mCleanerService;

    private boolean mAlreadyScanned = false;
    private boolean mAlreadyCleaned = false;

    private ListView mListView;
    private TextView mEmptyView;
    private RelativeLayout header;
    private CounterView textCounter;
    private TextView sufix;
    private View mProgressBar;
    private TextView mProgressBarText;
    private LinearLayout bottom_lin;
    private Button clearButton;

    private RublishMemoryAdapter rublishMemoryAdapter;
    List<CacheListItem> mCacheListItem = new ArrayList<>();
    private ProgressDialogFragment mProgressDialogFragment;

    private static String mDialogTag = "basedialog";

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanerService = ((CleanerService.CleanerServiceBinder) service).getService();
            mCleanerService.setOnActionListener(RubbishCleanActivity.this);

            if (!mCleanerService.isScanning() && !mAlreadyScanned) {
                mCleanerService.scanCache();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCleanerService.setOnActionListener(null);
            mCleanerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubbish_clean);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        mListView.setEmptyView(mEmptyView);
        rublishMemoryAdapter = new RublishMemoryAdapter(this, mCacheListItem);
        mListView.setAdapter(rublishMemoryAdapter);
        int footerHeight = getResources().getDimensionPixelSize(R.dimen.footer_height);
        mListView.setOnItemClickListener(rublishMemoryAdapter);
        mListView.setOnScrollListener(new QuickReturnListViewOnScrollListener(QuickReturnType.FOOTER, null, 0, bottom_lin, footerHeight));

        bindService(new Intent(this, CleanerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.listview);
        mEmptyView = (TextView) findViewById(R.id.empty);
        header = (RelativeLayout) findViewById(R.id.header);
        textCounter = (CounterView) findViewById(R.id.textCounter);
        sufix = (TextView) findViewById(R.id.sufix);
        mProgressBar = (View) findViewById(R.id.progressBar);
        mProgressBarText = (TextView) findViewById(R.id.progressBarText);
        bottom_lin = (LinearLayout) findViewById(R.id.bottom_lin);
        clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCleanerService != null && !mCleanerService.isScanning() && !mCleanerService.isCleaning() && mCleanerService.getCacheSize() > 0) {
                    mAlreadyCleaned = false;

                    mCleanerService.cleanCache();
                }
            }
        });
    }

    @Override
    public void onScanStarted(Context context) {
        mProgressBarText.setText(R.string.scanning);
        showProgressBar(true);
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {
        mProgressBarText.setText(getString(R.string.scanning_m_of_n, current, max));

    }

    @Override
    public void onScanCompleted(Context context, List<CacheListItem> apps) {
        showProgressBar(false);
        mCacheListItem.clear();
        mCacheListItem.addAll(apps);
        rublishMemoryAdapter.notifyDataSetChanged();
        header.setVisibility(View.GONE);
        if (apps.size() > 0) {
            header.setVisibility(View.VISIBLE);
            bottom_lin.setVisibility(View.VISIBLE);

            long medMemory = mCleanerService != null ? mCleanerService.getCacheSize() : 0;

            StorageSize mStorageSize = StorageUtil.convertStorageSize(medMemory);
            textCounter.setAutoFormat(false);
            textCounter.setFormatter(new DecimalFormatter());
            textCounter.setAutoStart(false);
            textCounter.setStartValue(0f);
            textCounter.setEndValue(mStorageSize.value);
            textCounter.setIncrement(5f); // the amount the number increments at each time interval
            textCounter.setTimeInterval(50); // the time interval (ms) at which the text changes
            sufix.setText(mStorageSize.suffix);
            //  textCounter.setSuffix(mStorageSize.suffix);
            textCounter.start();
        } else {
            header.setVisibility(View.GONE);
            bottom_lin.setVisibility(View.GONE);
        }
        if (!mAlreadyScanned) {
            mAlreadyScanned = true;
        }

    }

    @Override
    public void onCleanStarted(Context context) {
        if (isProgressBarVisible()) {
            showProgressBar(false);
        }

        if (!RubbishCleanActivity.this.isFinishing()) {
            showDialogLoading();
        }
    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {
        dismissDialogLoading();
        Toast.makeText(context, context.getString(R.string.cleaned, Formatter.formatShortFileSize(this, cacheSize)), Toast.LENGTH_LONG).show();
        header.setVisibility(View.GONE);
        bottom_lin.setVisibility(View.GONE);
        mCacheListItem.clear();
        rublishMemoryAdapter.notifyDataSetChanged();
    }

    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private boolean isProgressBarVisible() {
        return mProgressBar.getVisibility() == View.VISIBLE;
    }

    public void showDialogLoading() {
        showDialogLoading(null);
    }

    public void showDialogLoading(String msg) {
        if (mProgressDialogFragment == null) {
            mProgressDialogFragment = ProgressDialogFragment.newInstance(0, null);
        }
        if (msg != null) {
            mProgressDialogFragment.setMessage(msg);
        }
        mProgressDialogFragment.show(getFragmentManager(), mDialogTag);

    }

    public void dismissDialogLoading() {
        if (mProgressDialogFragment != null) {
            mProgressDialogFragment.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
