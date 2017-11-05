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
	
	// �̻�PID
	private static final String ALIPAY_PARTNER = "rOKsZRB971TKsy2aeNmhtMHm/H2oJpTS";//2088211223084261
	// �̻��տ��˺�
	private static final String ALIPAY_SELLER = "kUcLWxkqvtkxSI14W9kekb0rPgEw2gnt";//pengxiang@Yunbee.cn
	// �̻�˽Կ��pkcs8��ʽ
	private static final String ALIPAY_RSA_PRIVATE = "MC/erG8FOGynDmHDdYua9ewFYOZl18oKbWCjag7tB9+2J0EPOWb5ix0zgojbllo+dkLt7zSdb+Pg4fvQTtDWXw1GTvy4okc44cYf6l5KJiGQYz1DWZpjOkQWRBvqLLKsVuNRQ4ZwpRJu7NRI0jceOjCOhPA1XU1CfdnkvlyUe4fUrE6bZmGgMZY314LuAbgXBQs+f0ru7TWKX658Rm0nX4+Bm5yynhx3WtCHTRXBN3EVTPjVRxc6IJAoKmVgOgMrgBSGF3F0ZZrMXwRqpGyJxBDi0RjPUZI0IW7YxSxi4+V7gmxHHT/a3aTuDizO11QervZRD8K+a9PhCZad5I0bU+e6sMx+q/PHMYwrG0SaY+RoXe5qRAffKBEs74p52wgcAXiy1Fz65chh1boiang4VAe/gc8ZbsvEBfxHRJ07nnL/rUhrkOeIvHTk52p7VEm+iCO2v+Ay85WcAlUS8P+590BxqoAW8N3CEyS8U3OtBd0qnaG3aQFtbpHQqtf20nWCAEPVXEFmxaBkD8Z1+bXl9roDYx8zPAh6fOIQZ5ZxHCzAp4qZb/jsOTEPUIiOD4hGzXdVdzvapETefA5bGhyXyHXLO4VLVpHgjZTjhOcSTUx85YtPqMqmR4D6eOErcYYaU4TL8ddT/871QmPQ5wz7ZN++SPvvMG0QZ83MagBnC2KsPOi1cmQEX3KCJt87XPPZ2Ply6BZbaGg6j9U9IWVf++8GoiMKyeHrzT4s8w7ch1CIHwsjE1y+JtJXTO26A5GYai6mcym5pwBTPanZKkst04xnXa77Y9p46TRCL7H4J4cIE/WRMJqAVVz74c5Q/xizbIm7Fgf90QT30jG78JWhBhvhDoUiR4dq0B7k/ki1ITTTSPhp5HZM9T9Y2Ux4CBh0BOkDbMGTLRgIqVsDmtL74nzZ+gsNKo1FfCQ6PK/n2V8mSSas73snUdyZOz/uBtXLoGZq2Nw3SVqUa3pZUz03Y+X1nw3jHFT2ILaw0YHlEXS1wlFE5NM5Yv0hTxt+CD+1iYIjvnYjzqZS4adb0ZVcGJlz8JrtsvTLMhIN0ovm0uIC1bk7IyFgW2/EI53sFCozSBuKZVDICeX+cCig8N13TovR/TNumyDIq7zzjtb94tzB5vx9qCaU0g==";
	//MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAL0Jkx1fMUi49k1a0cDGoM9123Pg7p/T4lq1TsvywRW29SQqB3sa461lyN7C3sPHkG9vKqjf9EKuB/1ApL+VZJspa2W8zBxf6dt+VHaCOg8Aj2K3hJ/w9o9M0QsKaMCBUtFT/ZncRKNQAMufzPGjlpLlkhh6fCLmz/4CRLcOpfF1AgMBAAECgYAl8GL/NRiuELkWA1EvkG271VqK4iziFONL8zFySzEnS4XdkTXZJidlqJTs6E1PG6FITBZSuEfMjiL6V9v4u2HqTcbnyHGSSYyZdP7diefdEGeyjnJuRdtv5vj2SmQXGrqOFAKnzFO/HaDMeZSPftfGR1VKm3c1Opwz0Wq4XsCJgQJBAON5B2OINBldNUAaRsfi0mo2ku/nPpN51SPE5Q59bppSaB+FnF0gq6F07nwkVs9t/AE4mAm4d3FEECpkF0N93RUCQQDUvpXe2qLKGolKJuhNYLJGujnVBnLHeF9CUDnFdCMJ0/Xuwa6I3Ixark/PL/1YBEnBz4wkgs4CjKQQxwi/6JrhAkEAlDYOf2aGc6RMhujYB3MdoEDQumlUV2jBXB31FNzbTMe2uhsnR3J7AacboF1ZBqxlzzHdti+v+2falJREqCABBQJBAKscv56Ha/fycApcUOwrojiRAIL+tMMLZlMIA+91AdmdymlHDwK1oY4uNbMHflWU3V4teSLEsr4KMMvtynPQuwECQQCKi7PsB5kyoOyU+56SpF1dnOywcKM3gQPiyzFP9LX4ZFGjGYqU1LJtiG3ZeJzb6G7iWK+Gsxeu9Jlb2CjSflSC
	// ֧������Կ
	//private static final String ALIPAY_RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	//MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB

	@Override
	public AlipayPay pay() {
		TAGS.log("pay_id:" + mPayId + ", price:" + mPrice);
		TAGS.log("ʹ��֧��������֧��");
		try {
			if (TextUtils.isEmpty(DecodeKey.decrypt(ALIPAY_PARTNER, YunbeeVice.gameJSONInfo.secretKey)) || TextUtils.isEmpty(DecodeKey.decrypt(ALIPAY_RSA_PRIVATE, YunbeeVice.gameJSONInfo.secretKey))
					|| TextUtils.isEmpty(DecodeKey.decrypt(ALIPAY_SELLER, YunbeeVice.gameJSONInfo.secretKey))) {
				TAGS.log("����������");
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
			 * �ر�ע�⣬�����ǩ���߼���Ҫ���ڷ���ˣ�����˽Կй¶�ڴ����У�
			 */
			String sign = sign(orderInfo);
			try {
				/**
				 * �����sign ��URL����
				 */
				sign = URLEncoder.encode(sign, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			/**
			 * �����ķ���֧���������淶�Ķ�����Ϣ
			 */
			final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
					+ getSignType();
			TAGS.log("payInfo:" + payInfo);
			Runnable payRunnable = new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					/*// ����PayTask ����
					com.alipay.sdk.app.PayTask alipay = new com.alipay.sdk.app.PayTask(
							YunbeeVice.activity);
					// ����֧���ӿڣ���ȡ֧�����
					String result = alipay.pay(payInfo, true);*/
					
					DialogUtils.closeDialog(mActivity, PayManager.dialog);
					
					SDKOptProducer producer = SDKOptProducer.newInstance(mActivity);
					
					IAlipaySDK alipaySdk = producer.getAlipaySDK();
					
					String result = alipaySdk.alipay(mActivity, payInfo);
					
					Alipay_PayResult payResult = new Alipay_PayResult(result);
					String resultStatus = payResult.getResultStatus();
					// �ж�resultStatus Ϊ��9000�������֧���ɹ�������״̬�������ɲο��ӿ��ĵ�
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

			// �����첽����
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
	 * sign the order info. �Զ�����Ϣ����ǩ��
	 * 
	 * @param content
	 *            ��ǩ��������Ϣ
	 */
	private String sign(String content) {
		String privateData = ALIPAY_RSA_PRIVATE;
		
		try {
			privateData = DecodeKey.decrypt(ALIPAY_RSA_PRIVATE, YunbeeVice.gameJSONInfo.secretKey);
		} catch (Exception e) {
			TAGS.log("--sign(֧��������)-->ex: " + e.toString());
		}
		
		TAGS.log("--sign(֧��������)-->privateData: " + privateData);
		
		return Alipay_SignUtils.sign(content, privateData);
	}

	/**
	 * get the sign type we use. ��ȡǩ����ʽ
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}
	
	/**
	 * create the order info. ����������Ϣ
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
			TAGS.log("--getOrderInfo(֧��������)-->ex: " + e.toString());
		}
		
		TAGS.log("--getOrderInfo(֧��������)-->partner: " + partner);
		
		TAGS.log("--getOrderInfo(֧��������)-->seller_id: " + seller_id);

		// ǩԼ���������ID
		String orderInfo = "partner=" + "\"" + partner + "\"";

		// ǩԼ����֧�����˺�
		orderInfo += "&seller_id=" + "\"" + seller_id + "\"";

		// �̻���վΨһ������
		orderInfo += "&out_trade_no=" + "\"" + data + "\"";

		// ��Ʒ����
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// ��Ʒ����
		orderInfo += "&body=" + "\"" + body + "\"";

		// ��Ʒ���
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// �������첽֪ͨҳ��·��
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

		// ����ӿ����ƣ� �̶�ֵ
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// ֧�����ͣ� �̶�ֵ
		orderInfo += "&payment_type=\"1\"";

		// �������룬 �̶�ֵ
		orderInfo += "&_input_charset=\"utf-8\"";

		// ����δ����׵ĳ�ʱʱ��
		// Ĭ��30���ӣ�һ����ʱ���ñʽ��׾ͻ��Զ����رա�
		// ȡֵ��Χ��1m��15d��
		// m-���ӣ�h-Сʱ��d-�죬1c-���죨���۽��׺�ʱ����������0��رգ���
		// �ò�����ֵ������С���㣬��1.5h����ת��Ϊ90m��
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_tokenΪ���������Ȩ��ȡ����alipay_open_id,���ϴ˲����û���ʹ����Ȩ���˻�����֧��
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// ֧��������������󣬵�ǰҳ����ת���̻�ָ��ҳ���·�����ɿ�
		orderInfo += "&return_url=\"m.alipay.com\"";

		// �������п�֧���������ô˲���������ǩ���� �̶�ֵ ����ҪǩԼ���������п����֧��������ʹ�ã�
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

}
