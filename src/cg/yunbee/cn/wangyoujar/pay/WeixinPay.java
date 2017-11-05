package cg.yunbee.cn.wangyoujar.pay;

import android.content.Context;
import android.content.pm.PackageManager;
import cg.yunbee.cn.wangyoujar.pojo.DeviceInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkConfig.SecretData;
import cg.yunbee.cn.wangyoujar.sdkConfig.WEIXINConfig;
import cg.yunbee.cn.wangyoujar.sdkpay.Wechat_Pay;
import cg.yunbee.cn.wangyoujar.sdkpay.Wechat_PrePay;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.work.PayManager;

public class WeixinPay extends SDKPay {

	@Override
	public WeixinPay pay() {
		TAGS.log("使用微信进行支付");
		try {
			
			SecretData secretData = SecretData.getInstance();
			WEIXINConfig weixinConfig = (WEIXINConfig) secretData.getSDKConfig(SecretData.TYPE_WEIXIN);
			Wechat_Pay.appid = weixinConfig.getAppId();
			Wechat_Pay.partnerid = weixinConfig.getPartnerId();
			
			TAGS.log("appid：" + Wechat_Pay.appid);
			TAGS.log("partnerid：" + Wechat_Pay.partnerid);

			String price = mPrice;
			String propertyName = mPropName;
			String productName = getParam(mActivity, "productName");
			String ip = getParam(mActivity, "ip");
			Wechat_PrePay.prePay(mActivity, propertyName,
					price, ip, mData, mPayId);
			TAGS.log("productName：" + productName);
			TAGS.log("ip：" + ip);
			new Thread(new Runnable() {

				@Override
				public void run() {
					boolean res = Wechat_Pay.pay(mActivity);
					if(!res) {
						DialogUtils.closeDialog(mActivity, PayManager.dialog);
					}
					TAGS.log(".....................微信最终结果：" + res);
					//DialogUtils.closeDialog(PayManager.dialog);
					/*try {
						if(res) {
							paySuccess(mData);
						} else {
							payAbort(mData);
						}
					} catch(Exception e) {
						payAbort(mData);
					}*/
				}

			}).start();
		} catch (Exception e) {
			e.printStackTrace();
			payAbort();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			payAbort();
		} catch (Error e) {
			e.printStackTrace();
			payAbort();
		}
		
		return this;
	}
	
	
	public String getParam(Context context, String paramName) {
		if ("productName".equals(paramName)) {
			PackageManager pm = context.getPackageManager();
			String appName = context.getApplicationInfo().loadLabel(pm)
					.toString();
			return appName;
		} else if ("ip".equals(paramName)) {
			return DeviceInfo.getInstance().getIp();
		} else {
			return "";
		}
	}

}
