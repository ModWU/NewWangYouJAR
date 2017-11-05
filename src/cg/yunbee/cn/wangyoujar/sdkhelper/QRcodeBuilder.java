package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;

public class QRcodeBuilder extends SPayBuilder {

	public QRcodeBuilder(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getIconName() {
		return "dyqrscanlogo";
	}

	@Override
	public String getPayName() {
		return "二维码支付";
	}

	@Override
	public String getPayDetail() {
		return "使用摄像头进行扫描";
	}

}
