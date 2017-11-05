package cg.yunbee.cn.wangyou.loginSys;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import cg.yunbee.cn.wangyoujar.feeInfo.InitFeeInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import cg.yunbee.cn.wangyoujar.work.ServerCommunicationManager;
import dy.compatibility.work.Contant;

public class UserAuthorize {
	
	public interface IAuthorizeListener {
		void success(UserInfo userInfo, int code, String info);
		void fail(UserInfo userInfo, int code, String errorInfo);
	}
	
	public static final int CODE_SUCCESS = 200;
	public static final int CODE_NET_FAIL = 1000;
	public static final int CODE_JSON_FAIL = 1001;
	public static final int CODE_ALTER_RETURN_FAIL = 1002;
	public static final int CODE_REGISTER_NORMAL_RETURN_FAIL = 1003;
	public static final int CODE_REGISTER_FEE_RETURN_FAIL = 1004;
	public static final int CODE_REGISTER_BIND_RETURN_FAIL = 1005;
	public static final int CODE_LOGIN_FEE_RETURN_FAIL = 1006;
	public static final int CODE_LOGIN_NORMAL_RETURN_FAIL = 1007;
	public static final int CODE_LOGOUT_ERROR = 1008;
	public static final int CODE_LOGOUT_NOTVALID = 1009;
	public static final int CODE_LOGOUT_RETURN_FAIL = 1010;
	public static final int CODE_LOGOUT_RESET_LOGIN = 1011;//需要重新登录
	public static final int CODE_LOGOUT_NEED_LOGIN = 1012;//需要重新登录
	public static final int CODE_LOGIN_BIND_FAIL = 1013;
	public static final int CODE_BIND_VERTIFY_ID = 1014;
	public static final int CODE_BIND_RETURN_FAIL = 1015;
	
	public static final int CODE_LOGIN_ALWAY_KEEP = 1016;//已经登录过，本次登录只回调接口
	
	public static final int CODE_GET_VERCODE_FAIL = 1017;
	
	public static final int CODE_CONFIRM_VERCODE_FAIL = 1018;
	
	
	private UserInfo userInfo;
	
	private static final String REGISTER_NORMAL_URL = Contant.LOGIN_HOST + "/api/OAuth/UserRegister";
	private static final String REGISTER_TOURIST_URL = Contant.LOGIN_HOST + "/api/OAuth/TouristsRegister";
	private static final String REGISTER_BIND_URL = Contant.LOGIN_HOST + "/api/OAuth/TouristsBindUser";
	
	private static final String LOGIN_NORMAL_URL = Contant.LOGIN_HOST + "/api/OAuth/UserLogin";
	private static final String LOGIN_TOURIST_URL = Contant.LOGIN_HOST + "/api/OAuth/TouristsLogin";
	
	private static final String ALTER_PASSWORD_URL = Contant.LOGIN_HOST + "/api/OAuth/ModifyPassword";
	private static final String LOGOUT_URL = Contant.LOGIN_HOST + "/api/OAuth/SignOut";
	
	private static final String SEND_SMS = Contant.HOST + "/SendSMS/Sendsms.ashx";
	private static final String POST_SMS = Contant.HOST + "/SendSMS/Postsms.ashx";
	
	private Activity mActivity;
	//addParams("Phone", lastPhoneNumber).addParams("Id", userId)
	public static UserAuthorize build(Activity activity, UserInfo userInfo) {
		if(userInfo == null) {
			userInfo = new UserInfo();
			userInfo.setUserName("");
		}
		return new UserAuthorize(activity, userInfo);
	}
	
	private UserAuthorize(Activity activity, UserInfo userInfo) {
		this.userInfo = userInfo;
		mActivity = activity;
	}
	
	public static void getVertifyCodeByPhone(Context context, IAuthorizeListener listener, String phoneNumber) {
		final Context finalContext = context;
		final IAuthorizeListener finalListener = listener;
		final String finalPhoneNumber = phoneNumber;
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				UserInfo userInfo = LoginRecords.getInstance(finalContext).findUserForNumberOrEmail(finalPhoneNumber);
				if(userInfo == null) {
					callListener(finalListener, userInfo, false, CODE_GET_VERCODE_FAIL, "该用户本地不存在");
					return;
				}
				params.put("Phone", finalPhoneNumber);
				params.put("Id", userInfo.getDianId());
				
				String result = HttpUtils.sendPostUTF8(SEND_SMS, params);
				
				try {
					JSONObject dataObj = new JSONObject(result);
					
					int code = dataObj.optInt("Code", -1);
	        		if(code == 0) {
	        			callListener(finalListener, userInfo, true, CODE_SUCCESS, "验证码已成功发送到手机: "+ finalPhoneNumber);
	        		} else {
	        			callListener(finalListener, userInfo, false, CODE_GET_VERCODE_FAIL, "result: " + result);
	        		}
				} catch (JSONException e) {
					callListener(finalListener, userInfo, false, CODE_GET_VERCODE_FAIL, "JSONException: " + e.toString());
				}
        		
				
			}
			
		}).start();
		
	}
	
	public static void confirmVertifyCode(Context context, IAuthorizeListener listener, String phoneNumber, String verCode) {
		final Context finalContext = context;
		final IAuthorizeListener finalListener = listener;
		final String finalPhoneNumber = phoneNumber;
		final String finalVercode = verCode;
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				UserInfo userInfo = LoginRecords.getInstance(finalContext).findUserForNumberOrEmail(finalPhoneNumber);
				if(userInfo == null) {
					callListener(finalListener, userInfo, false, CODE_CONFIRM_VERCODE_FAIL, "该用户本地不存在");
					return;
				}
				Map<String, String> params = new HashMap<String, String>();
				params.put("Phone", finalPhoneNumber);
				params.put("Id", userInfo.getDianId());
				params.put("VerificationCode", finalVercode);
				String result = HttpUtils.sendPostUTF8(POST_SMS, params);
				try {
                    JSONObject jsonObj = new JSONObject(result);
                    int code = jsonObj.optInt("Code", -1);
                    if(code == 0) {
                    	callListener(finalListener, null, true, CODE_SUCCESS, "验证码已成功发送到手机: "+ finalPhoneNumber);
                        return;
                    }
                	callListener(finalListener, null, true, CODE_CONFIRM_VERCODE_FAIL, "result: "+ result);
                    return;
                } catch(Exception e) {
                	callListener(finalListener, null, true, CODE_CONFIRM_VERCODE_FAIL, "Exception: "+ e.toString());
                }
			}
			
		}).start();
	}
	
	void registerByNormal(IAuthorizeListener listener) {
		
		final IAuthorizeListener finalListener = listener;
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				String userName = userInfo.getUserName();
				String phoneNumber = userInfo.getPhoneNumber();
				String email = userInfo.getEmail();
				String password = userInfo.getPassword();
				
				if(phoneNumber == null) phoneNumber = "";
				if(email == null) email = "";
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("LoginName", userName);
				params.put("Phone", phoneNumber);
				params.put("Email", email);
				params.put("PassWord", password);
				
				String result = HttpUtils.sendPostUTF8(REGISTER_NORMAL_URL, params);
				if(result == null) {
					callListener(finalListener, false, CODE_NET_FAIL, "请检查网络连接");
					return;
				}
				
				try {
					JSONObject resultObj = new JSONObject(result);
					int code = resultObj.optInt("code", -1);
					String msg = resultObj.optString("msg", "");
					if(code == 0) {
						String id = resultObj.optString("Id", "");
						userInfo.setDianId(id);
						userInfo.setLoginName(userInfo.getUserName());
						callListener(finalListener, true, CODE_SUCCESS, msg);
						return;
					}
					callListener(finalListener, false, CODE_REGISTER_NORMAL_RETURN_FAIL, msg);
				} catch (JSONException e) {
					callListener(finalListener, false, CODE_JSON_FAIL, e.toString());
					TAGS.log("registerByNormal-->ex:" + e.toString());
				}
				
			}
			
		}).start();
	}
	
	private static String randomLoginName() {
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		random.setSeed(System.nanoTime());
		int loginNameLen = random.nextInt(7) + 6;
		int alphaLen = random.nextInt(loginNameLen - 4) + (random.nextInt(4) + 1); 
		int numLen = loginNameLen - alphaLen;
		
		while(alphaLen-- > 0) {
			boolean isUpper = random.nextFloat() > 0.5;
			char perC = (char) (random.nextInt('z' - 'a' + 1) + 'a');
			if(isUpper) perC = (char) (('A' - 'a') + perC);
			buffer.append(perC);
		}
		
		while(numLen-- > 0) {
			int num = random.nextInt(10);
			buffer.append(num);
		}
		
		return buffer.toString();
		
	}
	
	private static String randomPassword() {
		return "abc12345";
	}
	
	
	static void registerByTourist(IAuthorizeListener listener) {
		Log.i("baba", "-----------registerByTourist------------");
		final IAuthorizeListener finalListener = listener;
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				final String userName = randomLoginName();
				final String password = randomPassword();
				Log.i("baba", "userName:" + userName);
				Log.i("baba", "password:" + password);
				params.put("LoginName", userName);
				params.put("PassWord", password);
				//String result = HttpUtils.sendPostDataUTF8(REGISTER_TOURIST_URL, "");
				String result = HttpUtils.sendPostUTF8(REGISTER_TOURIST_URL, params);
				if(result == null) {
					callListener(finalListener, null, false, CODE_NET_FAIL, "请检查网络连接");
					return;
				}
				
				try {
					JSONObject resultObj = new JSONObject(result);
					int code = resultObj.optInt("code", -1);
					String msg = resultObj.optString("msg", "");
					if(code == 0) {
						UserInfo userInfo = new UserInfo();
						String id = resultObj.optString("Id", "");
						userInfo.setDianId(id);
						userInfo.setNormalUser(false);
						userInfo.setLoginName(userName);
						userInfo.setUserName(userName);
						userInfo.setPassword(password);
						userInfo.setRememberPwd(true);
						callListener(finalListener, userInfo, true, CODE_SUCCESS, msg);
						return;
					}
					callListener(finalListener, null, false, CODE_REGISTER_FEE_RETURN_FAIL, msg);
				} catch (JSONException e) {
					callListener(finalListener, null, false, CODE_JSON_FAIL, e.toString());
					TAGS.log("registerByTourist-->ex:" + e.toString());
				}
			}
			
		}).start();
	}
	
	static void registerBind(IAuthorizeListener listener, UserInfo userInfo) {
		final IAuthorizeListener finalListener = listener;
		final UserInfo finalUsrInfo = userInfo;
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				String userName = finalUsrInfo.getUserName();
				String phoneNumber = finalUsrInfo.getPhoneNumber();
				String email = finalUsrInfo.getEmail();
				String password = finalUsrInfo.getPassword();
				String userId = finalUsrInfo.getDianId();
				
				if(phoneNumber == null) phoneNumber = "";
				if(email == null) email = "";
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("LoginName", userName);
				params.put("Phone", phoneNumber);
				params.put("PassWord", password);
				params.put("Email", email);
				params.put("Id", userId);
				
				String result = HttpUtils.sendPostUTF8(REGISTER_BIND_URL, params);
				if(result == null) {
					callListener(finalListener, finalUsrInfo, false, CODE_NET_FAIL, "请检查网络连接");
					return;
				}
				
				try {
					JSONObject resultObj = new JSONObject(result);
					int code = resultObj.optInt("code", -1);
					String msg = resultObj.optString("msg", "");
					if(code == 0) {
						callListener(finalListener, finalUsrInfo, true, CODE_SUCCESS, msg);
						return;
					}
					callListener(finalListener, finalUsrInfo, false, CODE_REGISTER_BIND_RETURN_FAIL, msg);
				} catch (JSONException e) {
					callListener(finalListener, finalUsrInfo, false, CODE_JSON_FAIL, e.toString());
					TAGS.log("registerBind-->ex:" + e.toString());
				}
			}
			
		}).start();
	}
	
	void loginByNormal(IAuthorizeListener listener) {
		final IAuthorizeListener finalListener = listener;
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				String loginName = userInfo.getLoginName();
				
				String password = userInfo.getPassword();
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("LoginName", loginName);
				params.put("PassWord", password);
				String result = HttpUtils.sendPostUTF8(LOGIN_NORMAL_URL, params);
				if(result == null) {
					callListener(finalListener, userInfo.copy(), false, CODE_NET_FAIL, "请检查网络连接");
					return;
				}
				
				try {
					JSONObject resultObj = new JSONObject(result);
					int code = resultObj.optInt("code", -1);
					String msg = resultObj.optString("msg", "");
					if(code == 0) {
						String token = resultObj.optString("token", "");
						String id = resultObj.optString("id", "");
						UserInfo usrtmp = userInfo.copy();
						usrtmp.setDianId(id);
						usrtmp.setToken(token);
						callListener(finalListener, usrtmp, true, CODE_SUCCESS, msg);
						return;
					} else if(code == -10) {
						//此时不需要监听，直接弹出登录窗体
						LoginRecords.getInstance(mActivity).clearUserToken();
						ServerCommunicationManager.popLoginWindow(mActivity);
						return;
					}
					callListener(finalListener, userInfo.copy(), false, CODE_LOGIN_NORMAL_RETURN_FAIL, msg);
				} catch (JSONException e) {
					callListener(finalListener, userInfo.copy(), false, CODE_JSON_FAIL, e.toString());
					TAGS.log("loginByNormal-->ex:" + e.toString());
				}
			}
			
		}).start();
	}
	
	static void loginByTourist(Activity activity, IAuthorizeListener listener, UserInfo userInfo) {
		final IAuthorizeListener finalListener = listener;
		final UserInfo finalUsrInfo = userInfo.copy();
		final Activity finalAct = activity;
		new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("LoginName", finalUsrInfo.getLoginName());
				params.put("PassWord", finalUsrInfo.getPassword());
				String result = HttpUtils.sendPostUTF8(LOGIN_TOURIST_URL, params);
				if(result == null) {
					callListener(finalListener, finalUsrInfo, false, CODE_NET_FAIL, "请检查网络连接");
					return;
				}
				
				try {
					JSONObject resultObj = new JSONObject(result);
					int code = resultObj.optInt("code", -1);
					String msg = resultObj.optString("msg", "");
					if(code == 0) {
						String token = resultObj.optString("token", "");
						String dianId = resultObj.optString("id", "");
						finalUsrInfo.setDianId(dianId);
						finalUsrInfo.setToken(token);
						callListener(finalListener, finalUsrInfo, true, CODE_SUCCESS, msg);
						return;
					} else if(code == -10) {
						//此时不需要监听，直接弹出登录窗体
						LoginRecords.getInstance(finalAct).clearUserToken();
						ServerCommunicationManager.popLoginWindow(finalAct);
						return;
					}
					callListener(finalListener, finalUsrInfo, false, CODE_LOGIN_FEE_RETURN_FAIL, msg);
				} catch (JSONException e) {
					callListener(finalListener, finalUsrInfo, false, CODE_JSON_FAIL, e.toString());
					TAGS.log("loginByTourist-->ex:" + e.toString());
				}
			}
		}).start();
	}
	
	void alterPassword(IAuthorizeListener listener, String newPwd) {
		final IAuthorizeListener finalListener = listener;
		final String finalNewPwd = newPwd;
		new Thread(new Runnable() {

			@Override
			public void run() {
				UserInfo tmp = userInfo.copy();
				String account = tmp.getLoginName();
				String oldPwd = tmp.getPassword();
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("LoginName", account);
				params.put("OldPassword", oldPwd);
				params.put("NewPassword", finalNewPwd);
				String result = HttpUtils.sendPostUTF8(ALTER_PASSWORD_URL, params);
				if(result == null) {
					callListener(finalListener, tmp, false, CODE_NET_FAIL, "请检查网络连接");
					return;
				}
				
				try {
					JSONObject resultObj = new JSONObject(result);
					int code = resultObj.optInt("code", -1);
					String msg = resultObj.optString("msg", "");
					if(code == 0) {
						tmp.setPassword(finalNewPwd);
						callListener(finalListener, tmp, true, CODE_SUCCESS, msg);
						return;
					}
					callListener(finalListener, tmp, false, CODE_ALTER_RETURN_FAIL, msg);
				} catch (JSONException e) {
					callListener(finalListener, tmp, false, CODE_JSON_FAIL, e.toString());
					TAGS.log("loginByTourist-->ex:" + e.toString());
				}
			}
		}).start();
	}
	
	static void logout(Activity activity, IAuthorizeListener listener, UserInfo userInfo) {
		final IAuthorizeListener finalListener = listener;
		final UserInfo finalUserInfo = userInfo;
		final Activity finalActivity = activity;
		new Thread(new Runnable() {

			@Override
			public void run() {
				final UserInfo tmp = finalUserInfo.copy();
				String id = tmp.getDianId();
				String token = tmp.getToken();
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("Id", id);
				params.put("Token", token);
				String result = HttpUtils.sendPostUTF8(LOGOUT_URL, params);
				if(result == null) {
					callListener(finalListener, tmp, false, CODE_NET_FAIL, "请检查网络连接");
					return;
				}
				
				try {
					JSONObject resultObj = new JSONObject(result);
					int code = resultObj.optInt("code", -1);
					final String msg = resultObj.optString("msg", "");
					if(code == 0) {
						callListener(finalListener, tmp, true, CODE_SUCCESS, msg);
						return;
					} else if(code == -10) {
						UserToken userToken = LoginRecords.getInstance(finalActivity).getUserToken();
						if(userToken != null) {
							callListener(finalListener, tmp, true, CODE_LOGOUT_RESET_LOGIN, msg);
						} else {
							callListener(finalListener, tmp, true, CODE_LOGOUT_NEED_LOGIN, msg);
						}
						return;
					}
					callListener(finalListener, tmp, false, CODE_LOGOUT_RETURN_FAIL, msg);
				} catch (JSONException e) {
					callListener(finalListener, tmp, false, CODE_JSON_FAIL, e.toString());
					TAGS.log("loginByTourist-->ex:" + e.toString());
				}
			}
		}).start();
	}
	
	
	static void bindUidAndDianId(Activity activity, UserAuthorize.IAuthorizeListener listener, UserInfo userInfo) {
		TAGS.log("--------------bindUidAndDianId--------------");
		final UserAuthorize.IAuthorizeListener finalListener = listener;
		final Activity finalactivity = activity;
		final UserInfo finaluserInfo = userInfo;
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = Contant.HOST + "/User/BindingId.ashx";
				UserToken userToken = LoginRecords.getInstance(finalactivity).getUserToken();
				boolean isHasToken = false;
				String dianId = "", uid = "", token = "";
				if(userToken != null) {
					dianId = userToken.getUserId();
					uid = InitFeeInfo.getInstance().getId();
					token = userToken.getUserToken();
					
					isHasToken = !Utils.isEmpty(dianId) && !Utils.isEmpty(uid) && !Utils.isEmpty(token);
					
					if(Utils.isEmpty(uid)) {
						uid = ServerCommunicationManager.getUidFromPhone(finalactivity);
						if(Utils.isEmpty(uid)) {
							TAGS.log("bindUidAndDianId-->获取uid为null,再次上传手机信息");
							uid = ServerCommunicationManager.initUploadMobilePhoneInfo(finalactivity);
						}
						
						if(uid == null)
							uid = "";
						InitFeeInfo.getInstance().setId(uid);
					}
					
					TAGS.log("bindUidAndDianId-->dianId:" + dianId + ",uid,:" + uid + ",token:" + token);
				}
				
				if(isHasToken) {
					Map<String, String> params = new HashMap<String, String>();
					params.put("id", uid);
					params.put("dianId", dianId);
					params.put("token", token);
					String result = HttpUtils.sendPostUTF8(url, params);
					if(result == null) {
						callListener(finalListener, finaluserInfo, false, CODE_NET_FAIL, "请检查网络连接");
						return;
					} else {
						try {
							JSONObject resultObj = new JSONObject(result);
							int code = resultObj.optInt("code", -1);
							String msg = resultObj.optString("message", "");
							if(code == 0) {
								//同时验证uid
								boolean isVerfifySuccess = ServerCommunicationManager.initVefifyId(finalactivity, true, InitFeeInfo.getInstance().getId());
								if(isVerfifySuccess) 
									callListener(finalListener, finaluserInfo, true, CODE_SUCCESS, msg);
								else
									callListener(finalListener, finaluserInfo, false, CODE_BIND_VERTIFY_ID, "登录成功,但是验证用户id失败!");
								return;
							}
							callListener(finalListener, finaluserInfo, false, CODE_BIND_RETURN_FAIL, msg);
						} catch (JSONException e) {
							callListener(finalListener, finaluserInfo, false, CODE_JSON_FAIL, e.toString());
							TAGS.log("bindUidAndDianId-->ex:" + e.toString());
						}
					}
				} else {
					callListener(finalListener, finaluserInfo, false, CODE_LOGIN_BIND_FAIL, "uid, dianId or token is not exist");
				}
				
			}
		}).start();
	}
	
	
	private void callListener(IAuthorizeListener listener, boolean isSuccess, int code, String info) {
		
		UserInfo usrTmp = (userInfo != null ? userInfo.copy() : null);
		
		callListener(listener, usrTmp, isSuccess, code, info);
	}
	
	private static void callListener(IAuthorizeListener listener, UserInfo userInfo, boolean isSuccess, int code, String info) {
		final IAuthorizeListener finalListener = listener;
		final boolean finalIsSuccess = isSuccess;
		final int finalCode = code;
		final UserInfo finalUserInfo = (userInfo != null ? userInfo.copy() : null);
		final String finalInfo = info;
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				if(finalListener != null) {
					if(finalIsSuccess)
						finalListener.success(finalUserInfo, finalCode, finalInfo);
					else
						finalListener.fail(finalUserInfo, finalCode, finalInfo);
				}
					
			}
			
		});
	}
	
}
