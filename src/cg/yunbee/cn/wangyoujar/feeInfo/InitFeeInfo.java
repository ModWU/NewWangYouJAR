package cg.yunbee.cn.wangyoujar.feeInfo;
import java.util.Map;

import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkConfig.SDKConfig;
public class InitFeeInfo {
	private String id = "";
	private boolean isOAuthState;
	private Map<String, SDKConfig> listSdk;
	
	//private List<String> listSdkName;
	
	private static volatile InitFeeInfo instance;
	
	private InitFeeInfo() {}
	
	public static InitFeeInfo getInstance() {
		if(instance == null) {
			synchronized (InitFeeInfo.class) {
				if(instance == null)
					instance = new InitFeeInfo();
				
			}
		}
		
		return instance;
	}
	
	
	
	public boolean isOAuthState() {
		return isOAuthState;
	}

	public void setOnAuthState(boolean isOAuthState) {
		this.isOAuthState = isOAuthState;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, SDKConfig> getListSdk() {
		return listSdk;
	}
	public void setListSdk(Map<String, SDKConfig> listSdk) {
		TAGS.log("-------------------setListSdk-------------------");
		this.listSdk = listSdk;
	}


	
	
}
