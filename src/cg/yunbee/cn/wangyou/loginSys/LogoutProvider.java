package cg.yunbee.cn.wangyou.loginSys;

import android.app.Activity;
import android.content.Intent;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cn.yunbee.cn.wangyoujar.joggle.ILogoutListener;

public class LogoutProvider {
	
	private boolean isPopWindow;
	private static ILogoutListener logoutListener;
	private Activity activity;
	public static boolean isDoThing = false;
	
	public LogoutProvider(Activity activity, boolean isPopWindow) {
		this.activity = activity;
		this.isPopWindow = isPopWindow;
	}
	
	public static void setLogoutListener(ILogoutListener logoutListener) {
		LogoutProvider.logoutListener = logoutListener;
	}
	
	public void logout() {
		UserToken userToken = LoginRecords.getInstance(activity).getUserToken();
		if(userToken == null) {
			String loginName = "";
			/*List<UserInfo> userInfoList = LoginRecords.getInstance(activity).getAllListUsers();
			Collections.sort(userInfoList);
			if(userInfoList != null && userInfoList.size() > 0)
				loginName = userInfoList.get(0).getLoginName();*/
			LogoutProvider.logoutFail(loginName, UserAuthorize.CODE_LOGOUT_NOTVALID, Utils.getResourceStr(activity, "dy_logout_notvalid"));
			return;
		}
		
		if(isDoThing) {
			return;
		}
		
		isDoThing = true;
		if(activity != null) 
			Util.replaceClassLoader_api17(LogoutProvider.class.getClassLoader(), activity);
		if(isPopWindow) {
			popLogoutWindow();
		} else {
			doLogout(activity);
		}
	}
	
	private void popLogoutWindow() {
		if(activity != null) 
			activity.startActivity(new Intent(activity, DyLogoutActivity.class));
	}
	
	public static void doLogout(Activity activity) {
		if(activity != null) {
			Intent it = new Intent(activity, DyLoadingActivity.class);
			it.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_LOGOUT);
			activity.startActivity(it);
		}
	}
	
	
	public static void logoutSuccess(String dianId, String loginName) {
		if(logoutListener != null) {
			logoutListener.logoutSuccess(dianId, loginName);
		}
	}
	
	public static void logoutFail(String loginName, int errorCode, String errorInfo) {
		if(logoutListener != null) {
			logoutListener.logoutFail(loginName, errorCode, errorInfo);
		}
	}

}
