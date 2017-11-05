package dy.compatibility.joggle;

import java.util.Map;

import android.app.Activity;
import dy.compatibility.callback.WFTCallback;

public interface IWFTSDK {
	/*
	 * params.put("body", "SPay�տ�"); // ��Ʒ����
        params.put("service", "unified.trade.pay"); // ֧������
        params.put("version", "1.0"); // �汾
        params.put("mch_id", "7552900037"); // ����ͨ�̻���
        //        params.put("mch_id", mchId.getText().toString()); // ����ͨ�̻���
        params.put("notify_url", " "); // ��̨֪ͨurl
        params.put("nonce_str", genNonceStr()); // �����
        payOrderNo = genOutTradNo();
        params.put("out_trade_no", payOrderNo); //������
        params.put("mch_create_ip", "127.0.0.1"); // ����ip��ַ
        params.put("total_fee", "1"); // �ܽ��
        params.put("limit_credit_pay", "0"); // �Ƿ��������ÿ�֧���� 0�������ƣ�Ĭ�ϣ���1������
        String sign = createSign("11f4aca52cf400263fdd8faf7a69e007", params); // 01133be809cd03a4726e8b861b58ad7a  ����ͨ��Կ
        
        params.put("sign", sign); // signǩ��
	 */
	Map<String, String> getXMLData(String url, String body, String service, String version, String mch_id, String notify_url, String nonce_str, String out_trade_no, String mch_create_ip, String total_fee, String limit_credit_pay, String sign);
	void weixinPay(Activity activity, Map<String, String> dataMap, WFTCallback callback);
	void alipayPay(Activity activity, Map<String, String> dataMap, WFTCallback callback);
	void qqWalletPay(Activity activity, Map<String, String> dataMap, WFTCallback callback);
}
