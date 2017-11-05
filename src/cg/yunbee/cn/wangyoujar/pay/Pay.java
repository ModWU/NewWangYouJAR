package cg.yunbee.cn.wangyoujar.pay;

import android.app.Activity;
import cn.yunbee.cn.wangyoujar.joggle.IPayCallBack;

public class Pay {
	private String payType = "";
	private String propId = "";
	private String propName = "";
	private String param = "";
	private String price = "";
	private  String data = "";
	private String payId = "";
	private  IPayCallBack payCallBack;
	private Activity activity;
	public  String propertyPayResult = "";
	
	public Pay() {}
	
	public Pay(Activity activity, String propId, String propName, String price, String param, IPayCallBack payCallBack) {
		this.propId = propId;
		this.propName = propName;
		this.price = price;
		this.param = param;
		this.payCallBack = payCallBack;
		this.activity = activity;
	}
	public String getPropId() {
		return propId;
	}
	public void setPropId(String propId) {
		this.propId = propId;
	}
	
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public String getPropName() {
		return propName;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public IPayCallBack getPayCallBack() {
		return payCallBack;
	}
	public void setPayCallBack(IPayCallBack payCallBack) {
		this.payCallBack = payCallBack;
	}
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public String getPropertyPayResult() {
		return propertyPayResult;
	}
	public void setPropertyPayResult(String propertyPayResult) {
		this.propertyPayResult = propertyPayResult;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	
}
