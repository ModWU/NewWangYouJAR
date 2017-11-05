package cn.yunbee.cn.wangyoujar.views;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class RefreshRotateView extends ImageView {
	
	private static final int REFRESH = 0X001;
	
	private static final int STOP = 0X002;
	
	public RefreshRotateView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	public RefreshRotateView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public RefreshRotateView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if(mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					if(msg.what == REFRESH) {
						mDegrees += 30;
						invalidate();
						if(mDegrees >= 360.0f) {
							mDegrees = 0.0f;
						}
						mHandler.sendEmptyMessageDelayed(REFRESH, mDelayTime);
					}
					
					if(msg.what == STOP) {
						RefreshRotateView.this.setVisibility(View.INVISIBLE);
						mHandler.removeMessages(REFRESH);
						mDegrees = 0.0f;
						invalidate();
					}
				};
			};
		}
	}
	
	private static long DEFAULT_DELAY_TIME = 100;
	
	private float mDegrees = 0.0f;
	
	private boolean isClockwise = true;
	
	private long mDelayTime = DEFAULT_DELAY_TIME;
	
	private Handler mHandler;
	
	public void startRefresh(long delayTime, boolean isClosewise) {
		this.setVisibility(View.VISIBLE);
		mDegrees = 0.0f;
		mDelayTime = delayTime; 
		isClockwise = isClosewise;
		mHandler.sendEmptyMessage(REFRESH);
	}
	
	public void startRefresh(long delayTime) {
		this.setVisibility(View.VISIBLE);
		mDegrees = 0.0f;
		mDelayTime = delayTime; 
		mHandler.sendEmptyMessage(REFRESH);
	}
	
	public void startRefresh() {
		startRefresh(DEFAULT_DELAY_TIME, isClockwise);
	}
	
	public void startRefresh(boolean isClosewise) {
		this.setVisibility(View.VISIBLE);
		mDegrees = 0.0f;
		mDelayTime = DEFAULT_DELAY_TIME; 
		this.isClockwise = isClosewise;
		mHandler.sendEmptyMessage(REFRESH);
	}
	
	public void stopRefresh() {
		mHandler.sendEmptyMessage(STOP);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.rotate(isClockwise ? mDegrees : -Math.abs(mDegrees) , getWidth()/2, getHeight()/2);
		super.onDraw(canvas);
	}

}
