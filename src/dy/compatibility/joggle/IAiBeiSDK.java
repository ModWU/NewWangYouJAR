package dy.compatibility.joggle;

import android.app.Activity;
import dy.compatibility.callback.AiBeiCallback;

public interface IAiBeiSDK {
	void aibei_init(Activity activity, String orientation, String appId);
	void aibei_pay(Activity activity, String params, String publicKey, AiBeiCallback aibeiCallback);
	String getParams(String appId, String appuserid, int waresid, float price, String cporderid, String propName, String orderId, String privateKey);
}
