package cg.yunbee.cn.wangyoujar.pay;

import android.widget.Toast;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkConfig.AIBEIConfig;
import cg.yunbee.cn.wangyoujar.sdkConfig.SecretData;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import dy.compatibility.callback.AiBeiCallback;
import dy.compatibility.joggle.IAiBeiSDK;
import dy.compatibility.work.SDKOptProducer;

public class AibeiPay extends SDKPay {

	@Override
	public AibeiPay pay() {	
		TAGS.log("Ê¹ÓÃ°®±´Ö§¸¶");
		if(Util.isEmpty(PayManager.mPayItem.getAibei_id())) {
			TAGS.log("°®±´id²»´æÔÚ£¬ÇëÌí¼Ó°®±´id");
			Util.showToastAtMainThread(Toast.makeText(mActivity, "", Toast.LENGTH_SHORT), "°®±´id²»´æÔÚ");
			payAbort();
			return this;
		}
		int aibeiPropId = Integer.valueOf(PayManager.mPayItem.getAibei_id());
		String cpOrderId = mData;
		String appUserId = PayManager.getUID(mActivity);
		if (appUserId == null || "".equals(appUserId)) {
			appUserId = String.valueOf(System.currentTimeMillis() / 1000);
		}
		String price = mPrice;
		String propertyName = mPropName;
		float fenint = Float.valueOf(price);
		float yuanFloat = fenint / 100.0f;
		String propName = propertyName;
		String params = getTransdata(appUserId, aibeiPropId, mPayId, yuanFloat,
				cpOrderId, propName);
		TAGS.log("params:" + params);
		try {
			SDKOptProducer producer = SDKOptProducer.newInstance(mActivity);
			
			IAiBeiSDK aibeiSdk = producer.getAiBeiSDK();
			
			DialogUtils.closeDialog(mActivity, PayManager.dialog);
			
			SecretData secretData = SecretData.getInstance();
			AIBEIConfig aibeiConfig = (AIBEIConfig) secretData.getSDKConfig(SecretData.TYPE_AIBEI);
			TAGS.log("°®±´¹«Ô¿(±ØÐë): " + aibeiConfig.getPublicKey());
			aibeiSdk.aibei_pay(mActivity, params, aibeiConfig.getPublicKey(), new AiBeiCallback() {
				
				@Override
				public void onPayResult(int resultCode, String arg1, String arg2) {
					
					TAGS.log("resultCode: " + resultCode);
					TAGS.log("message: " + arg2);
					try {
						
						if (resultCode == 0) {
							paySuccess(mData);
						} else {
							if (resultCode == 2) {
								payCancel(mData);
							} else {
								payAbort(mData);
							}
						}
					
					} catch(Exception e) {
						payAbort(mData);
					}
				}
			});
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
	
	
	private String getTransdata(String appuserid, int waresid,
			String param_id, float price, String cporderid, String propName) {
		
		SecretData secretData = SecretData.getInstance();
		AIBEIConfig aibeiConfig = (AIBEIConfig) secretData.getSDKConfig(SecretData.TYPE_AIBEI);
		String appId = aibeiConfig.getAppId();
		String privateKey = aibeiConfig.getPrivateKey();
		
		TAGS.log("appId:" + appId);
		TAGS.log("privateKey:" + privateKey);
		TAGS.log("appuserid:" + appuserid);
		TAGS.log("waresid:" + waresid);
		TAGS.log("price:" + price);
		TAGS.log("cporderid:" + cporderid);
		TAGS.log("propName:" + propName);
		TAGS.log("param_id:" + param_id);
		
		SDKOptProducer producer = SDKOptProducer.newInstance(mActivity);
		
		IAiBeiSDK aibeiSdk = producer.getAiBeiSDK();
		
		//String getParams(String appId, String appuserid, int waresid, float price, String cporderid, String propName, String orderId, String privateKey);
		
		return aibeiSdk.getParams(appId, appuserid, waresid, price, cporderid, propName, param_id, privateKey);
	}

}
