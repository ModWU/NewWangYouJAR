package cg.yunbee.cn.wangyoujar.sdkConfig;

public class AIBEIConfig extends SDKConfig {
	private String appId = "";
	private String privateKey = "";
	private String publicKey = "";
	private String orientation = "";
	

	public AIBEIConfig() {
	}
	
	

	public String getPublicKey() {
		return publicKey;
	}



	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}



	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	
	
	


}
