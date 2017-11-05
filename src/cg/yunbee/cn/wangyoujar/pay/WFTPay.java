package cg.yunbee.cn.wangyoujar.pay;

import java.util.Map;

import android.content.Intent;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.keycode.DecodeKey;
import cg.yunbee.cn.wangyoujar.pojo.DeviceInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import cg.yunbee.cn.wangyoujar.work.DYH5Activity;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import dy.compatibility.callback.WFTCallback;
import dy.compatibility.joggle.IWFTSDK;
import dy.compatibility.work.Contant;
import dy.compatibility.work.SDKOptProducer;

public class WFTPay extends SDKPay {
	
	public static final int WEIXIN = 0;
	public static final int ALIPAY = 1;
	public static final int QQ = 2;
	public static final int WAP_WEIXIN = 3;
	
	private int mType = 0;
	
	public WFTPay(int type) {
		mType = type;
	}

	@Override
	public WFTPay pay() {
		TAGS.log("使用威富通进行支付");
		if(mType == WAP_WEIXIN) {
			weixinPayByWap();
			return this;
		}
		
		SDKOptProducer producer = SDKOptProducer.newInstance(mActivity);
		final IWFTSDK wftSDK = producer.getWFTSDK();		
		final int finalFlag = mType;
		final String url = "https://pay.swiftpass.cn/pay/gateway";
		final String body = mPropName;
		final String service = "unified.trade.pay";
		final String version = "1.0";
		
		final String notify_url = Contant.CALL_HOST + "/Callback/weifutong1.ashx";
		final String nonce_str = null;
		final String out_trade_no = mData;
		final String mch_create_ip = DeviceInfo.getInstance().getIp();
		final String total_fee = mPrice;
		final String limit_credit_pay = "0";
		final String weixin_sign[] = {"59UUAiZu5kIkSr1OPpxWOndFbWEnLNKTI3kw/edZAqfB5vx9qCaU0g=="};//11f4aca52cf400263fdd8faf7a69e007
		final String weixin_mch_id[] = {"Kzy883OH/ZB4JAbZj4kfig=="};//7552900037
		final String alipay_sign[] = {"7c7K+fo6wUaIa4uMa+JJctSJCLbpQjArfjGsU0HDiLLB5vx9qCaU0g=="};//58bb7db599afc86ea7f7b262c32ff42f
		final String alipay_mch_id[] = {"1FI+Zbuhh/Ab1suILQ2LJg=="};//101520000465
		final String qq_sign[] = {"7GELMkBWBM5jTIIFZhSnn9e1ZgKwJpa5web8fagmlNI="};//385abd5f2a3a101c125bae7b
		final String qq_mch_id[] = {"5ZFNYbbfr7/a1yfhIyjHYQ=="};//755110002853
		
		try {
			weixin_sign[0] = DecodeKey.decrypt(weixin_sign[0], YunbeeVice.gameJSONInfo.secretKey);
			weixin_mch_id[0] = DecodeKey.decrypt(weixin_mch_id[0], YunbeeVice.gameJSONInfo.secretKey);
			
			alipay_sign[0] = DecodeKey.decrypt(alipay_sign[0], YunbeeVice.gameJSONInfo.secretKey);
			alipay_mch_id[0] = DecodeKey.decrypt(alipay_mch_id[0], YunbeeVice.gameJSONInfo.secretKey);
			
			qq_sign[0] = DecodeKey.decrypt(qq_sign[0], YunbeeVice.gameJSONInfo.secretKey);
			qq_mch_id[0] = DecodeKey.decrypt(qq_mch_id[0], YunbeeVice.gameJSONInfo.secretKey);
		} catch (Exception e) {
			TAGS.log("--getOrderInfo(支付宝数据)-->ex: " + e.toString());
		}
		
		
		final WFTCallback callback = new WFTCallback() {
			
			@Override
			public void paySuccess() {
				WFTPay.this.paySuccess(mData);
			}
			
			@Override
			public void payFail() {
				WFTPay.this.payAbort(mData);
			}
		};
		
		/**
         * 微信:7552900037
         * 支付宝:101520000465
         * qq钱包:755110002853
         */
		
		/**
         * 微信:11f4aca52cf400263fdd8faf7a69e007
         * 支付宝:58bb7db599afc86ea7f7b262c32ff42f
         * qq钱包:385abd5f2a3a101c125bae7b
         */
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> dataMap = null;
				
				if(finalFlag == 0) {
					//微信
					dataMap = wftSDK.getXMLData(url, body, service, version, weixin_mch_id[0], notify_url, nonce_str, out_trade_no, mch_create_ip, total_fee, limit_credit_pay, weixin_sign[0]);
				} else if(finalFlag == 1) {
					//支付宝
					dataMap = wftSDK.getXMLData(url, body, service, version, alipay_mch_id[0], notify_url, nonce_str, out_trade_no, mch_create_ip, total_fee, limit_credit_pay, alipay_sign[0]);
				} else {
					//qq钱包
					dataMap = wftSDK.getXMLData(url, body, service, version, qq_mch_id[0], notify_url, nonce_str, out_trade_no, mch_create_ip, total_fee, limit_credit_pay, qq_sign[0]);
				}
				
				DialogUtils.closeDialog(mActivity, PayManager.dialog);
				
				if(finalFlag == WEIXIN) {
					//微信
					wftSDK.weixinPay(mActivity, dataMap, callback);
				} else if(finalFlag == ALIPAY) {
					//支付宝
					wftSDK.alipayPay(mActivity, dataMap, callback);
				} else if(finalFlag == QQ) {
					//qq钱包
					wftSDK.qqWalletPay(mActivity, dataMap, callback);
				}
			}
			
		}).start();
		
		return this;
	}
	
	private final static String URL = "https://pay.swiftpass.cn/pay/gateway";
	private final static String CALLBACK_URL = "http://onlinegame.quhuogo.com/JumpPage.aspx";
	private final static String KEY = "11f4aca52cf400263fdd8faf7a69e007";
	private final static String MCH_ID = "7552900037";
	
	private void weixinPayByWap() {
		TAGS.log("使用WAP微信支付:2017.8.16");
		new Thread(new Runnable() {

			@Override
			public void run() {
				DeviceInfo deviceInfo = DeviceInfo.getInstance();
				
				String body = mPropName;
				String attach = mParam;
				String callback_url = CALLBACK_URL;
				String ip = deviceInfo.getIp();
				String notify_url = Contant.CALL_HOST + "/Callback/WeiFuTongH5WeiXin.ashx";
				String out_trade_no = mData;
				String total_fee = mPrice;
				
				String params = buildWeixinParamsByWap(body, callback_url, attach, ip, notify_url, out_trade_no, total_fee);
				
				String result = HttpUtils.sendPostDataUTF8(URL, params);
				
				String status = getValueFromXML(result, "status");
				
				DialogUtils.closeDialog(mActivity, PayManager.dialog); 
				
				if("0".equals(status)) {
					String pay_info = getValueFromXML(result, "pay_info");
					
					if(pay_info != null) {
						startH5Page(pay_info, DYH5Activity.PAGE_WFT_WEIXIN, false);
					} else {
						payAbort("pay_info未获取到");
					}
				} else {
					String message = getValueFromXML(result, "message");
					payAbort(message);
				}
				
				
				
			}
			
		}).start();
	}
	
	
	private String buildWeixinParamsByWap(String pbody, String pcallback_url, String pattach, String ip, String pnotify_url, String pout_trade_no, String ptotal_fee) {
		String attach = pattach;
		String body = pbody;
		String callback_url = pcallback_url;
		String charset = "UTF-8";
		String device_info = "AND_SDK";
		String mch_app_id = "com.tencent.tmgp.sgame";
		String mch_app_name = YunbeeVice.gameJSONInfo.gameName;
		String mch_create_ip = ip;
		String mch_id = MCH_ID;
		String nonce_str = getRandomString(32);
		String notify_url = pnotify_url;
		String out_trade_no = pout_trade_no;
		String service = "pay.weixin.wappay";
		String sign_type = "MD5";
		String total_fee = ptotal_fee;
		String version = "2.0";
		
		String stringA = "attach=" + attach + "&body=" + body + "&callback_url="+ callback_url
				 + "&charset="+ charset + "&device_info=" + device_info
				+ "&mch_app_id=" + mch_app_id + "&mch_app_name=" + mch_app_name + "&mch_create_ip=" + mch_create_ip
				 + "&mch_id=" + mch_id + "&nonce_str=" + nonce_str + "&notify_url=" + notify_url
				+ "&out_trade_no=" + out_trade_no + "&service=" + service + "&sign_type=" + sign_type
				+ "&total_fee=" + total_fee + "&version=" + version;
		String stringB = stringA + "&key=" + KEY;
		String sign = md5WeChat(stringB).toUpperCase();
		
		
		TAGS.log("stringB: " + stringB);
		TAGS.log("sign: " + sign);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<xml>");
		sb.append("    <attach>" + attach + "</attach>");
		sb.append("    <body>" + body + "</body>");
		sb.append("    <callback_url>" + callback_url + "</callback_url>");
		sb.append("    <charset>" + charset + "</charset>");
		sb.append("    <device_info>" + device_info + "</device_info>");
		sb.append("    <mch_app_id>" + mch_app_id + "</mch_app_id>");
		sb.append("    <mch_app_name>" + mch_app_name + "</mch_app_name>");
		sb.append("    <mch_create_ip>" + mch_create_ip
				+ "</mch_create_ip>");
		sb.append("    <mch_id>" + mch_id + "</mch_id>");
		sb.append("    <nonce_str>" + nonce_str + "</nonce_str>");
		sb.append("    <notify_url>" + notify_url + "</notify_url>");
		
		sb.append("    <out_trade_no>" + out_trade_no + "</out_trade_no>");
		sb.append("    <service>" + service
				+ "</service>");
		sb.append("    <sign_type>" + sign_type + "</sign_type>");
		
		sb.append("    <total_fee>" + total_fee + "</total_fee>");
		sb.append("    <version>" + version + "</version>");
		sb.append("    <sign>" + sign + "</sign>");
		sb.append("</xml>");
		TAGS.log("prepay 参数:" + sb);
		final String entity = sb.toString();
		return entity;
			
	}
	
	
	

}
