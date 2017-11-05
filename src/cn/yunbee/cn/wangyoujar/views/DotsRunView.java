package cn.yunbee.cn.wangyoujar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class DotsRunView extends View {
	
	public static final int DEFAULT_SELECTED_COLOR = Color.WHITE;
	public static final int DEFAULT_UNSELECTED_COLOR = Color.GRAY;
	
	public static final float DEFAULT_DOT_MARGIN = 8;
	
	public static final int DEFAULT_DOT_COUNT = 3;
	
	public static final float DEFAULT_DOT_SIZE = 10;
	
	public static final long DEFAULT_DELAY_TIME = 200;
	
	public static final int START_RUN_MESSAGE = 0;
	public static final int STOP_RUN_MESSAGE = 1;
	
	private Paint mPaint;
	private int width, height;
	private float mDensity;
	private int screenWidth, screenHeight;
	
	private int currentSelectedColor = DEFAULT_SELECTED_COLOR;
	private int currentUnSelectedColor = DEFAULT_UNSELECTED_COLOR;
	private float dot_margin = DEFAULT_DOT_MARGIN;
	private float dot_size = DEFAULT_DOT_SIZE;
	private int dot_count = DEFAULT_DOT_COUNT;
	
	private boolean isNeedDraw = true;
	
	private float realUseWidth = 0;
	
	private int currentPosition = -1;
	
	private long currentDelayTime = DEFAULT_DELAY_TIME;
	
	private boolean isStop = true;
	
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case START_RUN_MESSAGE:
				if(isStop) {
					mHandler.removeMessages(START_RUN_MESSAGE);
					return;
				}
				
				currentPosition = ++currentPosition % dot_count;
				
				Log.i("INFO", "DotsRunView->position:" + currentPosition);
				
				invalidate();
				mHandler.sendEmptyMessageDelayed(START_RUN_MESSAGE, currentDelayTime);
				break;
				
			case STOP_RUN_MESSAGE:
				mHandler.removeMessages(START_RUN_MESSAGE);
				invalidate();
				break;
			}
		};
	};
	

	public DotsRunView(Context context) {
		super(context);
		initScreenInfo(context);
		initPaint();
	}
	
	public DotsRunView(Context context, int width, int height) {
		this(context);
		
		this.width = width;
		this.height = height;
	}
	
	private void initScreenInfo(Context context) {
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
		screenWidth = outMetrics.widthPixels;
		screenHeight = outMetrics.heightPixels;
		mDensity = outMetrics.density;
	}
	

	private void initPaint() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.GRAY);
		dot_margin *= mDensity;
		dot_size *= mDensity;
	}
	
	public void setDotMargin(float margin) {
		dot_margin = margin * mDensity;
	}
	
	public void setDotCount(int count) {
		dot_count = count;
	}
	
	public void setSelectedColor(int color) {
		currentSelectedColor = color;
	}
	
	public void setUnSelectedColor(int color) {
		currentUnSelectedColor = color;
	}
	
	public void startRun() {
		startRun(0, DEFAULT_DELAY_TIME);
	}
	
	public void startRun(long firstDelayTime) {
		startRun(firstDelayTime, DEFAULT_DELAY_TIME);
	}
	
	public void startRun(long firstDelayTime, long delayTime) {
		isStop = false;
		currentPosition = -1;
		currentDelayTime = delayTime;
		mHandler.removeMessages(START_RUN_MESSAGE);
		mHandler.sendEmptyMessageDelayed(START_RUN_MESSAGE, firstDelayTime);
	}
	
	public void stopRun() {
		isStop = true;
		currentPosition = -1;
		mHandler.sendEmptyMessage(STOP_RUN_MESSAGE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		isNeedDraw = true;
		
		int realWidth = 0;
		int realHeight = 0;
		
		if(width > 0) {
			realWidth = width;
		} else {
			if(realUseWidth < 0)
				realWidth = resolveSize(screenWidth, widthMeasureSpec);
			else 
				realWidth = resolveSize(Math.abs((int)(realUseWidth + 0.5f)), widthMeasureSpec);
		}
		
		if(height > 0) {
			realHeight = height;
		} else {
			if(realUseWidth < 0) {
				realHeight = resolveSize(screenHeight, heightMeasureSpec);
			} else {
				realHeight = resolveSize(Math.abs((int)(dot_size + 0.5f)), heightMeasureSpec);
			}
			
		}
		
		setMeasuredDimension(realWidth, realHeight);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		
		int minValue = Math.min(width, height);
		
		if(minValue <= 0) {
			isNeedDraw = false;
			return;
		}
		
		if(dot_size > minValue) {
			dot_size = minValue;
		}
		
		realUseWidth = dot_count * dot_size + (dot_count - 1) * dot_margin;
		
		float minMargin = 1 * mDensity;
		float minDotSize = 2 * mDensity;
		
		float perDelSize = 2;
		
		while(realUseWidth > width) {
			if(realUseWidth <= 0) {
				isNeedDraw = false;
				break;
			}
			
			if(dot_margin > minMargin) {
				dot_margin -= perDelSize;
			} else if(dot_size > minDotSize) {
				dot_size -= perDelSize;
			} else if(dot_count > DEFAULT_DOT_COUNT) {
				dot_count--;
			}
			
			if(dot_margin <= minMargin && dot_size <= minDotSize && dot_count <= DEFAULT_DOT_COUNT) {
				dot_margin = minMargin;
				dot_size = minDotSize;
				realUseWidth = dot_count * dot_size + (dot_count - 1) * dot_margin;
				if(realUseWidth > width) {
					isNeedDraw = false;
				}
				break;
			} else if(dot_margin < minMargin) {
				dot_margin = minMargin;
			} else if(dot_size < minDotSize) {
				dot_size = minDotSize;
			} else if(dot_count < DEFAULT_DOT_COUNT) {
				dot_count = DEFAULT_DOT_COUNT;
			}
			
			realUseWidth = dot_count * dot_size + (dot_count - 1) * dot_margin;
		}
		
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isNeedDraw) {
			float leftOffset = (width - realUseWidth) * 0.5f + dot_size * 0.5f;
			for(int i = 0; i < dot_count; i++) {
				if(i == currentPosition) {
					mPaint.setColor(currentSelectedColor);
				} else {
					mPaint.setColor(currentUnSelectedColor);
				}
				canvas.drawCircle(leftOffset + (dot_size + dot_margin) * i, height * 0.5f, dot_size * 0.5f, mPaint);
			}
		}
		
	}

}
