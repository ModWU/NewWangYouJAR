package cg.yunbee.cn.wangyou.loginSys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.SharedPreferencesUtils;

public class LoginRecords {
	
	public static final String USER_INFO_SPFNAME = "dy_usrRecords";
	
	public static final String USER_INFO_STR = "dy_user_info";
	public static final String USER_TOKEN_STR = "dy_user_token";
	public static final String USER_INFO_NAME = "usrRecords";
	
	public static final String FLAG_USER_ID = "userid";
	public static final String FLAG_USER_TOKEN = "usertoken";
	
	public static final String FLAG_BIND_UID_DIANID = "bind_uid_dianid";
	
	public static final String FLAG_NORMAL_USER = "isNormalUser";
	public static final String FLAG_LOGIN_NAME = "loginname";
	public static final String FLAG_USER_NAME = "username";
	public static final String FLAG_PASSWORD = "password";
	public static final String FLAG_TIME = "lasttime";
	public static final String FLAG_REMEMBER_PASSWORD = "rememberPwd";
	public static final String FLAG_AUTO_LOGIN = "autoLogin";
	public static final String FLAG_PHONE = "phoneNumber";
	public static final String FLAG_EMAIL = "email";
	
	private SharedPreferencesUtils spUtils;
	
	private static LoginRecords INSTANCE;
	
	private LoginRecords(Context context) {
		spUtils = SharedPreferencesUtils.newInstance(context, USER_INFO_SPFNAME);
	}
	
	public static LoginRecords getInstance(Context context) {
		if(INSTANCE == null)
			synchronized (LoginRecords.class) {
				if(INSTANCE == null)
					INSTANCE = new LoginRecords(context);
			}
		return INSTANCE;
	}
	
	public void saveUserToken(UserToken userToken) {
		if(userToken != null) {
			userToken.encodeKey();
			StringBuffer buffer = new StringBuffer();
			buffer.append("{");
			buffer.append("\"" + FLAG_LOGIN_NAME + "\":\"" + userToken.getLoginName() + "\",");
			buffer.append("\"" + FLAG_USER_ID + "\":\"" + userToken.getUserId() + "\",");
			buffer.append("\"" + FLAG_USER_TOKEN + "\":\"" + userToken.getUserToken() + "\",");
			buffer.append("\"" + FLAG_BIND_UID_DIANID + "\":" + userToken.isBindUidAndDianId());
			buffer.append("}");
			spUtils.saveString(USER_TOKEN_STR, buffer.toString()).commit();
		}
	}
	
	public void clearUserToken() {
		spUtils.saveString(USER_TOKEN_STR, "").commit();
	}
	
	public UserToken getUserToken() {
		UserToken userToken = null;
		String usrToken = spUtils.getString(USER_TOKEN_STR, "");
		try {
			if(usrToken != null && !"".equals(usrToken)) {
				JSONObject tokenObj = new JSONObject(usrToken);
				String username = tokenObj.optString(FLAG_LOGIN_NAME, "");
				String userId = tokenObj.optString(FLAG_USER_ID, "");
				String token = tokenObj.optString(FLAG_USER_TOKEN, "");
				boolean isBindUidDianId = tokenObj.optBoolean(FLAG_BIND_UID_DIANID, false);
				userToken = new UserToken(username, userId, token);
				userToken.setBindUidAndDianId(isBindUidDianId);
				userToken.decodeKey();
			}
		} catch (JSONException e) {
			TAGS.log("LoginRecords-->getUserToken:" + e.toString());
		}
		return userToken;
	}
	
	
	public ArrayList<UserInfo> getAllListUsers() {
		
		ArrayList<UserInfo> usrList = new ArrayList<UserInfo>();
		String usrStr = spUtils.getString(USER_INFO_STR, "");
		try {
			if(usrStr != null && !"".equals(usrStr)) {
				JSONObject usrObj = new JSONObject(usrStr);
				JSONArray allUsrArr = usrObj.getJSONArray(USER_INFO_NAME);
				int count = allUsrArr.length();
				for (int i = 0; i < count; i++) {
					JSONObject perObj = allUsrArr.getJSONObject(i);
					String loginname = perObj.optString(FLAG_LOGIN_NAME);
					String username = perObj.optString(FLAG_USER_NAME);
					String dianId = perObj.optString(FLAG_USER_ID);
					String password = perObj.optString(FLAG_PASSWORD);
					String phoneNumber = perObj.optString(FLAG_PHONE);
					String email = perObj.optString(FLAG_EMAIL);
					long lasttime = perObj.optLong(FLAG_TIME);
					boolean isRemeberPwd = perObj.optBoolean(FLAG_REMEMBER_PASSWORD, false);
					boolean isAutoLogin = perObj.optBoolean(FLAG_AUTO_LOGIN, false);
					boolean isNormalUser = perObj.optBoolean(FLAG_NORMAL_USER);
					UserInfo userInfo = new UserInfo(username, password, phoneNumber, email, lasttime, isRemeberPwd, isAutoLogin);
					userInfo.setLoginName(loginname);
					userInfo.setNormalUser(isNormalUser);
					userInfo.setDianId(dianId);
					userInfo.decodeKey();
					usrList.add(userInfo);
				}
			}
		} catch (JSONException e) {
			TAGS.log("LoginRecords-->getAllUsers:" + e.toString());
		}
		
		return usrList;
	}
	
	public HashMap<String, UserInfo> getAllMapUsers() {
		
		HashMap<String, UserInfo> usrMap = new HashMap<String, UserInfo>();
		String usrStr = spUtils.getString(USER_INFO_STR, "");
		try {
			if(usrStr != null && !"".equals(usrStr)) {
				JSONObject usrObj = new JSONObject(usrStr);
				JSONArray allUsrArr = usrObj.getJSONArray(USER_INFO_NAME);
				int count = allUsrArr.length();
				for (int i = 0; i < count; i++) {
					JSONObject perObj = allUsrArr.getJSONObject(i);
					String loginname = perObj.optString(FLAG_LOGIN_NAME);
					String username = perObj.optString(FLAG_USER_NAME);
					String dianId = perObj.optString(FLAG_USER_ID);
					String password = perObj.optString(FLAG_PASSWORD);
					String phoneNumber = perObj.optString(FLAG_PHONE);
					String email = perObj.optString(FLAG_EMAIL);
					long lasttime = perObj.optLong(FLAG_TIME);
					boolean isRememberPwd = perObj.optBoolean(FLAG_REMEMBER_PASSWORD, false);
					boolean isAutoLogin = perObj.optBoolean(FLAG_AUTO_LOGIN, false);
					boolean isNormalUser = perObj.optBoolean(FLAG_NORMAL_USER);
					UserInfo userInfo = new UserInfo(username, password, phoneNumber, email, lasttime, isRememberPwd, isAutoLogin);
					userInfo.setLoginName(loginname);
					userInfo.setNormalUser(isNormalUser);
					userInfo.setDianId(dianId);
					userInfo.decodeKey();
					usrMap.put(userInfo.getLoginName(), userInfo);
				}
			}
		} catch (JSONException e) {
			TAGS.log("LoginRecords-->getAllMapUsers:" + e.toString());
		}
		
		return usrMap;
	}

	public void updateInfo(Collection<UserInfo> usrList) {
		if(usrList == null) return;
		
		if(usrList.size() <= 0) {
			spUtils.saveString(USER_INFO_STR, "").commit();
			return;
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\"" + USER_INFO_NAME + "\":[");
		Iterator<UserInfo> iterator = usrList.iterator();
		while(iterator.hasNext()) {
			UserInfo userInfo = iterator.next();
			userInfo.encodeKey();
			buffer.append(userInfo.toString() + ",");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append("]}");
		
		spUtils.saveString(USER_INFO_STR, buffer.toString()).commit();
	}
	
	public void clearAllUserInfo() {
		spUtils.saveString(USER_INFO_STR, "").commit();
	}
	
	public void addUserInfo(UserInfo userInfo) {
		if(userInfo == null) return;
		UserInfo temp = userInfo.copy();
		Map<String, UserInfo> allUser = getAllMapUsers();
		if(allUser != null) {
			allUser.put(temp.getLoginName(), temp);
			updateInfo(allUser.values());
		}
	}
	
	public void updateUserInfo(String oldLoginName, UserInfo userInfo) {
		if(userInfo == null || Utils.isEmpty(oldLoginName)) return;
		UserInfo tmp = userInfo.copy();
		Map<String, UserInfo> allUser = getAllMapUsers();
		if(allUser != null && allUser.containsKey(oldLoginName)) {
			allUser.remove(oldLoginName);
			allUser.put(tmp.getLoginName(), tmp);
			updateInfo(allUser.values());
		}
		
	}
	
	public UserInfo findUserInfo(String account) {
		if(Utils.isEmpty(account)) return null;
		List<UserInfo> allUsers = getAllListUsers();
		for(UserInfo userInfo : allUsers) {
			if(account.equals(userInfo.getUserName()) || account.equals(userInfo.getPhoneNumber()) || account.equals(userInfo.getEmail())) {
				return userInfo;
			}
		}
		return null;
	}
	
	public UserInfo findUserForNumberOrEmail(String account) {
		if(Utils.isEmpty(account)) return null;
		List<UserInfo> allUsers = getAllListUsers();
		for(UserInfo userInfo : allUsers) {
			if(account.equals(userInfo.getPhoneNumber()) || account.equals(userInfo.getEmail())) {
				return userInfo;
			}
		}
		return null;
	}
	
	public void clearPwd(String account) {
		TAGS.log("------------alterPwd----------");
		UserInfo userInfo = findUserForNumberOrEmail(account);
		if(userInfo != null) {
			userInfo.setPassword("");
			userInfo.setRememberPwd(false);
			userInfo.setAutoLogin(false);
			updateUserInfo(userInfo.getLoginName(), userInfo);
		}
		
	}
	
	public UserInfo deleteUserInfo(String loginName) {
		TAGS.log("------------deleteUserInfo-----------");
		if(Utils.isEmpty(loginName)) return null;
		Map<String, UserInfo> allMapUsr = getAllMapUsers();
		
		if(allMapUsr != null) {
			UserInfo userInfo = allMapUsr.remove(loginName);
			updateInfo(allMapUsr.values());
			return userInfo;
		}
		
		return null;
	}
	
	public ArrayList<UserName> convertToLoginNameList(List<UserInfo> list) {
		
		ArrayList<UserName> userNames = new ArrayList<UserName>();
		if(list != null)
			for(UserInfo userInfo : list)
				userNames.add(new UserName(userInfo.getLoginName()));
		return userNames;
	}
	
	public Map<String, UserInfo> convertToUserInfoMap(List<UserInfo> list) {
		Map<String, UserInfo> map = new HashMap<String, UserInfo>();
		if(list != null)
			for(UserInfo userInfo : list)
				map.put(userInfo.getLoginName(), userInfo);
		return map;
	}
	
	
	
	public void clear() {
		INSTANCE = null;
	}
	
}
