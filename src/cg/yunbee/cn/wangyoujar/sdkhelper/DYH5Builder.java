package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class DYH5Builder extends SPayBuilder {
	
	DYH5Builder(Activity activity) {
		super(activity);
	}


	@Override
	protected String getIconName() {
		return "dian_pay_icon_paytype_aibei";
	}


	@Override
	public String getPayName() {
		return "µ‰÷ß∏∂";
	}


	@Override
	public String getPayDetail() {
		return null;
	}

}
