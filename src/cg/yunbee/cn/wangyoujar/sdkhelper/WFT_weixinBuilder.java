package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class WFT_weixinBuilder extends SPayBuilder {

	public WFT_weixinBuilder(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getIconName() {
		return "dian_pay_icon_paytype_weixin";
	}

	@Override
	public String getPayName() {
		return "΢��֧��";
	}

	@Override
	public String getPayDetail() {
		return "�Ƽ�΢���û�";
	}

}
