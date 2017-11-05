package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class WeixinBuilder extends SPayBuilder {

	public WeixinBuilder(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getIconName() {
		return "dian_pay_icon_paytype_weixin";
	}

	@Override
	public String getPayName() {
		return "微信支付";
	}

	@Override
	public String getPayDetail() {
		return "推荐微信用户";
	}

}
