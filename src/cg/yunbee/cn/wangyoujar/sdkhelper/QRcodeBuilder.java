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
		return "��ά��֧��";
	}

	@Override
	public String getPayDetail() {
		return "ʹ������ͷ����ɨ��";
	}

}
