package cg.yunbee.cn.wangyou.loginSys;

import cg.yunbee.cn.wangyoujar.keycode.DecodeKey;

public class UserToken {
	
	private String loginName = "";
	private String userId = "";
	private String userToken = "";
	private boolean isBindUidAndDianId;
	
	//加密解密的密钥
	private static final String DEFAULT_KEY = "default_dy123";
		
	//额外信息
	private String key_code = DEFAULT_KEY;
	
	
	public UserToken(String loginName, String userId, String userToken) {
		if(loginName == null) loginName = "";
		this.loginName = loginName;
		this.userId = userId;
		this.userToken = userToken;
	}
	
	public UserToken(String loginName, String userId, String userToken, boolean isBindUidAndDianId) {
		this(loginName, userId, userToken);
		this.isBindUidAndDianId = isBindUidAndDianId;
	}
	
	public UserToken copy() {
		return new UserToken(loginName, userId, userToken, isBindUidAndDianId); 
	}
	
	public void setKeyCode(String keyCode) {
		if(keyCode != null) {
			this.key_code = keyCode;
		}
	}
	
	public void encodeKey() {
		try {
			loginName = DecodeKey.encrypt(loginName, key_code);
			userId = DecodeKey.encrypt(userId, key_code);
			userToken = DecodeKey.encrypt(userToken, key_code);
		} catch (Exception e) {
		}
	}
	
	public void decodeKey() {
		try {
			loginName = DecodeKey.decrypt(loginName, key_code);
			userId = DecodeKey.decrypt(userId, key_code);
			userToken = DecodeKey.decrypt(userToken, key_code);
		} catch (Exception e) {
		}
	}
	
	
	public boolean isBindUidAndDianId() {
		return isBindUidAndDianId;
	}

	public void setBindUidAndDianId(boolean isBindUidAndDianId) {
		this.isBindUidAndDianId = isBindUidAndDianId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		if(loginName == null) loginName = "";
		this.loginName = loginName;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		if(userId == null) userId = "";
		this.userId = userId;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		if(userToken == null) userToken = "";
		this.userToken = userToken;
	}
	
	@Override
	public String toString() {
		return "{\"" + LoginRecords.FLAG_LOGIN_NAME + "\":\"" + loginName + "\", \"" 
				     +  LoginRecords.FLAG_USER_ID + "\":\"" + userId + "\", \""
				     + LoginRecords.FLAG_USER_TOKEN + "\":\"" + userToken +", \""
				     + LoginRecords.FLAG_BIND_UID_DIANID + "\":" + isBindUidAndDianId +"}" ;
	}
}
