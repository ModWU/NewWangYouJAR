package cg.yunbee.cn.wangyoujar.sdkpay;


import android.app.Activity;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.work.DianPayActivity;
import dy.compatibility.joggle.IWeixinSDK;
import dy.compatibility.work.SDKOptProducer;

public class Wechat_Pay {
	public static String appid;
	public static String partnerid;

	public static boolean pay(Activity activity) {
		
		/*while (Wechat_PrePay.prepay_id == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TAGS.log("正在等待预支付结果。。");
		}*/
		
		if(Wechat_PrePay.prepay_id == null) {
			synchronized (Wechat_PrePay.WAIT_LOCK) {
				if(Wechat_PrePay.prepay_id == null) {
					try {
						TAGS.log("正在等待预支付结果。。");
						Wechat_PrePay.WAIT_LOCK.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
		TAGS.log("预支付结果->prepay_id: " + Wechat_PrePay.prepay_id);
		
		if (Wechat_PrePay.prepay_id == null
				|| "error".equals(Wechat_PrePay.prepay_id)) {
			return false;
		}
		String appid = Wechat_Pay.appid;
		String noncestr = Wechat_PrePay.getRandomString(32);
		String packagePackage = "Sign=WXPay";
		String partnerid = Wechat_Pay.partnerid;
		String prepayid = Wechat_PrePay.prepay_id;
		String timestamp = System.currentTimeMillis() / 1000 + "";

		String stringA = "appid=" + appid + "&noncestr=" + noncestr
				+ "&package=" + packagePackage + "&partnerid=" + partnerid
				+ "&prepayid=" + prepayid + "&timestamp=" + timestamp;
		String stringB = stringA + "&key=" + Wechat_PrePay.key;
		String sign = Wechat_PrePay.md5(stringB).toUpperCase();

		TAGS.log("appid：" + appid);
		TAGS.log("noncestr：" + noncestr);
		TAGS.log("partnerid：" + partnerid);
		TAGS.log("prepayid：" + prepayid);
		TAGS.log("timestamp：" + timestamp);
		TAGS.log("stringA：" + stringA);
		TAGS.log("sign：" + sign);

		SDKOptProducer sdkOptProducer = SDKOptProducer.newInstance(activity);
		IWeixinSDK iweixinSdk = sdkOptProducer.getWeixinSDK();
		boolean isSuccess = iweixinSdk.sendReq(activity, appid, noncestr, partnerid, prepayid, timestamp, stringA, packagePackage, sign);
		
		if(!isSuccess) {
			DianPayActivity.isPayRunning = false;
			YunbeeVice.payManager.payAbort();
		}
		
		return isSuccess;
		
	}
	
	
}
