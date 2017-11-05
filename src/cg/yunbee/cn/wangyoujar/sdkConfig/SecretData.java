package cg.yunbee.cn.wangyoujar.sdkConfig;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class SecretData {
	
	private String appName;
	//private String uid;
	private String packageName;// xml
	private String versionCode;// xml
	private String versionName;// xml
	private String jarVersion;// init()中传递过来
	private String plugVersion;
	private final String urlVersion = "1";// 代码写死
	public static final String appConfigFileName = "DYConfig.data";
	
	//json
	private String appId;
	private String packageId;
	private String channelId;
	private String subChannelId;
	private String orientation;
	private String gameName;
	private String secretKey;
	private String debug;
	
	public static final String TYPE_ALIPAY = "alipay";
	public static final String TYPE_AIBEI = "aibei";
	public static final String TYPE_WEIXIN = "weixin";
	public static final String TYPE_YHXF = "yhxf";
	public static final String TYPE_YST = "yst";
	public static final String TYPE_WFT = "wft";
	public static final String TYPE_QRCODE = "QRCodepay";
	public static final String TYPE_DYH5 = "dyh5";
	
	private Map<String, SDKConfig> mSdkConfig = new HashMap<String, SDKConfig>();
	
	private static final SecretData INSTANCE = new SecretData();
	
	private SecretData() {}
	
	
	public static SecretData getInstance() {
		return INSTANCE;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void addSDKConfig(String type, SDKConfig sdkConfig) {
		mSdkConfig.put(type, sdkConfig);
	}
	
	public SDKConfig getSDKConfig(String type) {
		return mSdkConfig.get(type);
	}
	
	public boolean isHasSDK(String type) {
		return mSdkConfig.containsKey(type);
	}
	
	/*public boolean isSDKEmpty() {
		return mSdkConfig.size() <= 0;
	}*/
	
	public void clear() {
		mSdkConfig.clear();
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getSubChannelId() {
		return subChannelId;
	}

	public void setSubChannelId(String subChannelId) {
		this.subChannelId = subChannelId;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public Map<String, SDKConfig> getSdkConfig() {
		return mSdkConfig;
	}

	public void setSdkConfig(Map<String, SDKConfig> mSdkConfig) {
		this.mSdkConfig = mSdkConfig;
	}

	public static String getTypeAlipay() {
		return TYPE_ALIPAY;
	}

	public static String getTypeAibei() {
		return TYPE_AIBEI;
	}

	public static String getTypeWeixin() {
		return TYPE_WEIXIN;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getJarVersion() {
		return jarVersion;
	}

	public void setJarVersion(String jarVersion) {
		this.jarVersion = jarVersion;
	}

	public String getPlugVersion() {
		return plugVersion;
	}

	public void setPlugVersion(String plugVersion) {
		this.plugVersion = plugVersion;
	}

	public String getUrlVersion() {
		return urlVersion;
	}

	public String getAppConfigFileName() {
		return appConfigFileName;
	}
	
	
	
}
