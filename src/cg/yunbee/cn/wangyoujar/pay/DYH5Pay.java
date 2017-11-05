package cg.yunbee.cn.wangyoujar.pay;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import cg.yunbee.cn.wangyou.loginSys.LoginRecords;
import cg.yunbee.cn.wangyou.loginSys.UserToken;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.keycode.DecodeKey;
import cg.yunbee.cn.wangyoujar.pojo.DeviceInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkConfig.SecretData;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.DYH5Activity;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import dy.compatibility.work.Contant;

public class DYH5Pay extends SDKPay {
	
	//private static final String URL_H5 = "http://api.onlinegame.quhuogo.com/Cashier/GetCashierParam.ashx";
	private static final String URL_H5 = "http://192.168.1.19:8902/Cashier/GetCashierParam.ashx";
	private static final String URL_WEIXIN = "http://api.onlinegame.quhuogo.com/Cashier/CashierPay.ashx";
	private static final String URL_ALIPAY = "http://api.onlinegame.quhuogo.com/Cashier/AlipayPay.ashx";
	
	
	public static final int H5_ALIPAY = 0;
	public static final int H5_WEIXIN = 1;
	public static final int H5_ALL = 2;
	
	private int mType = 0;
	
	public DYH5Pay(int type) {
		mType = type;
	}
	@Override
	public SDKPay pay() {
		TAGS.log("使用好付H5支付");
		new Thread(new Runnable() {

			@Override
			public void run() {
				if(mType == H5_ALIPAY) {
					h5_alipay();
				} else if(mType == H5_WEIXIN) {
					h5_weixin();
				} else {
					h5();
				}
			}
			
		}).start();
		
		return this;
	}
	
	
	private HashMap<String, String> getParamMap(String callback_url) {
		String money = String.valueOf(Integer.valueOf(mPrice) / 100.0f);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("attach", mParam);
		params.put("body", mPropName);
		params.put("callback_url", Contant.CALL_HOST + "/Callback/WeiFuTongH5WeiXin.ashx");
		params.put("device_info", "AND_SDK");
		params.put("mch_app_name", YunbeeVice.gameJSONInfo.gameName);
		params.put("total_fee", money);
		
		DeviceInfo deviceInfo = DeviceInfo.getInstance();
		
		String dianid = "", token = "";
		UserToken userToken = LoginRecords.getInstance(mActivity).getUserToken();
		if(userToken != null) {
			dianid = userToken.getUserId();
			token = userToken.getUserToken();
		}
		
		params.put("screen_width", deviceInfo.getScreen_width());//deviceInfo.getScreen_width());
		params.put("screen_height", deviceInfo.getScreen_height());
		params.put("imei", deviceInfo.getImei());
		params.put("imsi", deviceInfo.getImsi());
		params.put("iccid", deviceInfo.getIccid());
		params.put("phone_type", deviceInfo.getPhone_type());
		params.put("phone_version", deviceInfo.getAndroid_version());
		params.put("api_version", deviceInfo.getApi_version());
		params.put("cid", deviceInfo.getCID());
		params.put("lac", deviceInfo.getLAC());
		params.put("netstate", deviceInfo.getNet_state());
		params.put("phone_operate_system", "android");
		params.put("ip", deviceInfo.getIp());
		params.put("dianId", dianid);
		params.put("token", token);
		params.put("apk_version", SecretData.getInstance().getVersionCode());
		params.put("package_id", SecretData.getInstance().getPackageId());
		
		params.put("prop_id", mPropId);
		params.put("propName", mPropName);
		params.put("price", mPrice);
		
		params.put("param", mParam);
		params.put("explain", YunbeeVice.gameName);
		return params;
	}
	
	
	
	private void h5() {
		TAGS.log("使用网页端支付");
			String callback_url = "";
			HashMap<String, String> params = getParamMap(callback_url);
				
			String result = HttpUtils.sendPostUTF8(URL_H5, params);
			DialogUtils.closeDialog(mActivity, PayManager.dialog);
			try {
				JSONObject result2 = new JSONObject(result);
				String url = result2.getString("message");
				String urlData = "";
					
				urlData = DecodeKey.decryptKey(url, "qwerasdf");
				TAGS.log("DYH5Pay-->urlData: " + urlData);
				startH5Page(urlData, DYH5Activity.PAGE_DYH5, true);                                                                                                  
			} catch (Exception e) {
				TAGS.log("DYH5Pay-->ex: " + e.toString());
				payAbort();
			}
				
	}
			
		
	private void h5_weixin() {
		TAGS.log("使用微信wap支付");
		String callback_url = "";
		Map<String, String> params = getParamMap(callback_url);
		String result = HttpUtils.sendPostUTF8(URL_WEIXIN, params);
		DialogUtils.closeDialog(mActivity, PayManager.dialog);
		try {
			JSONObject result2 = new JSONObject(result);
			String result_code = result2.getString("result_code");
			String code = result2.getString("code");
			if(!Util.isEmpty(result_code) && !Util.isEmpty(code)) {
				if(result_code.equals("0") && code.equals("0")) {
					String pay_info = result2.getString("pay_info");
					
					startH5Page(pay_info, DYH5Activity.PAGE_DYH5_WEIXIN, false);
				}
			}
		} catch (JSONException e) {
			TAGS.log("DYH5Pay-->ex: " + e.toString());
			payAbort();
		}
		
	}
	private void h5_alipay() {
		TAGS.log("使用支付宝wap支付");
		String callback_url = "";
		Map<String, String> params = getParamMap(callback_url);
		String result = HttpUtils.sendPostUTF8(URL_ALIPAY, params);   
		DialogUtils.closeDialog(mActivity, PayManager.dialog);
		try {
			startH5Page(result, DYH5Activity.PAGE_DYH5_ALIPAY, false);
		} catch (Exception e) {
			e.printStackTrace();
			payAbort();
		}
		
	}
	

}
