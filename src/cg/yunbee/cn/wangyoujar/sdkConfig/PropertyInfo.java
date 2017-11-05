package cg.yunbee.cn.wangyoujar.sdkConfig;

public abstract class PropertyInfo {
	public String PropertyId;
	
	public static AibeiPropertyInfo getAibeiPropertyInfo() {
		return new AibeiPropertyInfo();
	}
	
	public static AlipayPropertyInfo getAlipayPropertyInfo() {
		return new AlipayPropertyInfo();
	}
	
	
	public static WeixinPropertyInfo getWeixinPropertyInfo() {
		return new WeixinPropertyInfo();
	}
	
	public static YhxfPropertyInfo getYhxfPropertyInfo() {
		return new YhxfPropertyInfo();
	}

	public static YstPropertyInfo getYstPropertyInfo() {
		return new YstPropertyInfo();
	}
	
	public static WftPropertyInfo getWftPropertyInfo() {
		return new WftPropertyInfo();
	}
	
	
	public final static class AibeiPropertyInfo extends PropertyInfo {
		public String aibeiPropertyId;
		private AibeiPropertyInfo() {}
	}
	
	public final static class AlipayPropertyInfo extends PropertyInfo {
		private AlipayPropertyInfo() {}
	}
	
	public final static class WeixinPropertyInfo extends PropertyInfo {
		private WeixinPropertyInfo() {}
	}
	
	public final static class YhxfPropertyInfo extends PropertyInfo {
		private YhxfPropertyInfo() {}
	}
	
	public final static class YstPropertyInfo extends PropertyInfo {
		private YstPropertyInfo() {}
	}
	
	public final static class WftPropertyInfo extends PropertyInfo {
		private WftPropertyInfo() {}
	}
}
