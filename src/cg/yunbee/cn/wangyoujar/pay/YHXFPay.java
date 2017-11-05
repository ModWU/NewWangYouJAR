package cg.yunbee.cn.wangyoujar.pay;

import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.feeInfo.InitFeeInfo;
import cg.yunbee.cn.wangyoujar.keycode.DecodeKey;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import dy.compatibility.callback.YHXFCallback;
import dy.compatibility.joggle.IYHXFSDK;
import dy.compatibility.work.Contant;
import dy.compatibility.work.SDKOptProducer;

public class YHXFPay extends SDKPay {
	
	public static final int WEIXIN = 0;
	public static final int ALIPAY = 1;
	public static final int QQ = 2;
	
	private int mType = 0;
	
	public YHXFPay(int type) {
		mType = type;
	}

	@Override
	public YHXFPay pay() {
		try {
			SDKOptProducer producer = SDKOptProducer.newInstance(mActivity);
			IYHXFSDK yhxfSDK = producer.getYHXFSDK();
			
			InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
			
			String orderid = mData;
			String spzdy = mPayId;
			String uid = initFeeInfo.getId();
			String spsuc = Contant.CALL_HOST + "/Callback/yinghuaxunfang.ashx";//"http://14.17.77.161/api/order/wx_pay_result";
			String productname = YunbeeVice.gameJSONInfo.gameName;
			
			String mz = "";
			float fenint = Float.valueOf(mPrice);
			float yuanFloat = fenint / 100.0f;
			mz = String.valueOf(yuanFloat);
			
			TAGS.log("orderid: " + orderid);
			TAGS.log("spzdy: " + spzdy);
			TAGS.log("uid: " + uid);
			TAGS.log("spsuc: " + spsuc);                                                                                                                                                                                  
			TAGS.log("productname: " + productname);
			TAGS.log("mz: " + mz);
			
			YHXFCallback callback = new YHXFCallback() {
				
				@Override
				public void onPayFinish(String s, String s1) {
					TAGS.log("s1: " + s1);
					/*//关闭浏览器
					ActivityManager manager = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
					manager.killBackgroundProcesses("com.android.browser");*/
					//此处还需要相应的查询
					paySuccess(s1);
				}
			};
			
			if(mType == WEIXIN) {
				TAGS.log("使用盈华讯方微信进行支付");
				String spid = "YvEBCe+tXYw=";//8082
				String sppwd = "z3ZC+v4qz/7+y0JYtr98mUlcL+tCndBs";//e3c0a9df3cec45ab90
				
				try {
					spid = DecodeKey.decrypt(spid, YunbeeVice.gameJSONInfo.secretKey);
					sppwd = DecodeKey.decrypt(sppwd, YunbeeVice.gameJSONInfo.secretKey);
				} catch (Exception e) {
					TAGS.log("--yhxfPay(盈华讯方微信数据)-->ex: " + e.toString());
				}
				
				TAGS.log("spid: " + spid);
				TAGS.log("sppwd: " + sppwd);
				
				yhxfSDK.initParams(spid, sppwd, orderid, mz, spzdy, uid, spsuc, productname, 2);
				yhxfSDK.payByWeixin(mActivity, callback);
			} else if(mType == ALIPAY) {
				TAGS.log("使用盈华讯方支付宝进行支付");
				String spid = "UoskhReXpoc=";//8083
				String sppwd = "5L/J7MJeDl3bvcZt6lRqJgHgrr834nnB";//a087f38fe6314de2a1
				try {
					spid = DecodeKey.decrypt(spid, YunbeeVice.gameJSONInfo.secretKey);
					sppwd = DecodeKey.decrypt(sppwd, YunbeeVice.gameJSONInfo.secretKey);
				} catch (Exception e) {
					TAGS.log("--yhxfPay(盈华讯方支付宝数据)-->ex: " + e.toString());
				}
				
				TAGS.log("spid: " + spid);
				TAGS.log("sppwd: " + sppwd);
				
				yhxfSDK.initParams(spid, sppwd, orderid, mz, spzdy, uid, spsuc, productname, 1);
				yhxfSDK.payByAlipay(mActivity, callback);
			} else {
				TAGS.log("使用盈华讯方qq钱包进行支付");
				String spid = "";//"UoskhReXpoc=";//8083
				String sppwd = "";//"5L/J7MJeDl3bvcZt6lRqJgHgrr834nnB";//a087f38fe6314de2a1
				try {
					spid = DecodeKey.decrypt(spid, YunbeeVice.gameJSONInfo.secretKey);
					sppwd = DecodeKey.decrypt(sppwd, YunbeeVice.gameJSONInfo.secretKey);
				} catch (Exception e) {
					TAGS.log("--yhxfPay(盈华讯方支付宝数据)-->ex: " + e.toString());
				}
				
				TAGS.log("spid: " + spid);
				TAGS.log("sppwd: " + sppwd);
				
				yhxfSDK.initParams(spid, sppwd, orderid, mz, spzdy, uid, spsuc, productname, 4);
				yhxfSDK.payByQQ(mActivity, callback);
			}
			
			DialogUtils.closeDialog(mActivity, PayManager.dialog);
			
		} catch (Exception e) {
			TAGS.log("ex1: " + e.toString());
			payAbort();
		} catch (NoClassDefFoundError e) {
			TAGS.log("ex2: " + e.toString());
			payAbort();
		} catch (Error e) {
			TAGS.log("ex3: " + e.toString());
			payAbort();
		}
		
		return this;
	}

}
