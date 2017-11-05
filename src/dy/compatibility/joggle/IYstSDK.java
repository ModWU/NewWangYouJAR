package dy.compatibility.joggle;

import android.app.Activity;
import dy.compatibility.callback.YstCallback;

public interface IYstSDK {
	void yst_pay(Activity activity, YstCallback ystCallback, String merchantNum, String merchantKeyOne, String merchantKeyTwo, String formId, String orgCode, String orderNum, String payAmount, String orderInfo, String waresInfo, String transDate, String backUrl, String notifyUrl, String reqReserved);
}
