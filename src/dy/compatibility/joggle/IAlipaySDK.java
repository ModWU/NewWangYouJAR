package dy.compatibility.joggle;

import android.app.Activity;

public interface IAlipaySDK {
	String alipay(Activity activity, String payInfo);
	void alipay_init(Activity activity);
}
