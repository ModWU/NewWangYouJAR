package cg.yunbee.cn.wangyoujar.pay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import cg.yunbee.cn.wangyoujar.Yunbee;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.keycode.DecodeKey;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkpay.Alipay_PayResult;
import cg.yunbee.cn.wangyoujar.sdkpay.Alipay_SignUtils;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import dy.compatibility.joggle.IAlipaySDK;
import dy.compatibility.work.Contant;
import dy.compatibility.work.SDKOptProducer;

public class AlipayPay extends SDKPay {
	
	// 商户PID
	private static final String ALIPAY_PARTNER = "rOKsZRB971TKsy2aeNmhtMHm/H2oJpTS";//2088211223084261
	// 商户收款账号
	private static final String ALIPAY_SELLER = "kUcLWxkqvtkxSI14W9kekb0rPgEw2gnt";//pengxiang@Yunbee.cn
	// 商户私钥，pkcs8格式
	private static final String ALIPAY_RSA_PRIVATE = "MC/erG8FOGynDmHDdYua9ewFYOZl18oKbWCjag7tB9+2J0EPOWb5ix0zgojbllo+dkLt7zSdb+Pg4fvQTtDWXw1GTvy4okc44cYf6l5KJiGQYz1DWZpjOkQWRBvqLLKsVuNRQ4ZwpRJu7NRI0jceOjCOhPA1XU1CfdnkvlyUe4fUrE6bZmGgMZY314LuAbgXBQs+f0ru7TWKX658Rm0nX4+Bm5yynhx3WtCHTRXBN3EVTPjVRxc6IJAoKmVgOgMrgBSGF3F0ZZrMXwRqpGyJxBDi0RjPUZI0IW7YxSxi4+V7gmxHHT/a3aTuDizO11QervZRD8K+a9PhCZad5I0bU+e6sMx+q/PHMYwrG0SaY+RoXe5qRAffKBEs74p52wgcAXiy1Fz65chh1boiang4VAe/gc8ZbsvEBfxHRJ07nnL/rUhrkOeIvHTk52p7VEm+iCO2v+Ay85WcAlUS8P+590BxqoAW8N3CEyS8U3OtBd0qnaG3aQFtbpHQqtf20nWCAEPVXEFmxaBkD8Z1+bXl9roDYx8zPAh6fOIQZ5ZxHCzAp4qZb/jsOTEPUIiOD4hGzXdVdzvapETefA5bGhyXyHXLO4VLVpHgjZTjhOcSTUx85YtPqMqmR4D6eOErcYYaU4TL8ddT/871QmPQ5wz7ZN++SPvvMG0QZ83MagBnC2KsPOi1cmQEX3KCJt87XPPZ2Ply6BZbaGg6j9U9IWVf++8GoiMKyeHrzT4s8w7ch1CIHwsjE1y+JtJXTO26A5GYai6mcym5pwBTPanZKkst04xnXa77Y9p46TRCL7H4J4cIE/WRMJqAVVz74c5Q/xizbIm7Fgf90QT30jG78JWhBhvhDoUiR4dq0B7k/ki1ITTTSPhp5HZM9T9Y2Ux4CBh0BOkDbMGTLRgIqVsDmtL74nzZ+gsNKo1FfCQ6PK/n2V8mSSas73snUdyZOz/uBtXLoGZq2Nw3SVqUa3pZUz03Y+X1nw3jHFT2ILaw0YHlEXS1wlFE5NM5Yv0hTxt+CD+1iYIjvnYjzqZS4adb0ZVcGJlz8JrtsvTLMhIN0ovm0uIC1bk7IyFgW2/EI53sFCozSBuKZVDICeX+cCig8N13TovR/TNumyDIq7zzjtb94tzB5vx9qCaU0g==";
	//MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAL0Jkx1fMUi49k1a0cDGoM9123Pg7p/T4lq1TsvywRW29SQqB3sa461lyN7C3sPHkG9vKqjf9EKuB/1ApL+VZJspa2W8zBxf6dt+VHaCOg8Aj2K3hJ/w9o9M0QsKaMCBUtFT/ZncRKNQAMufzPGjlpLlkhh6fCLmz/4CRLcOpfF1AgMBAAECgYAl8GL/NRiuELkWA1EvkG271VqK4iziFONL8zFySzEnS4XdkTXZJidlqJTs6E1PG6FITBZSuEfMjiL6V9v4u2HqTcbnyHGSSYyZdP7diefdEGeyjnJuRdtv5vj2SmQXGrqOFAKnzFO/HaDMeZSPftfGR1VKm3c1Opwz0Wq4XsCJgQJBAON5B2OINBldNUAaRsfi0mo2ku/nPpN51SPE5Q59bppSaB+FnF0gq6F07nwkVs9t/AE4mAm4d3FEECpkF0N93RUCQQDUvpXe2qLKGolKJuhNYLJGujnVBnLHeF9CUDnFdCMJ0/Xuwa6I3Ixark/PL/1YBEnBz4wkgs4CjKQQxwi/6JrhAkEAlDYOf2aGc6RMhujYB3MdoEDQumlUV2jBXB31FNzbTMe2uhsnR3J7AacboF1ZBqxlzzHdti+v+2falJREqCABBQJBAKscv56Ha/fycApcUOwrojiRAIL+tMMLZlMIA+91AdmdymlHDwK1oY4uNbMHflWU3V4teSLEsr4KMMvtynPQuwECQQCKi7PsB5kyoOyU+56SpF1dnOywcKM3gQPiyzFP9LX4ZFGjGYqU1LJtiG3ZeJzb6G7iWK+Gsxeu9Jlb2CjSflSC
	// 支付宝公钥
	//private static final String ALIPAY_RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	//MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB

	@Override
	public AlipayPay pay() {
		TAGS.log("pay_id:" + mPayId + ", price:" + mPrice);
		TAGS.log("使用支付宝进行支付");
		try {
			if (TextUtils.isEmpty(DecodeKey.decrypt(ALIPAY_PARTNER, YunbeeVice.gameJSONInfo.secretKey)) || TextUtils.isEmpty(DecodeKey.decrypt(ALIPAY_RSA_PRIVATE, YunbeeVice.gameJSONInfo.secretKey))
					|| TextUtils.isEmpty(DecodeKey.decrypt(ALIPAY_SELLER, YunbeeVice.gameJSONInfo.secretKey))) {
				TAGS.log("参数不完整");
				payAbort();
				return this;
			}
			String propertyName = mPropName;
			String yuan = "";
			float fenint = Float.valueOf(mPrice);
			float yuanFloat = fenint / 100.0f;
			yuan = String.valueOf(yuanFloat);
			String orderInfo = getOrderInfo(propertyName, mPayId, yuan, mData);
			
			
			/**
			 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
			 */
			String sign = sign(orderInfo);
			try {
				/**
				 * 仅需对sign 做URL编码
				 */
				sign = URLEncoder.encode(sign, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			/**
			 * 完整的符合支付宝参数规范的订单信息
			 */
			final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
					+ getSignType();
			TAGS.log("payInfo:" + payInfo);
			Runnable payRunnable = new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					/*// 构造PayTask 对象
					com.alipay.sdk.app.PayTask alipay = new com.alipay.sdk.app.PayTask(
							YunbeeVice.activity);
					// 调用支付接口，获取支付结果
					String result = alipay.pay(payInfo, true);*/
					
					DialogUtils.closeDialog(mActivity, PayManager.dialog);
					
					SDKOptProducer producer = SDKOptProducer.newInstance(mActivity);
					
					IAlipaySDK alipaySdk = producer.getAlipaySDK();
					
					String result = alipaySdk.alipay(mActivity, payInfo);
					
					Alipay_PayResult payResult = new Alipay_PayResult(result);
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					try {
						if (TextUtils.equals(resultStatus, "9000")) {
							paySuccess(mData);
						} else {
							if (TextUtils.equals(resultStatus, "6001")) {
								payCancel(mData);
							} else {
								payAbort(mData);
							}
						}
					} catch (Exception e) {
						payAbort(mData);
					}
				}
			};

			// 必须异步调用
			Thread payThread = new Thread(payRunnable);
			payThread.start();

		} catch (Exception e) {
			TAGS.log("alipay1->" + Log.getStackTraceString(e));
			payAbort();
		} catch (NoClassDefFoundError e) {
			TAGS.log("alipay2->" + Log.getStackTraceString(e));
			payAbort();
		} catch (Error e) {
			TAGS.log("alipay3->" + Log.getStackTraceString(e));
			payAbort();
		}
		
		return this;
	}
	
	
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		String privateData = ALIPAY_RSA_PRIVATE;
		
		try {
			privateData = DecodeKey.decrypt(ALIPAY_RSA_PRIVATE, YunbeeVice.gameJSONInfo.secretKey);
		} catch (Exception e) {
			TAGS.log("--sign(支付宝数据)-->ex: " + e.toString());
		}
		
		TAGS.log("--sign(支付宝数据)-->privateData: " + privateData);
		
		return Alipay_SignUtils.sign(content, privateData);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}
	
	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private String getOrderInfo(String subject, String body,
			String price, String data) {
		
		String partner = ALIPAY_PARTNER;
		String seller_id = ALIPAY_SELLER;
		try {
			partner = DecodeKey.decrypt(ALIPAY_PARTNER, YunbeeVice.gameJSONInfo.secretKey);
			seller_id = DecodeKey.decrypt(ALIPAY_SELLER, YunbeeVice.gameJSONInfo.secretKey);
		} catch (Exception e) {
			TAGS.log("--getOrderInfo(支付宝数据)-->ex: " + e.toString());
		}
		
		TAGS.log("--getOrderInfo(支付宝数据)-->partner: " + partner);
		
		TAGS.log("--getOrderInfo(支付宝数据)-->seller_id: " + seller_id);

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + partner + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + seller_id + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + data + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=\"" + Contant.CALL_HOST + "/Callback/alipay.ashx" + "\"";
		// + "\""
		// +
		// "http://yunbeepay.mtkgame.com:8889/SDK/20/8e67befb-b871-40ac-bbc3-bc8c86392c9e/Common.ashx"
		// + "\"";

		// orderInfo += "&notify_url=" + "\""
		// + "http://yunbeepay.mtkgame.com:8889/SDK/zhifubao.ashx" + "\"";

		/*orderInfo += "&notify_url=" + "\"" + GetNotifyAddress.alipayNotifyUrl
				+ "\"";*/

		// orderInfo += "&notify_url=" + "\""
		// + "http://116.226.147.232:28889/SDK/zhifubao.ashx" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

}
