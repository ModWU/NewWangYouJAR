package cg.yunbee.cn.wangyoujar.pay;

import cg.yunbee.cn.wangyoujar.pojo.TAGS;

public class NULLPay extends SDKPay {

	@Override
	public NULLPay pay() {
		TAGS.log("该支付方式有误，请检查您的支付方式");
		return this;
	}

}
