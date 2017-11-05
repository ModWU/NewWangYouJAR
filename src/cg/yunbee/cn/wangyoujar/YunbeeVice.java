package cg.yunbee.cn.wangyoujar;

import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cg.yunbee.cn.wangyou.loginSys.DyLoadingActivity;
import cg.yunbee.cn.wangyou.loginSys.LoginRecords;
import cg.yunbee.cn.wangyou.loginSys.LogoutProvider;
import cg.yunbee.cn.wangyou.loginSys.UserInfo;
import cg.yunbee.cn.wangyou.loginSys.UserToken;
import cg.yunbee.cn.wangyoujar.pojo.GameJSONInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.DianPayActivity;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import cn.yunbee.cn.wangyoujar.joggle.IInitListener;
import cn.yunbee.cn.wangyoujar.joggle.ILoginListener;
import cn.yunbee.cn.wangyoujar.joggle.ILogoutListener;
import cn.yunbee.cn.wangyoujar.joggle.IPayCallBack;
import cn.yunbee.cn.wangyoujar.joggle.IQueryListener;
import cn.yunbee.cn.wangyoujar.joggle.IYunbee;

public class YunbeeVice implements IYunbee {
	
	private Activity mActivity;
	
	public static boolean doPayFlag = false;
	
	public static PayManager payManager;
	
	private YunbeeTask yunbeeTask;
	
	private Toast toast;
	
	public static GameJSONInfo gameJSONInfo = new GameJSONInfo();
	
	public static String gameName = "";
	
	
	public Handler handler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(payManager == null) {
				return;
			}
			payManager.handleMessage(msg);
		}
	};
	
	
	
	YunbeeVice(Activity activity) {
		mActivity = activity;
	}

	
	private void clearData() {
		if(payManager != null) 
			payManager.clean();
		
		if(yunbeeTask != null)
			yunbeeTask.cancel(true);
		doPayFlag = false;
		payManager = null;
		yunbeeTask = null;
		toast = null;
		System.gc();
	}
	
	void exit() {
		Log.i("INFO", "退出罗!");
		clearData();
	}
	
	private void startInit(final Activity activity) {
		if(activity != null) mActivity = activity;
		if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
			yunbeeTask = new YunbeeTask(activity);
			yunbeeTask.execute();
		} else {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					yunbeeTask = new YunbeeTask(activity);
					yunbeeTask.execute();
				}
				
			});
		}
	}

	@SuppressLint("ShowToast")
	private void handleUI(Activity activity) {
		if(activity != null && mActivity != activity) {
			mActivity = activity;
			toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		}
		
		if(toast == null) {
			toast = Toast.makeText(mActivity, "", Toast.LENGTH_SHORT);
		}
	}

	public void oGpay(Activity activity, String propId, String propName, String propPrice, String cpprivate,
			IPayCallBack payCallBack) {
		if(!doPayFlag) {
			doPayFlag = true;
			payOnMainThread(activity, propId, propName, propPrice, cpprivate, payCallBack);
		}
	}
	
	private void pay(Activity activity, String propId, String propName, String propPrice, String cpprivate,
			IPayCallBack payCallBack) {
		handleUI(activity);
		
		if(propPrice != null) {
			int index = propPrice.indexOf('.');
			if(index >= 0)
				propPrice = propPrice.substring(0, index);
		}
		
		if(null == propPrice || "".equals(propPrice.trim()) || !propPrice.matches("[0-9]+"))
			propPrice = "0";
		
		if(null == propName) propName = "";
		if(null == propId) propId = "";
		
		
		if(yunbeeTask != null) {
			payManager = new PayManager(activity, this, propId, propName, propPrice, cpprivate, payCallBack);
			yunbeeTask.pay(activity, payManager);
		} else {
			doPayFlag = false;
			toast.setText("请先初始化...");
			toast.show();
		}
	}
	
	private void payOnMainThread(final Activity activity, final String propId, final String propName, final String propPrice, final String cpprivate,
			final IPayCallBack payCallBack) {
		if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
			pay(activity, propId, propName, propPrice, cpprivate, payCallBack);
		} else {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					pay(activity, propId, propName, propPrice, cpprivate, payCallBack);
				}
				
			});
		}
	}


	public void pc_query(Activity activity, IQueryListener listener) {
		if(payManager != null) {
			payManager.pc_query(activity, listener, new boolean[]{false});
		}
	}
	
	

	public void doLogin(Activity activity) {
		TAGS.log("-------------------YunbeeVice-->doLogin----------------------");
		loginOnMainThread(activity, false);
	}
	
	public void payLogin(Activity activity) {
		TAGS.log("-------------------YunbeeVice-->payLogin----------------------");
		loginOnMainThread(activity, true);
	}
	
	
	private boolean checkLoginState(Activity activity) {
		UserToken userToken = LoginRecords.getInstance(activity).getUserToken();
		if(userToken != null) {
			UserInfo userInfo = LoginRecords.getInstance(activity).getAllMapUsers().get(userToken.getLoginName());
			if(userInfo != null && !Util.isEmpty(userInfo.getLoginName()) &&  !Util.isEmpty(userInfo.getPassword())) {
				if(Yunbee.isLogin) return true;
				Yunbee.isLogin = true;
				Intent loginAcIt = new Intent(activity, DyLoadingActivity.class);
				loginAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_LOGIN);
				loginAcIt.putExtra(DyLoadingActivity.USERINFO, userInfo);
				activity.startActivity(loginAcIt);
				return true;
			}
		}
		return false;
	}
	
	private void loginOnMainThread(Activity activity, boolean isPay) {
		if(isLoginSuccess()) {
			TAGS.log("已经是登录状态，忽略本次登录!只回调登录成功接口");
			DyLoadingActivity.callLoginListener(activity, true);
			return;
		}
		final Activity finalAc = activity;
		final boolean finalIsPay = isPay;
		if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
			loginShouldOnMainThread(activity, isPay);
		} else {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					loginShouldOnMainThread(finalAc, finalIsPay);
				}
				
			});
		}
	}
	
	private void loginShouldOnMainThread(Activity activity, boolean isPay) {
		handleUI(activity);
		
		if(checkLoginState(activity)) return;
		
		if(yunbeeTask != null) {
			if(isPay)
				yunbeeTask.payLogin(activity);
			else
				yunbeeTask.login(activity);
		} else {
			toast.setText("请先初始化...");
			toast.show();
		}
	}
	
	private boolean isLoginSuccess() {
		synchronized (YunbeeVice.class) {
			return LoginRecords.getInstance(mActivity).getUserToken() != null;
		}
	}

	public boolean isAutoLogin() {
		List<UserInfo> allUser = LoginRecords.getInstance(mActivity).getAllListUsers();
		if(allUser != null && allUser.size() > 0) {
			Collections.sort(allUser);
			return allUser.get(0).isAutoLogin() && allUser.get(0).isHasPwd();
		}
		
		return false;
	}


	public void setAutoLogin(boolean isAutoLogin) {
		List<UserInfo> allUser = LoginRecords.getInstance(mActivity).getAllListUsers();
		if(allUser != null && allUser.size() > 0) {
			Collections.sort(allUser);
			if(!isAutoLogin) {
				allUser.get(0).setAutoLogin(false);
			} else  {
				allUser.get(0).setRememberPwd(true);
				allUser.get(0).setAutoLogin(true);
			}
			LoginRecords.getInstance(mActivity).updateInfo(allUser);
		}
	}


	public void doLogout(Activity activity, boolean isPopOutWindow) {
		LogoutProvider logoutProvider = new LogoutProvider(activity, isPopOutWindow);
		logoutProvider.logout();
	}


	@Override
	public void yunbeeInit(Activity activity) {
		startInit(activity);
	}


	@Override
	public void exit(Activity activity) {
		exit();
	}


	@Override
	public void setDebug(boolean isDebug) {
		TAGS.isDebug = isDebug;
	}


	@Override
	public void setGameName(String gameName) {
		YunbeeVice.gameName = gameName;
	}


	@Override
	public void pc_no_query() {
		if(DianPayActivity.dianPayActivity != null) {
			DianPayActivity.dianPayActivity.finish();
		}
	}


	@Override
	public String getVersionCode() {
		return Yunbee.LOCAL_VERSION;
	}


	@Override
	public void setLoginListener(ILoginListener listener) {
		DyLoadingActivity.setLoginListener(listener);
	}


	@Override
	public void setLogoutListener(ILogoutListener listener) {
		LogoutProvider.setLogoutListener(listener);
	}


	@Override
	public void setInitListener(IInitListener listener) {
		YunbeeTask.setIInitListener(listener);
	}

}
