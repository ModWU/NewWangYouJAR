package dy.compatibility.joggle;

import java.util.Map;

import android.app.Activity;
import dy.compatibility.callback.WFTCallback;

public interface IWFTSDK {
	/*
	 * params.put("body", "SPay收款"); // 商品名称
        params.put("service", "unified.trade.pay"); // 支付类型
        params.put("version", "1.0"); // 版本
        params.put("mch_id", "7552900037"); // 威富通商户号
        //        params.put("mch_id", mchId.getText().toString()); // 威富通商户号
        params.put("notify_url", " "); // 后台通知url
        params.put("nonce_str", genNonceStr()); // 随机数
        payOrderNo = genOutTradNo();
        params.put("out_trade_no", payOrderNo); //订单号
        params.put("mch_create_ip", "127.0.0.1"); // 机器ip地址
        params.put("total_fee", "1"); // 总金额
        params.put("limit_credit_pay", "0"); // 是否限制信用卡支付， 0：不限制（默认），1：限制
        String sign = createSign("11f4aca52cf400263fdd8faf7a69e007", params); // 01133be809cd03a4726e8b861b58ad7a  威富通密钥
        
        params.put("sign", sign); // sign签名
	 */
	Map<String, String> getXMLData(String url, String body, String service, String version, String mch_id, String notify_url, String nonce_str, String out_trade_no, String mch_create_ip, String total_fee, String limit_credit_pay, String sign);
	void weixinPay(Activity activity, Map<String, String> dataMap, WFTCallback callback);
	void alipayPay(Activity activity, Map<String, String> dataMap, WFTCallback callback);
	void qqWalletPay(Activity activity, Map<String, String> dataMap, WFTCallback callback);
}
