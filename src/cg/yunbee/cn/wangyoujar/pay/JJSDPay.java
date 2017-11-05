package cg.yunbee.cn.wangyoujar.pay;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.content.Intent;
import cg.yunbee.cn.wangyoujar.pojo.DeviceInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.DeviceUtils;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.DYH5Activity;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import dy.compatibility.work.Contant;

public class JJSDPay extends SDKPay {
	
	private static final String URL = "http://www.toppsp.com/forward_jtsr/service";
	private static final String BACK_URL = Contant.CALL_HOST + "/Callback/ShanDe.ashx";//Contant.CALL_HOST + "/";//通知地址http://demo.mtkgame.com:8902/Callback/ShanDe.ashx
	private static final String REDIRECT_URL = "http://www.toppsp.com/pay/location.html?hp=http://onlinegame.quhuogo.com/JumpPage.aspx";
	
	
	public static final int WAP_WEIXIN = 0;
	
	private int mType;
	
	public JJSDPay(int type) {
		this.mType = type;
	}

	@Override
	public SDKPay pay() {
		TAGS.log("使用金栈衫德进行支付");
		if(mType == WAP_WEIXIN) {
			weixinPayByWap();
		}
		
		return this;
	}

	private void weixinPayByWap() {
		TAGS.log("使用WAP微信支付");
		 new Thread(new Runnable() {

				@Override
				public void run() {
					String merchantcode = "949390048163501";
					String screct = "76D83EC734101E63A6BD90B4C85A121C";
					String subject = "上海典游";
					String money = String.valueOf(Integer.valueOf(mPrice) / 100.0f);
					String backurl = BACK_URL;
					String transdate = getCurrentDate();
					
					String params = buildWeixinParamsByWap(merchantcode, screct, subject, money, backurl, transdate);
					
					String response = HttpUtils.sendPostDataUTF8(URL, params);
					
			          try {
				        	String s = response.toString();
				            JSONObject json = new JSONObject(s);
				            String merchorder_no = json.getString("merchorder_no");
				            String qrcode = json.getString("qrcode");
				            
				            qrcode = URLEncoder.encode(qrcode, "UTF-8");
				            String redirect = REDIRECT_URL + "?merchorder_no=" + merchorder_no;
				            redirect = URLEncoder.encode(redirect, "UTF-8");
				            String postUrl = "http://www.toppsp.com/pay/location.html?hp=" + qrcode + "&redirect_url=" + redirect;
				            
				            DialogUtils.closeDialog(mActivity, PayManager.dialog);
				            
				            startH5Page(postUrl, DYH5Activity.PAGE_JZSD_WEIXIN, false);
			          } catch (Exception e) {
			        	  TAGS.log("JZSDPay->weixinPayByWap: " + e.toString());
			        	  payAbort();
			          }
				}
		    	  
		      }).start();
	}
	
	private String buildWeixinParamsByWap(String merchantcode, String screct, String subject, String money, String backurl, String transdate) {
		JSONObject json = new JSONObject();
		DeviceUtils deviceUtils = new DeviceUtils(mActivity);
	    try {
	      json.put("service", "sand.wxpay");
	      json.put("merchantcode", merchantcode);
	      json.put("spbillcreateip", DeviceInfo.getInstance().getNetIp());
	      json.put("subject", mPayId);
	      json.put("money", money);
	      json.put("paytype", "2");
	      json.put("backurl", backurl);
	      json.put("mtype", "Android");
	      json.put("app_name", deviceUtils.appName);
	      json.put("bundle_id", deviceUtils.packageName);
	      json.put("transdate", transdate);
	      json.put("sign", "");
	      
	      String sign = json.toString();
	      while (sign.contains("\\"))
	    	  sign = json.toString().replace("\\", "");
	      sign = getSha512(sign, screct);
	      json.remove("sign");
	      json.put("sign", sign);
	      return json.toString();
	    } catch(Exception e) {
	    }
	    return "";
	}
	
	private String getSha512(String content, String key) {
		content = (key == null ? content : content + key);
		return Util.getHash(content, "SHA-512");
	}
	
	private static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        return df.format(new Date());
    }

}
