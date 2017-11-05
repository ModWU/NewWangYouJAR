package cg.yunbee.cn.wangyou.loginSys;

import android.view.View;
import android.view.View.OnClickListener;

public class DyLoginListeners {
	
	public static final int FLAG_REGISTER = 1;
	public static final int FLAG_LOGIN = 2;
	public static final int FLAG_QUICK_LOGIN = 3;
	public static final int FLAG_PWD_STATUS = 4;
	public static final int FLAG_SHOW_MORE = 5;
	public static final int FLAG_ALTER_PASSWORD = 6;
	public static final int FLAG_FORGET_PASSWORD = 7;
	public static final int FLAG_REMEMBER_PASSWORD = 8;
	public static final int FLAG_AUTO_LOGIN = 9;
	
	private static DyLoginListeners dyLoginListeners; 
	
	private ILoginEvents loginEvent; 
	
	
	private DyLoginListeners(ILoginEvents loginEvent) {
		this.loginEvent = loginEvent;
	}
	
	public static DyLoginListeners newInstance(ILoginEvents loginEvent) {
		return new DyLoginListeners(loginEvent);
	}
	
	private final OnClickListener registerListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.register();
		}
		
	};
	
	
	private final OnClickListener loginListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.login();
		}
		
	};
	
	
	private final OnClickListener quickLoginListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.quickLogin();
		}
		
	};
	
	private final OnClickListener pwdStatusListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.pwdVisible();
		}
		
	};
	
	private final OnClickListener showMoreListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.showMore();
		}
		
	};
	
	private final OnClickListener alterPwdListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.alterPwd();
		}
		
	};
	
	private final OnClickListener forgetPwdListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.forgetPwd();
		}
		
	};
	
	private final OnClickListener rememberPwdListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.rememberPwd();
		}
		
	};
	
	private final OnClickListener autoLoginListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(loginEvent != null)
				loginEvent.autoLogin();
		}
		
	};
	
	public void addListener(View view, int flag) {
		switch(flag) {
		case FLAG_REGISTER:
			view.setOnClickListener(registerListener);
			break;
			
		case FLAG_LOGIN:
			view.setOnClickListener(loginListener);
			break;
			
		case FLAG_QUICK_LOGIN:
			view.setOnClickListener(quickLoginListener);
			break;
			
		case FLAG_PWD_STATUS:
			view.setOnClickListener(pwdStatusListener);
			break;
			
		case FLAG_SHOW_MORE:
			view.setOnClickListener(showMoreListener);
			break;
			
		case FLAG_ALTER_PASSWORD:
			view.setOnClickListener(alterPwdListener);
			break;
			
		case FLAG_FORGET_PASSWORD:
			view.setOnClickListener(forgetPwdListener);
			break;
		case FLAG_REMEMBER_PASSWORD:
			view.setOnClickListener(rememberPwdListener);
			break;
		case FLAG_AUTO_LOGIN:
			view.setOnClickListener(autoLoginListener);
			break;
			default:
		}
	}
	
	public void clear() {
		dyLoginListeners = null;
	}
}
