package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class JXHYBuilder extends SPayBuilder {

	public JXHYBuilder(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getIconName() {
		return "dian_pay_icon_paytype_aibei";
	}

	@Override
	public String getPayName() {
		return "ÔÆÖ§¸¶";
	}

	@Override
	public String getPayDetail() {
		return null;
	}

}
