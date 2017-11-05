package cg.yunbee.cn.wangyoujar.feeInfo;


public class PayWay implements Comparable<PayWay>{
	private String name;
	private float discount;
	private int order;
	private String payDetails;
	private int imgId;
	private String payName;
	private String discountDetails;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	
	public String getDiscountDetails() {
		return discountDetails;
	}
	public void setDiscountDetails(String discountDetails) {
		this.discountDetails = discountDetails;
	}
	public String getPayDetails() {
		return payDetails;
	}
	public void setPayDetails(String payDetails) {
		this.payDetails = payDetails;
	}
	public int getImgId() {
		return imgId;
	}
	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
	
	public String getPayName() {
		return payName;
	}
	public void setPayName(String payName) {
		this.payName = payName;
	}
	@Override
	public int compareTo(PayWay another) {
		if(order == another.order) return 0;
		return order < another.order ? -1 : 1;
	}
	@Override
	public String toString() {
		return "PayWay [name=" + name + ", discount=" + discount + ", order=" + order + ", payDetails=" + payDetails
				+ ", imgId=" + imgId + ", payName=" + payName + ", discountDetails=" + discountDetails + "]";
	}
	
}
