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
		return "֧����֧��";
	}

	@Override
	public String getPayDetail() {
		return "�Ƽ�֧�����û�";
	}


}
