package cg.yunbee.cn.wangyoujar.feeInfo;

public class PayItem {
	private String payType;
	private String prop_id;
	private String scanUrl;
	private String pay_id;
	private String aibei_id;
	private int price;//单位：分
	private String propName;//道具名称
	private boolean isCancelPay;
	public String getPay_id() {
		return pay_id;
	}
	
	public boolean isCancelPay() {
		return isCancelPay;
	}

	public void setCancelPay(boolean isCancelPay) {
		this.isCancelPay = isCancelPay;
	}

	public void setPay_id(String pay_id) {
		this.pay_id = pay_id;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getPropName() {
		return propName;
	}
	public String getScanUrl() {
		return scanUrl;
	}
	public void setScanUrl(String scanUrl) {
		this.scanUrl = scanUrl;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	public String getProp_id() {
		return prop_id;
	}
	public void setProp_id(String prop_id) {
		this.prop_id = prop_id;
	}
	
	public String getAibei_id() {
		return aibei_id;
	}

	public void setAibei_id(String aibei_id) {
		this.aibei_id = aibei_id;
	}

	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public void clear() {
		prop_id = null;
		pay_id = null;
		price = 0;
		propName = null;
		payType = null;
	}
	
}
