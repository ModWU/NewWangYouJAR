package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class YSTBuilder extends SPayBuilder {

	public YSTBuilder(Activity activity) {
		super(activity);
	}

	@Override
	protected String getIconName() {
		return "dian_pay_icon_paytype_aibei";
	}

	@Override
	public String getPayName() {
		return "��֧��";
	}

	@Override
	public String getPayDetail() {
		return null;
	}

}
