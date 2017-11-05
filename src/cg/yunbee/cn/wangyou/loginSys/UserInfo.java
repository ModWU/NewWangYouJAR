package cg.yunbee.cn.wangyou.loginSys;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import cg.yunbee.cn.wangyoujar.keycode.DecodeKey;

public class UserInfo implements Comparable<UserInfo>, Parcelable {
	
	public static final String VALID_OK = "vaild_ok";
	
	//加密解密的密钥
	private static final String DEFAULT_KEY = "default_dy123";
	
	//额外信息
	private String key_code = DEFAULT_KEY;
	private String dianId = "";
	private String token = "";
	
	//实体信息
	private boolean isNormalUser = true;
	private String loginName = "";
	private String userName = "";
	private String password = "";
	private String phoneNumber = "";
	private String email = "";
	private boolean isRememberPwd;
	private boolean isAutoLogin;
	private long lastLoginTimeMillis;
	
	public UserInfo(String userName, String password, String phoneNumber, String email, long lastLoginTimeMillis, boolean isRememberPwd, boolean isAutoLogin) {
		this.userName = userName;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.lastLoginTimeMillis = lastLoginTimeMillis;
		this.isRememberPwd = isRememberPwd;
		this.isAutoLogin = isAutoLogin;
	}
	
	
	

	public boolean isNormalUser() {
		return isNormalUser;
	}




	public void setNormalUser(boolean isNormalUser) {
		this.isNormalUser = isNormalUser;
	}




	public String getLoginName() {
		return loginName;
	}



	public void setLoginName(String loginName) {
		if(loginName == null) loginName = "";
		this.loginName = loginName;
	}



	public String toAllString() {
		return "key_code:" + key_code + ", dianId:" + dianId + ", token:" + token + ", userName:" + userName + ", loginName:" + loginName + ", password:" + password + ", phoneNumber:" + phoneNumber +
				", email:" + email + ", isRememberPwd:" + isRememberPwd + ", isAutoLogin:" + isAutoLogin + ", lastLoginTimeMillis:" + lastLoginTimeMillis + ", isNormalUser:" + isNormalUser;
	}
	
	public void setKeyCode(String keyCode) {
		if(keyCode != null) {
			this.key_code = keyCode;
		}
	}
	
	
	public void encodeKey() {
		try {
			loginName = DecodeKey.encrypt(loginName, key_code);
			dianId = DecodeKey.encrypt(dianId, key_code);
			userName = DecodeKey.encrypt(userName, key_code);
			password = DecodeKey.encrypt(password, key_code);
			phoneNumber = DecodeKey.encrypt(phoneNumber, key_code);
			email = DecodeKey.encrypt(email, key_code);
		} catch (Exception e) {
		}
	}
	
	public void decodeKey() {
		try {
			loginName = DecodeKey.decrypt(loginName, key_code);
			dianId = DecodeKey.decrypt(dianId, key_code);
			userName = DecodeKey.decrypt(userName, key_code);
			password = DecodeKey.decrypt(password, key_code);
			phoneNumber = DecodeKey.decrypt(phoneNumber, key_code);
			email = DecodeKey.decrypt(email, key_code);
		} catch (Exception e) {
		}
	}
	
	public UserInfo copy() {
		UserInfo userInfo = new UserInfo(userName, password, phoneNumber, email, lastLoginTimeMillis, isRememberPwd, isAutoLogin);
		userInfo.setLoginName(loginName);
		userInfo.setNormalUser(isNormalUser);
		userInfo.setDianId(dianId);
		userInfo.setToken(token);
		userInfo.setKeyCode(key_code);
		return userInfo;
	}
	
	
	public boolean isValid(Context context) {
		return  VALID_OK.equals(validForPassword(context)) &&
				(VALID_OK.equals(validForUserName(context)) || VALID_OK.equals(validForPhoneNumber(context)) || VALID_OK.equals(validForEmail(context)));
		
	}
	
	public static String validForPhoneNumber(String phoneNumber, Context context) {
		if(phoneNumber == null || "".equals(phoneNumber.trim()))
			return Utils.getResourceStr(context, "dy_rmreg_phone_empty");
		else if(!Utils.isMobile(phoneNumber))
			return Utils.getResourceStr(context, "dy_rmreg_phone_incorrect");
		
		return VALID_OK;
	}
	
	public String validForPhoneNumber(Context context) {
		return validForPhoneNumber(phoneNumber, context);
	}
	
	
	public static String validForUserName(String userName, Context context) {
		if(userName == null || "".equals(userName))
			return Utils.getResourceStr(context, "dy_rmreg_usrname_empty");
		else if(!userName.matches("^[a-zA-Z].*"))
			return Utils.getResourceStr(context, "dy_rmreg_usrname_firstc");
		else if(!userName.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$")) 
			return Utils.getResourceStr(context, "dy_rmreg_usrname_alphnum");
		else if(userName.length() < 5 || userName.length() > 50)
			return Utils.getResourceStr(context, "dy_rmreg_usrname_length");
		return VALID_OK;
	}
	
	public String validForUserName(Context context) {
		return validForUserName(userName, context);
	}
	
	public String validForPassword(Context context) {
		if(password == null || "".equals(password.trim()))
			return Utils.getResourceStr(context, "dy_rmreg_pwd_empty");
		else if(password.matches("^\\d+$"))
			return Utils.getResourceStr(context, "dy_rmreg_pwd_digist");
		else if(password.matches("^[a-zA-Z]+$"))
			return Utils.getResourceStr(context, "dy_rmreg_pwd_alpha");
		else if(password.length() < 8 || password.length() > 17) 
			return Utils.getResourceStr(context, "dy_rmrey_pwd_length");
		return VALID_OK;
	}
	
	public static String validForEmail(String email, Context context) {
		if(email == null || "".equals(email.trim()))
			return Utils.getResourceStr(context, "dy_rmreg_email_empty");
		else if(email.length() < 5 || email.length() > 50) 
			return Utils.getResourceStr(context, "dy_rmreg_email_length");
		else if(!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) 
			return Utils.getResourceStr(context, "dy_rmreg_email_incorrect");
		return VALID_OK;
	}
	
	public String validForEmail(Context context) {
		return validForEmail(email, context);
	}
	
	@Override
	public int hashCode() {
		String str = userName + phoneNumber + email;
		return str.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof UserInfo) {
			UserInfo other = (UserInfo) o;
			String otherstr = other.userName + other.phoneNumber + other.email;
			String str = userName + phoneNumber + email;
			return otherstr.equals(str);
		}
		
		return false;
	}
	
	public UserInfo() {}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		if(userName == null) userName = "";
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		if(password == null) password = "";
		this.password = password;
	}
	public long getLastLoginTime() {
		return lastLoginTimeMillis;
	}
	public void setLastLoginTime(long lastLoginTimeMillis) {
		this.lastLoginTimeMillis = lastLoginTimeMillis;
	}
	
	public boolean isHasPwd() {
		return password != null && !"".equals(password);
	}
	
	
	public boolean isRememberPwd() {
		return isRememberPwd;
	}
	public void setRememberPwd(boolean isRememberPwd) {
		this.isRememberPwd = isRememberPwd;
	}
	
	public boolean isAutoLogin() {
		return isAutoLogin;
	}
	public void setAutoLogin(boolean isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		if(phoneNumber == null) phoneNumber = "";
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if(email == null) email = "";
		this.email = email;
	}
	

	public String getDianId() {
		return dianId;
	}

	public void setDianId(String dianId) {
		if(dianId == null) dianId = "";
		this.dianId = dianId;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		if(token == null) token = "";
		this.token = token;
	}

	@Override
	public String toString() {
		return "{\""  + LoginRecords.FLAG_NORMAL_USER + "\":\"" + isNormalUser + "\", \"" 
					  + LoginRecords.FLAG_LOGIN_NAME + "\":\"" + loginName + "\", \"" 
					 + LoginRecords.FLAG_USER_NAME + "\":\"" + userName + "\", \"" 
				     +  LoginRecords.FLAG_PASSWORD + "\":\"" + password + "\", \""
				     +  LoginRecords.FLAG_PHONE + "\":\"" + phoneNumber + "\", \""
				     +  LoginRecords.FLAG_EMAIL + "\":\"" + email + "\", \""
				     +  LoginRecords.FLAG_USER_ID + "\":\"" + dianId + "\", \""
				     + LoginRecords.FLAG_TIME + "\":" + lastLoginTimeMillis + ",\""
				     + LoginRecords.FLAG_AUTO_LOGIN + "\":" + isAutoLogin + ",\""
				     + LoginRecords.FLAG_REMEMBER_PASSWORD + "\":" + isRememberPwd +"}" ;
	}
	@Override
	public int compareTo(UserInfo another) {
		long cValue = getLastLoginTime() - another.getLastLoginTime();
		if(cValue == 0) return 0;
		return cValue > 0 ? -1 : 1;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {

		@Override
		public UserInfo createFromParcel(Parcel source) {
			return new UserInfo(source);
		}

		@Override
		public UserInfo[] newArray(int size) {
			return new UserInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBooleanArray(new boolean[]{isNormalUser});
		dest.writeString(key_code);
		dest.writeString(dianId);
		dest.writeString(token);
		dest.writeString(loginName);
		dest.writeString(userName);
		dest.writeString(password);
		dest.writeString(phoneNumber);
		dest.writeString(email);
		dest.writeBooleanArray(new boolean[]{isRememberPwd, isAutoLogin});
		dest.writeLong(lastLoginTimeMillis);
	}
	
	private UserInfo(Parcel in) {
		boolean bn2[] = new boolean[1];
		in.readBooleanArray(bn2);
		isNormalUser = bn2[0];
		key_code = in.readString();
		dianId = in.readString();
		token = in.readString();
		loginName = in.readString();
		userName = in.readString();
		password = in.readString();
		phoneNumber = in.readString();
		email = in.readString();
		boolean bn3[] = new boolean[2];
		in.readBooleanArray(bn3);
		isRememberPwd = bn3[0];
		isAutoLogin = bn3[1];
		lastLoginTimeMillis = in.readLong();
	}
	
}
