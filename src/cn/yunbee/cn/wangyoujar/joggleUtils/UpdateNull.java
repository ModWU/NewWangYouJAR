package cn.yunbee.cn.wangyoujar.joggleUtils;

import android.app.Activity;
import android.util.Log;
import cn.yunbee.cn.wangyoujar.joggle.ICheckUpdate;
import cn.yunbee.cn.wangyoujar.joggle.IUpdate;

public class UpdateNull implements IUpdate {
	
	private Activity mActivity;
	private final static String INFO = "检查更新异常!";
	
	public UpdateNull(Activity activity) {
		this.mActivity = activity;
		Log.i("yunbee_proceing", INFO);
	}

	@Override
	public void checkUpdate(ICheckUpdate listener) {
		Log.i("yunbee_proceing", INFO);
	}

	@Override
	public void tryAddSdkLibs() {
		Log.i("yunbee_proceing", INFO);
	}


}
