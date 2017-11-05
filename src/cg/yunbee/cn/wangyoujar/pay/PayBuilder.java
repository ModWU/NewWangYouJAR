package cg.yunbee.cn.wangyoujar.pay;

public class PayBuilder {
	
	public static final NULLPay NULL = new NULLPay();
	
	private static final Pay PAY_NULL = new Pay();
	
	
	private Pay mPay = PAY_NULL;
	
	
	private PayBuilder(Pay pay) {
		if(pay != null)
			mPay = pay;
	}
	
	
	public static PayBuilder build(Pay pay) {
		return new PayBuilder(pay);
	}
	
	
	public SDKPay buildPay() {
		String payType = mPay.getPayType();
		
		SDKPay sdkPay = NULL;
		
		if(PayType.TYPE_ALIPAY.equals(payType)) {
			sdkPay = new AlipayPay();
			
		} else if(PayType.TYPE_WEIXIN.equals(payType)) {
			sdkPay = new WeixinPay();
			
		} else if(PayType.TYPE_AIBEI.equals(payType)) {
			sdkPay = new AibeiPay();
			
		} else if(PayType.TYPE_OKPAY_WEIXIN.equals(payType)) {
			sdkPay = new OKPayPay(OKPayPay.WEIXIN);
			
		} else if(PayType.TYPE_OKPAY_ALIPAY.equals(payType)) {
			sdkPay = new OKPayPay(OKPayPay.ALIPAY);
			
		} else if(PayType.TYPE_YHXF_WEIXIN.equals(payType)) {
			sdkPay = new YHXFPay(YHXFPay.WEIXIN);
			
		} else if(PayType.TYPE_YHXF_ALIPAY.equals(payType)) {
			sdkPay = new YHXFPay(YHXFPay.ALIPAY);
			
		} else if(PayType.TYPE_JXHY.equals(payType)) {
			//sdkPay = new JXHYPay();
			
		} else if(PayType.TYPE_YST.equals(payType)) {
			sdkPay = new YstPay();
			
		} else if(PayType.TYPE_WFT_WEIXIN.equals(payType)) {
			sdkPay = new WFTPay(WFTPay.WEIXIN);
			
		} else if(PayType.TYPE_WFT_ALIPAY.equals(payType)) {
			sdkPay = new WFTPay(WFTPay.ALIPAY);
			
		} else if(PayType.TYPE_WFT_QQ.equals(payType)) {
			sdkPay = new WFTPay(WFTPay.QQ);
			
		} else if(PayType.TYPE_WFT_WAP_WEIXIN.equals(payType)) {
			sdkPay = new WFTPay(WFTPay.WAP_WEIXIN);
			
		} else if(PayType.TYPE_JJSD_WAP_WEIXIN.equals(payType)) {
			sdkPay = new JJSDPay(JJSDPay.WAP_WEIXIN);
			
		} else if(PayType.TYPE_QRCODE.equals(payType)) {
			sdkPay = new QRcodePay();
			
		} else if(PayType.TYPE_DYH5.equals(payType)) {
			sdkPay = new DYH5Pay(DYH5Pay.H5_ALL);
			
		} else if(PayType.TYPE_DYH5_ALIPAY.equals(payType)) {
			sdkPay = new DYH5Pay(DYH5Pay.H5_ALIPAY);
			
		} else if(PayType.TYPE_DYH5_WEIXIN.equals(payType)) {
			sdkPay = new DYH5Pay(DYH5Pay.H5_WEIXIN);
			
		}
		
		sdkPay.setParam(mPay);
		
		return sdkPay;
	}
	
}
