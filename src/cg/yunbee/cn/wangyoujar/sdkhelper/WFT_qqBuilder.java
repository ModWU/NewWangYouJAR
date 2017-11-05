package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class WFT_qqBuilder extends SPayBuilder {

	public WFT_qqBuilder(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getIconName() {
		return "dian_pay_icon_paytype_alipay";
	}

	@Override
	public String getPayName() {
		return "QQ钱包支付";
	}

	@Override
	public String getPayDetail() {
		return "推荐QQ钱包用户";
	}

}
