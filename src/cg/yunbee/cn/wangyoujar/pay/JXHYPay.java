package cg.yunbee.cn.wangyoujar.pay;

public class JXHYPay extends SDKPay {
	
	@Override
	public JXHYPay pay() {
		return this;
		/*try {
			//新的
			SDKOptProducer producer = SDKOptProducer.newInstance(mActivity);
			IJXHYSDK jxhySdk = producer.getJxhySDK();
			JXHYCallback callback = new JXHYCallback() {
				
				@Override
				public void payFail() {
					if(mPayCallBack != null) {
						mPayCallBack.onFailed("支付失败");
					}
					payAbort();
				}

				@Override
				public void paySuccess() {
					try {
						if(mPayCallBack != null) {
							mPayCallBack.onSuccess("支付成功!");
						}
						paySuccess();
					} catch (Exception e) {
						mPayCallBack.onFailed("支付失败: " + e.toString());
						payAbort();
					}
				}
			};
			
			TelephonyManager tm = (TelephonyManager)mActivity.getSystemService(Context.TELEPHONY_SERVICE);
			
			//设置额外的信息
			InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
			JXHYConfig jxhyConfig = (JXHYConfig) initFeeInfo.getListSdk().get(TunnelTypeId.JXHY_TUNNEL_TYPE_ID);
			String appId = jxhyConfig.getAppId();
			String body = "计费";
			String device_info = tm.getDeviceId();
			String nofity_url = Contant.CALL_HOST + "/Callback/lefutong.ashx";
			String order_no = mData;
			String para_id = "10361";
			String fee = mPrice;
			String attach = mPayId;
			String key = "HbWafCMSKZG9VBCZKN36BTW9UUBR6WdO";
			String child_para_id = "";
			
			
			TAGS.log("appId:" + appId);
			TAGS.log("body:" + body);
			TAGS.log("device_info:" + device_info);
			TAGS.log("nofity_url:" + nofity_url);
			TAGS.log("para_id:" + para_id);
			TAGS.log("order_no:" + order_no);
			TAGS.log("attach:" + attach);
			TAGS.log("fee:" + fee);
			TAGS.log("key:**************");
			TAGS.log("child_para_id:" + child_para_id);
			
			DialogUtils.closeDialog(mActivity. PayManager.dialog);
			
			jxhySdk.jxhy_pay(mActivity, appId, body, device_info, nofity_url, para_id, order_no, attach, fee, key, child_para_id, callback);
			
		} catch (Exception e) {
			e.printStackTrace();
			TAGS.log("jxhyPay-->ex:" + e.toString());
			payAbort();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			TAGS.log("jxhyPay-->NoClassDefFoundError:" + e.toString());
			payAbort();
		} catch (Error e) {
			e.printStackTrace();
			TAGS.log("jxhyPay-->Error:" + e.toString());
			payAbort();
		}*/
	}
	
	
}
