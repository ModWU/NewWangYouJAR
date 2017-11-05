package dy.compatibility.joggle;

import android.app.Activity;

public interface IWeixinSDK {
	boolean sendReq(Activity activity, String appid, String noncestr, String partnerid, String prepayid, String timestamp, String stringA, String packageValue, String sign);
}
