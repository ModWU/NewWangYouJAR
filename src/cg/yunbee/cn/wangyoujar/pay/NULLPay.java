package cg.yunbee.cn.wangyoujar.pay;

import cg.yunbee.cn.wangyoujar.pojo.TAGS;

public class NULLPay extends SDKPay {

	@Override
	public NULLPay pay() {
		TAGS.log("��֧����ʽ������������֧����ʽ");
		return this;
	}

}
