package cn.yunbee.cn.wangyoujar.joggleUtils;

import android.app.Activity;
import android.widget.Toast;
import cn.yunbee.cn.wangyoujar.joggle.IInitListener;
import cn.yunbee.cn.wangyoujar.joggle.ILoginListener;
import cn.yunbee.cn.wangyoujar.joggle.ILogoutListener;
import cn.yunbee.cn.wangyoujar.joggle.IPayCallBack;
import cn.yunbee.cn.wangyoujar.joggle.IQueryListener;
import cn.yunbee.cn.wangyoujar.joggle.IYunbee;

public class YunbeeNull implements IYunbee {
	
	private Activity activity;
	private Toast toast;
	private final static String INFO = "ÄÚ´æ²»×ã!";
	
	public YunbeeNull(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void yunbeeInit(Activity activity) {
		this.activity = activity;
		toast = Toast.makeText(activity, INFO, Toast.LENGTH_LONG);
	}


	@Override
	public void exit(Activity activity) {
	}

	@Override
	public void setDebug(boolean isDebug) {
	}

	@Override
	public void setGameName(String gameName) {
	}

	@Override
	public void pc_no_query() {
	}

	@Override
	public void pc_query(Activity activity, IQueryListener listener) {
	}

	@Override
	public String getVersionCode() {
		return Integer.MAX_VALUE + "";
	}


	@Override
	public void oGpay(Activity activity, String propId, String propName, String propPrice, String cpprivate,
			IPayCallBack payCallBack) {
		toast = Toast.makeText(activity, INFO, Toast.LENGTH_LONG);
		toast.show();
	}

	@Override
	public void doLogin(Activity activity) {
	}

	@Override
	public boolean isAutoLogin() {
		return false;
	}

	@Override
	public void setAutoLogin(boolean isAutoLogin) {
	}

	@Override
	public void doLogout(Activity activity, boolean isPopOutWindow) {
	}

	

	@Override
	public void setLogoutListener(ILogoutListener listener) {
	}

	@Override
	public void setLoginListener(ILoginListener listener) {
	}

	@Override
	public void setInitListener(IInitListener listener) {
	}

}
