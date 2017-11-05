package cg.yunbee.cn.wangyoujar.sdkhelper;

import android.app.Activity;
import android.icu.text.DisplayContext.Type;
import cg.yunbee.cn.wangyoujar.pay.PayType;
import dy.compatibility.work.Utils;


/**
 * 写这些类集主要是为了方便修改和做较少的if判断
 * @author Administrator
 *
 */
public abstract class SPayBuilder {
	protected Activity mActivity;
	
	
	
	public int getIconId() {
		return Utils.getResourceId(mActivity,
				"drawable", getIconName());
	}
	
	protected abstract String getIconName();
	public abstract String getPayName();
	public abstract String getPayDetail();
	
	
	public static SPayBuilder build(Activity activity, String type) {
		if(PayType.TYPE_AIBEI.equals(type)) {
			return new AibeiBuilder(activity);
		} else if(PayType.TYPE_ALIPAY.equals(type)) {
			return new AlipayBuilder(activity);
		} else if(PayType.TYPE_WEIXIN.equals(type)) {
			return new WeixinBuilder(activity);
		} else if(PayType.TYPE_OKPAY_WEIXIN.equals(type)) {
			return new OKPay_weixinBuilder(activity);
		} else if(PayType.TYPE_OKPAY_ALIPAY.equals(type)) {
			return new OKPay_alipayBuilder(activity);
		} else if(PayType.TYPE_YHXF_ALIPAY.equals(type)) {
			return new YHXF_alipayBuilder(activity);
		} else if(PayType.TYPE_YHXF_WEIXIN.equals(type)) {
			return new YHXF_weixinBuilder(activity);
		} else if(PayType.TYPE_YST.equals(type)) {
			return new YSTBuilder(activity);
		} else if(PayType.TYPE_WFT_ALIPAY.equals(type)) {
			return new WFT_alipayBuilder(activity);
		} else if(PayType.TYPE_WFT_ALIPAY.equals(type)) {
			return new WFT_alipayBuilder(activity);
		} else if(PayType.TYPE_WFT_QQ.equals(type)) {
			return new WFT_qqBuilder(activity);
		} else if(PayType.TYPE_JJSD_WAP_WEIXIN.equals(type)) {
			return new JJSDWap_weixinBuilder(activity);
		} else if(PayType.TYPE_WFT_WAP_WEIXIN.equals(type)) {
			return new WFTWap_weixinBuilder(activity);
		} else if(PayType.TYPE_QRCODE.equals(type)) {
			return new QRcodeBuilder(activity);
		} else if(PayType.TYPE_DYH5.equals(type)) {
			return new DYH5Builder(activity);
		} else if(PayType.TYPE_JXHY.equals(type)) {
			return new JXHYBuilder(activity);
		} else {
			return new NULLBuilder(activity);
		}
	}
	
	
	
	public SPayBuilder(Activity activity) {
		mActivity = activity;
	}
	
	
}
