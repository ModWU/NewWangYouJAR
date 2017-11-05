package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class NULLBuilder extends SPayBuilder {

	public NULLBuilder(Activity activity) {
		super(activity);
	}

	@Override
	protected String getIconName() {
		return "";
	}

	@Override
	public String getPayName() {
		return "";
	}

	@Override
	public String getPayDetail() {
		return "";
	}

}
