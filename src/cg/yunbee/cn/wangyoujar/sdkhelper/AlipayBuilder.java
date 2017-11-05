package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class AlipayBuilder extends SPayBuilder {
	
	AlipayBuilder(Activity activity) {
		super(activity);
	}

	@Override
	protected String getIconName() {
		return "dian_pay_icon_paytype_alipay";
	}

	@Override
	public String getPayName() {
		return "支付宝支付";
	}

	@Override
	public String getPayDetail() {
		return "推荐支付宝用户";
	}


}
