package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class OKPay_alipayBuilder extends SPayBuilder {

	public OKPay_alipayBuilder(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
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
