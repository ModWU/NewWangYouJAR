package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class JJSDWap_weixinBuilder extends SPayBuilder {

	public JJSDWap_weixinBuilder(Activity activity) {
		super(activity);
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
