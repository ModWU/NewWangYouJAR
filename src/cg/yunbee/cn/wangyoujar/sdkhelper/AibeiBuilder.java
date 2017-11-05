package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class AibeiBuilder extends SPayBuilder {
	
	AibeiBuilder(Activity activity) {
		super(activity);
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
