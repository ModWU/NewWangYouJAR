package cg.yunbee.cn.wangyoujar.pay;

import java.text.SimpleDateFormat;
import java.util.Date;

import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.keycode.DecodeKey;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import dy.compatibility.callback.YstCallback;
import dy.compatibility.joggle.IYstSDK;
import dy.compatibility.work.Contant;
import dy.compatibility.work.SDKOptProducer;

public class YstPay extends SDKPay {

	@Override
	public YstPay pay() {
		TAGS.log("使用银视通进行支付");
		try {
			
			 /*merchantNum="939310058120002";
			    merchantone="19B701FDA6CF540CED2BED20BCBE385E";
			    merchanttwo="6F7096C2C344D4E11D6FD2F4247C4A34";
			    formId="000000000008643";
			    orgCode="61000018";
			    orderInfo="典游支付测试";
			    waresInfo="游戏道具";
			    backUrl="http://www.chinatvpay.com";
			    notifyUrl="https://pay.100bsh.com:2443/unionpay_notify/unionpay/collectionNotify";*/
			
			String merchantNum = "dcqt0ezyHugwtIctcrhkqQ==";//839290048990090
			String merchantKeyOne = "6Dq+MVsF6Qf/ZrH97JvkIpa8rE4rlnWpeVuMZWp6KiTB5vx9qCaU0g==";//48866BA896396780B8B60F85C0BB5CE0
			String merchantKeyTwo = "LCZQfEUAmydhizqFos9oyAzsdxKdkSnwUHEiXknxvCfB5vx9qCaU0g==";//17E26C3B121511FF16203E152538D49E
			String formId = "r8Kf6QJJpn/cJeM9kfDiVg==";//000000000008663
			String orgCode = "lpS2hr4IPQvB5vx9qCaU0g==";//62000001
			String orderNum = mData;
			String payAmount = mPrice;
			String orderInfo = "典游支付";
			String waresInfo = mPropName;
			String reqReserved = mPayId;
			
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");  
	        String transDate = format.format(new Date()); 
	        
	        String backUrl = null;//"http://pay.100bsh.com:2080/back.html";
	        String notifyUrl = Contant.CALL_HOST + "/Callback/yinshitong.ashx";
	        
	        
	        //解密
	        merchantNum = DecodeKey.decrypt(merchantNum, YunbeeVice.gameJSONInfo.secretKey);
	        merchantKeyOne = DecodeKey.decrypt(merchantKeyOne, YunbeeVice.gameJSONInfo.secretKey);
	        merchantKeyTwo = DecodeKey.decrypt(merchantKeyTwo, YunbeeVice.gameJSONInfo.secretKey);
	        formId = DecodeKey.decrypt(formId, YunbeeVice.gameJSONInfo.secretKey);
	        orgCode = DecodeKey.decrypt(orgCode, YunbeeVice.gameJSONInfo.secretKey);
			
			SDKOptProducer producer = SDKOptProducer.newInstance(mActivity);
			IYstSDK ystSDK = (IYstSDK) producer.getYstSDK();
			YstCallback callback = new YstCallback() {
				
				@Override
				public void onUserCancel() {
					payCancel(mData);
				}
				
				@Override
				public void onPaySuccess() {
					paySuccess(mData);
				}
				
				@Override
				public void onPayFail(int errorCode, String errorMsg) {
					TAGS.log("errorCode: " + errorCode + ", errorMsg: " + errorMsg);
					payAbort(mData);
				}
			};
			int n = Integer.parseInt(payAmount);
			payAmount = String.format("%012d",  n);
			TAGS.log("merchantNum:" + merchantNum);
			TAGS.log("merchantKeyOne:" + merchantKeyOne);
			TAGS.log("merchantKeyTwo:" + merchantKeyTwo);
			TAGS.log("formId:" + formId);
			TAGS.log("orgCode:" + orgCode);
			TAGS.log("orderNum:" + orderNum);
			TAGS.log("payAmount:" + payAmount);
			TAGS.log("orderInfo:" + orderInfo);
			TAGS.log("waresInfo:" + waresInfo);
			TAGS.log("transDate:" + transDate);
			TAGS.log("backUrl:" + backUrl);
			TAGS.log("notifyUrl:" + notifyUrl);
			TAGS.log("reqReserved:" + reqReserved);
			DialogUtils.closeDialog(mActivity, PayManager.dialog);
			ystSDK.yst_pay(mActivity, callback, merchantNum, merchantKeyOne, merchantKeyTwo, formId, orgCode, orderNum, payAmount, orderInfo, waresInfo, transDate, backUrl, notifyUrl, reqReserved);
			
		} catch (Exception e) {
			e.printStackTrace();
			payAbort();
			TAGS.log("使用银视通进行支付ex:" + e.toString());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			payAbort();
			TAGS.log("使用银视通进行支付ex2:" + e.toString());
		} catch (Error e) {
			e.printStackTrace();
			payAbort();
			TAGS.log("使用银视通进行支付ex3:" + e.toString());
		}
		
		return this;
	}

}
