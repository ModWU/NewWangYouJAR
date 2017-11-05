package cn.yunbee.cn.wangyoujar.joggle;

import android.app.Activity;
import android.content.Intent;

public interface IYunbee {
	void yunbeeInit(Activity activity);
	void oGpay(final Activity activity, String propId, String propName, String propPrice, String cpprivate, IPayCallBack payCallBack);
	void exit(Activity activity);
	void setDebug(boolean isDebug);
	void setGameName(String gameName);
	void pc_no_query();
	void pc_query(Activity activity, IQueryListener listener);
	String getVersionCode();
	void doLogin(Activity activity);
	void doLogout(Activity activity, boolean isPopOutWindow);
	void setLoginListener(ILoginListener listener);
	void setLogoutListener(ILogoutListener listener);
	void setInitListener(IInitListener listener);
	boolean isAutoLogin();
	void setAutoLogin(boolean isAutoLogin);
}
