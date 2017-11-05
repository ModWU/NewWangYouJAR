package dy.compatibility.joggle;

import android.app.Activity;
import dy.compatibility.callback.YHXFCallback;

public interface IYHXFSDK {
	void yhxf_init(Activity activity);
	void initParams(String spid, String sppwd, String orderid, String mz, String spzdy, String uid, String spsuc, String productname, int orderType);
	void payByWeixin(Activity activity, YHXFCallback callback);
	void payByAlipay(Activity activity, YHXFCallback callback);
	void payByQQ(Activity activity, YHXFCallback callback);
}
