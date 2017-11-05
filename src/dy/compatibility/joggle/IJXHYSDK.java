package dy.compatibility.joggle;

import android.app.Activity;
import dy.compatibility.callback.JXHYCallback;

public interface IJXHYSDK {
	void jxhy_pay(Activity activity, String app_id, String body, String device_info, String notify_url, String para_id, String order_no, String attach, String fee, String key, String child_para_id, JXHYCallback callback);
}
