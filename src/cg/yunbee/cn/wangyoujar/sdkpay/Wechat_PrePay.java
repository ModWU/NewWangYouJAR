package cg.yunbee.cn.wangyoujar.sdkpay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import dy.compatibility.work.Contant;

public class Wechat_PrePay {
	private final static String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	public final static String key = "dianyoudianyoudianyoudianyoudian";
	public static volatile String prepay_id = null;
	
	public static final Object WAIT_LOCK = new Object();

	/*
	 * return prePayId
	 */
	public static void prePay(Context context, String productName,
			String fee, String ip, String orderId, String payId) {
		prepay_id = null;

		String appId = Wechat_Pay.appid;
		String attach = payId;
		String body = productName;// 商品或支付单简要描述
		String device_info = "ANDROID";
		String fee_type = "CNY";
		String mch_id = Wechat_Pay.partnerid;
		String nonce_str = getRandomString(32);// 随机字符串，不长于32位
		String notify_url = Contant.CALL_HOST + "/Callback/weixin.ashx";// 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
		String out_trade_no = orderId;// 商户系统内部的订单号,32个字符内、可包含字母
		String sign = "";
		String spbill_create_ip = ip;// ip
		String total_fee = fee;// 订单总金额，单位为分
		String trade_type = "APP";// 支付类型
		String stringA = "appid=" + appId + "&attach=" + attach + "&body="+ body + "&device_info="+ device_info
				+ "&fee_type=" + fee_type + "&mch_id=" + mch_id + "&nonce_str=" + nonce_str
				+ "&notify_url=" + notify_url + "&out_trade_no=" + out_trade_no
				 + "&spbill_create_ip=" + spbill_create_ip//
				+ "&total_fee=" + total_fee + "&trade_type=" + trade_type;
		String stringB = stringA + "&key=" + key;
		TAGS.log("stringA:" + stringA);
		sign = md5(stringB).toUpperCase();
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		sb.append("    <appid>" + appId + "</appid>");
		sb.append("    <attach>" + attach + "</attach>");
		sb.append("    <body>" + body + "</body>");
		sb.append("    <device_info>" + device_info + "</device_info>");
		sb.append("    <fee_type>" + fee_type + "</fee_type>");
		sb.append("    <mch_id>" + mch_id + "</mch_id>");
		sb.append("    <nonce_str>" + nonce_str + "</nonce_str>");
		sb.append("    <notify_url>" + notify_url + "</notify_url>");
		sb.append("    <out_trade_no>" + out_trade_no + "</out_trade_no>");
		sb.append("    <spbill_create_ip>" + spbill_create_ip
				+ "</spbill_create_ip>");
		sb.append("    <total_fee>" + total_fee + "</total_fee>");
		sb.append("    <trade_type>" + trade_type + "</trade_type>");
		sb.append("    <sign>" + sign + "</sign>");
		sb.append("</xml>");
		TAGS.log("prepay 参数:" + sb);
		final String entity = sb.toString();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String content = HttpUtils.sendPostDataUTF8(url, entity);
				if (content != null && content.length() > 0) {
					TAGS.log("prepay 返回：" + content);
					String return_code = getValueFromXML(content, "return_code");
					String result_code = getValueFromXML(content, "result_code");
					if ("SUCCESS".equals(return_code)
							&& "SUCCESS".equals(result_code)) {
						prepay_id = getValueFromXML(content, "prepay_id");
						TAGS.log("prepay_id：" + prepay_id);
					} else {
						prepay_id = "error";
						
					}
				} else {
					prepay_id = "error";
				}
				
				synchronized (WAIT_LOCK) {
					WAIT_LOCK.notify();
				}
				
			}
		}).start();

	}

	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String md5(String content) {
		String result = null;
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			result = convertFromByteToHexString(md5.digest(content
					.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String convertFromByteToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			String tmp = Integer.toHexString(bytes[i] & 0xFF);
			if (tmp.length() == 1) {
				result += "0" + tmp;
			} else {
				result += tmp;
			}
		}
		return result;
	}

	private static String getValueFromXML(String content, String nodeName) {
		String value = null;
		try {
			ByteArrayInputStream tInputStringStream = null;
			if (content != null && !content.trim().equals("")) {
				tInputStringStream = new ByteArrayInputStream(
						content.getBytes());
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(tInputStringStream, "UTF-8");
				int eventType = 0;
				eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG: {
						if (parser.getName().equals(nodeName)) {
							eventType = parser.next();
							value = parser.getText();
						}
						break;
					}
					}
					eventType = parser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

}
