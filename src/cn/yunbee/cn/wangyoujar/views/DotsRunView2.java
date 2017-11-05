package cn.yunbee.cn.wangyoujar.views;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class DotsRunView2 extends TextView {
	
	public static final int START_RUN_MESSAGE = 0;
	public static final int STOP_RUN_MESSAGE = 1;
	
	public static final long DEFAULT_DELAY_TIME = 400;
	
	private long currentDelayTime = DEFAULT_DELAY_TIME;
	
	private int currentPosition = -1;
	
	private int dot_count = DEFAULT_DOT_COUNT;
	
	public static final int DEFAULT_DOT_COUNT = 4;
	
	private boolean isStop = true;
	
	private String content;
	
	
	public void setContentText(String content) {
		this.content = content;
	}
	
	
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case START_RUN_MESSAGE:
				if(isStop) {
					mHandler.removeMessages(START_RUN_MESSAGE);
					return;
				}
				
				currentPosition = ++currentPosition % dot_count;
				Log.i("INFO", "DotsRunView2->position:" + currentPosition);
				
				StringBuffer dot = new StringBuffer(content == null ? "" : content);
				for(int i = 0; i < currentPosition; i++) {
					dot.append(".");
				}
				
				setText(dot.toString());
				mHandler.sendEmptyMessageDelayed(START_RUN_MESSAGE, currentDelayTime);
				break;
				
			case STOP_RUN_MESSAGE:
				mHandler.removeMessages(START_RUN_MESSAGE);
				setText("");
				break;
			}
		};
	};
	
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
	
	public float measureContentTxt() {
		StringBuffer sb = new StringBuffer(content == null ? "" : content);
		for(int i = 0; i < dot_count; i++)
			sb.append(".");
		return getPaint().measureText(sb.toString());
	}
	
	public void stopRun() {
		isStop = true;
		currentPosition = -1;
		mHandler.sendEmptyMessage(STOP_RUN_MESSAGE);
	}
	
	public void setDotCount(int count) {
		dot_count = count;
	}

	public DotsRunView2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DotsRunView2(Context context) {
		super(context, null);
	}
	
}
