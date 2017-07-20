package com.zhang.zsp.zhangshaopiao.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zsp on 2017/7/18.
 */

public class MyProgressAnimation extends View {
    private int width;//设置高
    private int height;//设置高

    private Bitmap bitmap;//定义Bitmap
    private Canvas bitmapCanvas;//定义Bitmap的画布

    private Path mPath;    //定义路径
    private Paint mPathPaint;//定义路径的画笔

    private Paint mPaintCircle;//定义圆形的画笔

    private Paint mPaintText; //定义绘制文字的画笔

    //设置进度
    private int maxProgress = 100;
    private int currentProgress = 0;

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();//实时更新进度
    }


    private int count = 0;
    private static final int NEED_INVALIDATE = 0X6666;
    //操作UI主线程
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NEED_INVALIDATE:
                    //更新时间
                    count += 5;
                    if (count > 80) {
                        count = 0;
                    }
                    invalidate();
                    sendEmptyMessageDelayed(NEED_INVALIDATE, 50);
                    break;
            }

        }
    };

    public MyProgressAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化一个路径
        mPath = new Path();
        //初始化绘制路径的画笔
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setColor(Color.argb(0xff, 0xff, 0x69, 0x5a));
        mPathPaint.setStyle(Paint.Style.FILL);//设置为填充，默认为填充，这里我们还是定义下
        mPathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(Color.argb(0xff, 0xf8, 0x8e, 0x8b));

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(Color.argb(0xff, 0xFF, 0xF3, 0xF7));
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(50);

        handler.sendEmptyMessageDelayed(NEED_INVALIDATE, 50);
    }

    public MyProgressAnimation(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);//设置宽和高

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制Bitmap上的圆形
        bitmapCanvas.drawCircle(width / 2, height / 2, 150, mPaintCircle);
        //通过Path绘制贝塞尔曲线
        mPath.reset();
        mPath.moveTo(width, (height / 2 + 150) - (currentProgress * 300f / maxProgress));
        mPath.lineTo(width, height / 2 + 200);
        mPath.lineTo(count, height / 2 + 200);
        mPath.lineTo(count, (height / 2 + 150) - (currentProgress * 300f / maxProgress));
        for (int i = 0; i < 10; i++) {
            mPath.rQuadTo(20, 5, 40, 0);
            mPath.rQuadTo(20, -5, 40, 0);
        }
        mPath.close();
        //将贝塞尔曲线绘制到Bitmap的Canvas上
        bitmapCanvas.drawPath(mPath, mPathPaint);
        //将Bitmap绘制到View的Canvas上
        bitmapCanvas.drawText(currentProgress * 100f / maxProgress + "%", width / 2, height / 2, mPaintText);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
