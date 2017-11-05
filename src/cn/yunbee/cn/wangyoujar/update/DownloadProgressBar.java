package cn.yunbee.cn.wangyoujar.update;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ProgressBar;
import cg.yunbee.cn.wangyoujar.utils.BitmapUtils;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;

public class DownloadProgressBar extends ProgressBar implements Runnable {
	
	private static final int DEFALUT_TEXT_SIZE = 12;//单位:sp
	
	private static final int DEFAULT_LOADING_COLOR = Color.parseColor("#40c4ff");
	
	private static final int DEFAULT_STOP_COLOR = Color.parseColor("#40c4ff");
	
	private static final int DEFAULT_DOWNLOAD_BORDER_SIZE = 1;
	
	private static final int DEFAULT_MINIMUM_HEIGHT = 2;
	
	private int mScreenW, mScreenH;
	
	private float mDentity;
	
	private float mScaleDentity;
	
	private int mTextSize = DEFALUT_TEXT_SIZE;
	
	private int mLoadingColor = DEFAULT_LOADING_COLOR;
	
	private int mProgressColor;
	
	private int mStopColor = DEFAULT_STOP_COLOR;
	
	private int mDownloadRectH;
	
	private int mDownloadRectW;
	
	private int mDownloadRectBorderSize = DEFAULT_DOWNLOAD_BORDER_SIZE;
	
	private Bitmap pgBitmap;
	
	private Canvas pgCanvas;
	
	private boolean isStop;
	
	private boolean isFinish;
	
	private boolean isDoOtherOnFinished;
	
	private Paint bgPaint;
	
	private Paint textPaint;
	
	private Paint pgPaint;
	
	private String mProgressText = "更新文件中";
	
	private Rect textRect;
	
	private RectF bgRectf;
	
	private Bitmap flikerBitmap;
	
	private float flickerLeft;
	
	private float flickerRight;
	
	private int radius;
	
	private BitmapShader bitmapShader;
	
	private Thread ballThread;
	
	
	public void setStop(boolean isStop) {
		this.isStop = isStop;
		mProgressColor = mStopColor;
		requestLayout();
		postInvalidate();
	}
	
	public void finish() {
		this.isStop = true;
		mProgressColor = mStopColor;
		isFinish = true;
		if(ballThread != null) {
			if(!ballThread.isInterrupted()) {
				ballThread.interrupt();
			}
		}
		requestLayout();
		postInvalidate();
	}
	
	public DownloadProgressBar(Context context) {
		this(context, null);
	}

	public DownloadProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	
	public DownloadProgressBar(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		Log.i("INFO", "-----------------DownloadProgressBar-----------------------");
		intData();
		intiAttrs(attrs);
		initPaint();
	}
	
	
	private void intiAttrs(AttributeSet attrs) {
		//不实现
	}

	private void intData() {
		Log.i("INFO", "-----------------intData-----------------------");
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mScreenW = metrics.widthPixels;
		mScreenH = metrics.heightPixels;
		mDentity = metrics.density;
		mScaleDentity = metrics.scaledDensity;
		
		Log.i("INFO", "mScreenW:" + mScreenW);
		Log.i("INFO", "mScreenH:" + mScreenH);
		
		mTextSize = (int) (mTextSize * mScaleDentity + 0.5f);
		mDownloadRectBorderSize = (int) (mDownloadRectBorderSize * mDentity + 0.5f);
		
		//宽度为屏幕的2/3
		mDownloadRectW = (int) (mScreenW / 3 * 2 + 0.5f);
		//高度为宽度的1/6
		mDownloadRectH = (int) (mDownloadRectW / 8 + 0.5f);
		if(mDownloadRectH < DEFAULT_MINIMUM_HEIGHT * mDentity) {
			mDownloadRectH = (int) (DEFAULT_MINIMUM_HEIGHT * mDentity + 0.5f);
		}
		
	}

	private void initPaint() {
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		bgPaint.setStyle(Paint.Style.STROKE);
		bgPaint.setStrokeWidth(mDownloadRectBorderSize);
		
		pgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		pgPaint.setStyle(Paint.Style.FILL);
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(mTextSize);
		
		textRect = new Rect();
		bgRectf = new RectF(mDownloadRectBorderSize, mDownloadRectBorderSize, mDownloadRectW - mDownloadRectBorderSize, mDownloadRectH - mDownloadRectBorderSize);
		
		mProgressColor = mLoadingColor;
		
		flikerBitmap = BitmapFactory.decodeResource(getResources(), ResourceUtils.getDrawableId(getContext(), "dyflicker"));
		
		int flikerBitmapWidth = (int) ((flikerBitmap.getWidth() * mDownloadRectH * 1.0f) / flikerBitmap.getHeight() + 0.5f);
		
		flikerBitmap = BitmapUtils.resizeImage(flikerBitmap, flikerBitmapWidth, mDownloadRectH);
		
		flickerLeft = -flikerBitmap.getWidth();
		
		pgBitmap = Bitmap.createBitmap(mDownloadRectW - mDownloadRectBorderSize, mDownloadRectH - mDownloadRectBorderSize, Bitmap.Config.ARGB_8888);
		pgCanvas = new Canvas(pgBitmap);
		
	}
	
	public void setDoOtherOnFinished(boolean isDoOther, String msg) {
		msg = (msg == null ? "请稍后..." : msg);
		float progressWidth = (getProgress() / (getMax() * 1.0f)) * getWidth();
		flickerLeft = -flikerBitmap.getWidth();
		flickerRight = progressWidth;
		isDoOtherOnFinished = isDoOther;
		setProgressText(msg);
	}
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.i("INFO", "--------------onMeasure--------------------");
		Log.i("INFO", "mDownloadRectW:" + mDownloadRectW);
		Log.i("INFO", "mDownloadRectH:" + mDownloadRectH);
		//固定宽高
		setMeasuredDimension(mDownloadRectW, mDownloadRectH);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		 //背景
        drawBackGround(canvas);

        //进度
        drawProgress(canvas);

         //进度text
        drawProgressText(canvas);

        //变色处理
        drawColorProgressText(canvas);
	}
	
	public void startRunBall() {
		if(ballThread != null && ballThread.isAlive()) {
			ballThread.interrupt();
		}
		ballThread = new Thread(this);
		ballThread.start();
	}
	
	private void drawColorProgressText(Canvas canvas) {
		textPaint.setColor(Color.WHITE);
		int tWidth = textRect.width();
		//int tHeight = textRect.height();
		float xCoordinate = (getWidth() - tWidth) / 2;
		float yCoordinate = (getHeight() - (textPaint.descent() + textPaint.ascent()))/2;
		float progressWidth = (getProgress() / (getMax() * 1.0f)) * getWidth();
		if(progressWidth > xCoordinate) {
			canvas.save(Canvas.CLIP_SAVE_FLAG);
			float right = Math.min(progressWidth, xCoordinate + tWidth * 1.1f);
			canvas.clipRect(xCoordinate, 0, right, getHeight());
			canvas.drawText(mProgressText, xCoordinate, yCoordinate, textPaint);
			canvas.restore();
		}
	}

	private void drawProgressText(Canvas canvas) {
		textPaint.setColor(mProgressColor);
		textPaint.getTextBounds(mProgressText, 0, mProgressText.length(), textRect);
		
		int tWidth = textRect.width();
		//int tHeight = textRect.height();
		float xCoordinate = (getWidth() - tWidth) / 2;
		float yCoordinate = (getHeight() - (textPaint.descent() + textPaint.ascent()))/2;//(getHeight() + tHeight) / 2;
		canvas.drawText(mProgressText, xCoordinate, yCoordinate, textPaint);
		
	}
	
	public void setProgressText(String text) {
		mProgressText = text;
		postInvalidate();
	}

	private int offset = 5;

	private Xfermode xfermode = new PorterDuffXfermode(Mode.SRC_ATOP);
	 private void drawProgress(Canvas canvas) {
		pgPaint.setColor(mProgressColor);
		
		float right = (getProgress() / (getMax() * 1.0f)) * getWidth();
		
		pgCanvas.save(Canvas.CLIP_SAVE_FLAG);
		pgCanvas.clipRect(0, 0, right, getHeight());
		pgCanvas.drawColor(mProgressColor);
		pgCanvas.restore();
		
		if(!isStop) {
			pgPaint.setXfermode(xfermode);
			int offsetHeight = (int)((mDownloadRectH - flikerBitmap.getHeight())/2.0f + 0.5f);
			pgCanvas.drawBitmap(flikerBitmap, flickerLeft, offsetHeight, pgPaint);
			if(isDoOtherOnFinished) {
				pgCanvas.drawBitmap(flikerBitmap, flickerRight, offsetHeight, pgPaint);
			}
			pgPaint.setXfermode(null);
		}
		
		bitmapShader = new BitmapShader(pgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		pgPaint.setShader(bitmapShader);
		canvas.drawRoundRect(bgRectf, radius, radius, pgPaint);
			
	}
	 
	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		if(progress >= getMax()) {
			mProgressText = "更新完成";
			invalidate();
		}
	}

	/**
     * 边框
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        bgPaint.setColor(mProgressColor);
        //left、top、right、bottom不要贴着控件边，否则border只有一半绘制在控件内,导致圆角处线条显粗
        canvas.drawRoundRect(bgRectf, radius, radius, bgPaint);
    }
	
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	
	@Override
	public void run() {
		
		try {
			while(!isFinish && !ballThread.isInterrupted()) {
				float offsetLen = offset * mDentity;
				float progressWidth = (getProgress() / (getMax() * 1.0f)) * getWidth();
				float widthMax = progressWidth;
				if(isDoOtherOnFinished) {
					flickerRight -= offsetLen;
					widthMax = progressWidth/2 - flikerBitmap.getWidth()/2;
				}
				flickerLeft += offsetLen;
				if(flickerLeft >= widthMax) {
					flickerLeft = -flikerBitmap.getWidth();
					flickerRight = progressWidth;
				}
				postInvalidate();
				
				if(isDoOtherOnFinished)
					Thread.sleep(25);
				else
					Thread.sleep(20);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
