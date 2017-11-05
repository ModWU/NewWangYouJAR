package cg.yunbee.cn.wangyoujar.pay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.pojo.DeviceInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.DYH5Activity;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import dy.compatibility.work.Contant;

public class OKPayPay extends SDKPay {
	
	private String url = "http://pay.yunbee.cn/pay/unifiedorder";
	private String getIpUrl = "http://pay.yunbee.cn/get/clientip";
	private String mch_id = "CDK1Z72L8CZO0E1";
	private String key = "asvbt0ll6x2m0cz";
	private String app_id = "201709011";
	public static final int WEIXIN = 0;
	public static final int ALIPAY = 1;
	private int mType = 0;
	
	public OKPayPay(int type) {
		mType = type;
	}
	
	private HashMap<String, String> getParams() {
		String pay_channel = "alipay";
		if(mType == WEIXIN) {
			pay_channel = "wechat";
		} else {
			pay_channel = "alipay";
		}
		String trade_type = "wap";
		String nonce_str = getRandomString(32);
		String detail = YunbeeVice.gameJSONInfo.gameName;
		String app_name = YunbeeVice.gameJSONInfo.gameName;
		String bundle = "com.okpay.sdk";
		String out_trade_no = mData;
		String total_fee = mPrice;
		String notify_url = Contant.CALL_HOST + "/Callback/unifiedorder.ashx";
		String ip = getIp();
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mch_id", mch_id);
		params.put("pay_channel", pay_channel);
		params.put("trade_type", trade_type);
		params.put("nonce_str", nonce_str);
		params.put("detail", detail);
		params.put("app_name", app_name);
		params.put("bundle", bundle);
		params.put("out_trade_no", out_trade_no);
		params.put("total_fee", total_fee);
		params.put("notify_url", notify_url);
		params.put("spbill_create_ip", ip);
		params.put("app_id", app_id);
		return params;
	}
	
	private String getIp() {
		String ip = "";
		String ipData = HttpUtils.sendGet(getIpUrl);
		try {
			JSONObject jsonObj = new JSONObject(ipData);
			ip = jsonObj.getString("ip");
		} catch (JSONException e) {
		}
		
		if(Util.isEmpty(ip)) ip = DeviceInfo.getInstance().getIp();
		return ip;
	}
	
	private String getSign(Map<String, String> paramMap) {
		List<String> keyList = new ArrayList<String>(paramMap.keySet());
		Collections.sort(keyList, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});
		
		StringBuffer paramsBuffer = new StringBuffer();
		for(String k : keyList) {
			String v = paramMap.get(k);
			paramsBuffer.append(k + "=" + v);
			paramsBuffer.append("&");
		}
		paramsBuffer.append("key=" + key);
		
		return Util.md5(paramsBuffer.toString()).toUpperCase();
	}
	
	
	private String getWebUrl(Map<String, String> params) {
		boolean isUtf8Decode = false;
		if(mType == WEIXIN) {
			isUtf8Decode = true;
		} else {
			isUtf8Decode = false;
		}
		String result = HttpUtils.sendPostUTF8(url, params, isUtf8Decode);
		
		try {
			JSONObject jsonObj = new JSONObject(result);
			String code = jsonObj.getString("code");
			if("success".equalsIgnoreCase(code)) {
				JSONObject dataObj = new JSONObject(jsonObj.getString("data"));
				String mweb_url = dataObj.getString("mweb_url");
				return mweb_url;
			}
			
		} catch (Exception e) {
			TAGS.log("OKPayPay-->getWebUrl: \n" + Log.getStackTraceString(e));
		}
		
		return null;
	}

	@Override
	public SDKPay pay() {
		
		TAGS.log("使用okpay支付");
		if(mType == WEIXIN) {
			TAGS.log("使用微信支付");
		} else {
			TAGS.log("使用支付宝支付");
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = getParams();
				params.put("sign", getSign(params));
				
				String web_url = getWebUrl(params);
				DialogUtils.closeDialog(mActivity, PayManager.dialog);
				if(!Util.isEmpty(web_url)) {
					startH5Page(web_url, mType == WEIXIN ? DYH5Activity.PAGE_OKPAY_WEIXIN : DYH5Activity.PAGE_OKPAY_APLIPAY, false);
				} else {
					payAbort(mData);
				}
				
			}
			
		}).start();
		
		return null;
	}
	
	
	

}
