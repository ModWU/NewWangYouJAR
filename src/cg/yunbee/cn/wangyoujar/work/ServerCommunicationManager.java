package cg.yunbee.cn.wangyoujar.work;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import cg.yunbee.cn.wangyou.loginSys.LoginRecords;
import cg.yunbee.cn.wangyou.loginSys.UserToken;
import cg.yunbee.cn.wangyoujar.Yunbee;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.feeInfo.InitFeeInfo;
import cg.yunbee.cn.wangyoujar.pojo.DeviceInfo;
import cg.yunbee.cn.wangyoujar.pojo.GameJSONInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkConfig.SecretData;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import cg.yunbee.cn.wangyoujar.utils.LoadFileToString;
import cg.yunbee.cn.wangyoujar.utils.SharedPreferencesManager;
import cn.yunbee.cn.wangyoujar.joggle.IQueryListener;
import dy.compatibility.work.Contant;

public class ServerCommunicationManager {
	// 设置上传服务器的间隔，5分钟
	public static final int UPLOAD_INTERVAL = 5 * 60;

	public static final String PREFERENCE_PAY_RESULT_NAME = "pay_result";
	public static final String PREFERENCE_USER_INFO_NAME = "user_info";
	public static final String PREFERENCE_DOWN_COUNT_NAME = "down_count";
	public static final String PREFERENCE_USER_DURATION_NAME = "user_duration";
	public static final String PREFERENCE_SUCCESS_ITEM_NAME = "success_item";
	
	public static final String PREFERENCE_INIT_SUCCESS_NAME = "init_success";
	

	public static String appConfigStr = null;

	

	
	public static GameJSONInfo getGameJsonInfo(Context context) {
		GameJSONInfo gameJSONInfo = new GameJSONInfo();
		if(appConfigStr == null)
			appConfigStr = LoadFileToString.loadFileFromAssets(context,
					SecretData.appConfigFileName);
		JSONObject appConfigJson = new JSONObject();
		try {
			appConfigJson = new JSONObject(appConfigStr);
			String gameName = appConfigJson.optString("gameName", "demo");
			gameJSONInfo.gameName = gameName;
			
			String secretKey = appConfigJson.optString("secretKey", "dy");
			gameJSONInfo.secretKey = secretKey;
			
		} catch (Exception e) {
		}
		return gameJSONInfo;
	}
	
	
	//初始化信息
	public static boolean initBase(Activity activity) {
		SharedPreferencesManager sp_init_sccuss = new SharedPreferencesManager(
				activity, PREFERENCE_INIT_SUCCESS_NAME);
		TAGS.log("--------------------initBase--------------------");
		String id = sp_init_sccuss.getValue("id");
		boolean isFirstOpenApp = false;
		if(id == null || "".equals(id)) {
			TAGS.log("本地不存在uid， 上传设备信息中...");
			id = initUploadMobilePhoneInfo(activity);
			isFirstOpenApp = true;
			TAGS.log("上传手机信息获取到的uid: " + id);
		} 
		
		if(id == null) id = "";
		
		final boolean isOAuthState = sp_init_sccuss.getBoolean("oAuthState");
		InitFeeInfo.getInstance().setOnAuthState(isOAuthState);
		InitFeeInfo.getInstance().setId(id);
		
		boolean isSuccess = false;
		boolean isNeedLogin = isOAuthState;
		
		TAGS.log("是否需要登录:" + isNeedLogin);
		
		if(id == null || "".equals(id)) {
			TAGS.log("获取uid失败...");
		} else {
			TAGS.log("已获取到uid,为:" + id);
			//保存相关信息
			isSuccess = (isFirstOpenApp && isNeedLogin) ? true : initVefifyId(activity, isNeedLogin, id);
			TAGS.log("是否验证成功:" + isSuccess);
			if(isSuccess) {
				//保存id到本地
				TAGS.log("保存uid到本地,这个uid为:" + InitFeeInfo.getInstance().getId());
				sp_init_sccuss.save("id", InitFeeInfo.getInstance().getId());
				InitFeeInfo.getInstance().setListSdk(SecretData.getInstance().getSdkConfig());
			} else {
				TAGS.log("验证id错误，初始化失败...");
			}
		}
		
		return isSuccess;
	}
	
	public static String getUidFromPhone(Context context) {
		SharedPreferencesManager sp_init_sccuss = new SharedPreferencesManager(
				context, PREFERENCE_INIT_SUCCESS_NAME);
		TAGS.log("--------------------getUidFromPhone--------------------");
		String id = sp_init_sccuss.getValue("id");
		return id;
	}
	

	//1.上传手机信息,并验证id
	public static String initUploadMobilePhoneInfo(Activity activity) {
		TAGS.log("--------------------initUploadMobilePhoneInfo--------------------");
		String url = Contant.HOST + "/User/MobilePhone.ashx";
		DeviceInfo deviceInfo = DeviceInfo.getInstance();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("screen_width", deviceInfo.getScreen_width());
		params.put("screen_height", deviceInfo.getScreen_height());
		params.put("imei", deviceInfo.getImei());
		params.put("imsi", deviceInfo.getImsi());
		params.put("iccid", deviceInfo.getIccid());
		params.put("phone_type", deviceInfo.getPhone_type());
		params.put("phone_version", deviceInfo.getAndroid_version());
		params.put("api_version", deviceInfo.getApi_version());
		params.put("cid", deviceInfo.getCID());
		params.put("lac", deviceInfo.getLAC());
		params.put("netstate", deviceInfo.getNet_state());
		params.put("phone_operate_system", "android");
		params.put("apk_version", SecretData.getInstance().getVersionCode());
		params.put("package_id", SecretData.getInstance().getPackageId());
			
		TAGS.log("param:" + params.toString());
		
			
		String result = HttpUtils.sendPostUTF8(url, params);
				
		TAGS.log("initUploadMobilePhoneInfo-->result:" + result);
				
		try {
			JSONObject	resultObj = new JSONObject(result);
			int code = resultObj.getInt("code");
			if(code == 0) {
				SharedPreferencesManager sp_init_sccuss = new SharedPreferencesManager(
						activity, PREFERENCE_INIT_SUCCESS_NAME);
				String id = resultObj.getString("id");
				int oAuthState = resultObj.getInt("oAuthState");
				InitFeeInfo.getInstance().setId(id);
				InitFeeInfo.getInstance().setOnAuthState(oAuthState == 1 ? true : false);
				sp_init_sccuss.saveBoolean("oAuthState", InitFeeInfo.getInstance().isOAuthState());
				return id;
				
			}
		} catch (Exception e) {
			TAGS.log("initUploadMobilePhoneInfo-->ex:" + e.toString());
		}
		
		return null;
	}
    
	//2.初始化获取id
	public static boolean initVefifyId(Activity activity, boolean isNeedLogin, String id) {
		TAGS.log("--------------------initVefifyId--------------------");
		String url = Contant.HOST + "/Init/Init.ashx";
		
		try {
			UserToken userToken = new UserToken("", "", "");
			if(isNeedLogin) {
				userToken = LoginRecords.getInstance(activity).getUserToken();
				if(userToken == null) {
					TAGS.log("initVefifyId-->token为空");
					userToken = new UserToken("", "", "");
				}
			}
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", id);
			params.put("dianid", userToken.getUserId());
			params.put("token", userToken.getUserToken());
			String result = HttpUtils.sendPostUTF8(url, params);
			TAGS.log("initVefifyId-->result:" + result);
			JSONObject resultJSONObj = new JSONObject(result);
			int code = resultJSONObj.getInt("code");
			
			if(code == 0) {
				SharedPreferencesManager sp_init_sccuss = new SharedPreferencesManager(
						activity, PREFERENCE_INIT_SUCCESS_NAME);
				int oAuthState = resultJSONObj.getInt("oAuthState");
				InitFeeInfo.getInstance().setId(id);
				InitFeeInfo.getInstance().setOnAuthState(oAuthState == 1 ? true : false);
				sp_init_sccuss.saveBoolean("oAuthState", InitFeeInfo.getInstance().isOAuthState());
				return true;
			} else if(code == -1) {
				TAGS.log("initVefifyId-->验证用户id失败，正在重新获取uid...");
				String uid = initUploadMobilePhoneInfo(activity);
				if(uid != null && !uid.equals("")) {
					TAGS.log("initVefifyId-->获取uid成功, 为: " + uid);
					InitFeeInfo.getInstance().setId(uid);
					return true;
				} else {
					TAGS.log("initVefifyId-->获取uid失败!");
				}
			} else if(code == -10) {
				/*LoginRecords.getInstance(activity).clearUserToken();
				popLoginWindow(activity);*/
				return true;
			}
		} catch (Exception e1) {
			TAGS.log("initVefifyId--exception:" + e1);
		}
		
		return false;
	}
	
	public static void return_code_10(Activity activity, Dialog dialog) {
		DialogUtils.closeDialog(activity, dialog);
		Yunbee.isPaying = false;
		YunbeeVice.doPayFlag = false;
		LoginRecords.getInstance(activity).clearUserToken();
		if(Yunbee.getInstance(activity).yunbeeVice != null)
			Yunbee.getInstance(activity).yunbeeVice.payLogin(activity);
	}
	
	
	public static void popLoginWindow(Activity activity) {
		TAGS.log("------------------------popLoginWindow--------------------------");
		final Activity finalActivity = activity;
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				SharedPreferencesManager sp_init_sccuss = new SharedPreferencesManager(
						finalActivity, PREFERENCE_INIT_SUCCESS_NAME);
				sp_init_sccuss.saveBoolean("oAuthState", true);
				InitFeeInfo.getInstance().setOnAuthState(true);
				LoginRecords.getInstance(finalActivity).clearUserToken();
				if(Yunbee.getInstance(finalActivity).yunbeeVice != null) 
					Yunbee.getInstance(finalActivity).yunbeeVice.doLogin(finalActivity);
			}
			
		});
	}
	
	
	/*private static boolean pc_query(String pay_id, int flag, long pTime) {
		String url = Contant.HOST + "/Pay/GetPayResult.ashx";
		long startTime = System.currentTimeMillis();
		try {
			InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", initFeeInfo.getId());
			params.put("pay_id", pay_id);
			String result = HttpUtils.sendPostUTF8(url, params);
			TAGS.log("addQuery-->result:" + result);
			JSONObject resultJSONObj = new JSONObject(result);
			int code = resultJSONObj.getInt("code");
			if(code == 0) {
				JSONObject dataJsonObj = resultJSONObj.optJSONObject("data");
				String resultCode = dataJsonObj.getString("result");
				if(resultCode.equals("0")) {
					return true;
				} else if(resultCode.equals("")) {
					long passTime = System.currentTimeMillis() - startTime;
					long sleepTime = 0;
					
					passTime += pTime;
					
					boolean isFinished = false;
					
					switch(flag) {
					case 0:
						sleepTime = passTime >= 3000 ? 0 : 3000 - passTime;
						break;
					case 1:
						sleepTime = passTime >= 10000 ? 0 : 10000 - passTime;
						break;
					case 2:
						sleepTime = passTime >= 60000 ? 0 : 60000 - passTime;
						break;
					case 3:
						sleepTime = passTime >= 180000 ? 0 : 180000 - passTime;
						break;
					case 4:
						isFinished = true;
						break;
					}
					if(!isFinished) {
						TAGS.log("addQuery-->等待中: flag:" + flag);
						Thread.sleep(sleepTime);
						pTime = sleepTime + passTime;
						return addQuery(pay_id, flag + 1, pTime);
					} else {
						return false;
					}
				}
			}
		} catch (Exception e1) {
			TAGS.log("addQuery--exception:" + e1);
		}
		return false;
	}*/
	
	
	
	private static Boolean pc_query(Activity activity, String pay_id, int flag, long pTime, boolean isCancel[], IQueryListener listener) {
		Log.i(TAGS.PROCESSING, "pc_query->pay_id:" + pay_id);
		final IQueryListener finalListener = listener;
		String url = Contant.HOST + "/Pay/GetPayResult.ashx";
		long startTime = System.currentTimeMillis();
		try {
			if(isCancel[0] ) {
				TAGS.log("pay_id: + " + pay_id + ", 已经取消查询！");
				return null;
			}
			String dianid = "", token = "";
			UserToken userToken = LoginRecords.getInstance(activity).getUserToken();
			if(userToken != null) {
				dianid = userToken.getUserId();
				token = userToken.getUserToken();
			}
			
			InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", initFeeInfo.getId());
			params.put("pay_id", pay_id);
			params.put("dianid", dianid);
			params.put("token", token);
			String result = HttpUtils.sendPostUTF8(url, params);
			TAGS.log("addQuery-->result:" + result);
			JSONObject resultJSONObj = new JSONObject(result);
			int code = resultJSONObj.getInt("code");
			
			if(code == 0) {
				JSONObject dataJsonObj = resultJSONObj.optJSONObject("data");
				String resultCode = dataJsonObj.getString("result");
				if(resultCode.equals("0")) {
					return true;
				}  else if(resultCode.equals("-10")) {
					ServerCommunicationManager.return_code_10(activity, PayManager.dialog);
				} else if(resultCode.equals("")) {
					long passTime = System.currentTimeMillis() - startTime;
					long sleepTime = 0;
					
					passTime += pTime;
					
					boolean isFinished = false;
					
					switch(flag) {
					case 0:
						sleepTime = (passTime >= 3000 ? 0 : 3000 - passTime);
						break;
					case 1:
						sleepTime = (passTime >= 8000 ? 0 : 8000 - passTime);
						break;
					case 2:
						sleepTime = (passTime >= 14000 ? 0 : 14000 - passTime);
						break;
					case 3:
						sleepTime = (passTime >= 17000 ? 0 : 17000 - passTime);
						break;
						
					case 4:
						sleepTime = (passTime >= 20000 ? 0 : 20000 - passTime);
						break;
						
					case 5:
						sleepTime = (passTime >= 25000 ? 0 : 25000 - passTime);
						break;
						
					case 6:
						sleepTime = (passTime >= 30000 ? 0 : 30000 - passTime);
						break;
						
					case 7:
						sleepTime = (passTime >= 35000 ? 0 : 35000 - passTime);
						break;
						
					case 8:
						sleepTime = (passTime >= 40000 ? 0 : 40000 - passTime);
						break;
						
					case 9:
						sleepTime = (passTime >= 45000 ? 0 : 45000 - passTime);
						break;
						
					case 10:
						sleepTime = (passTime >= 60000 ? 0 : 60000 - passTime);
						break;
						
					case 11:
						sleepTime = (passTime >= 75000 ? 0 : 75000 - passTime);
						break;
						
					case 12:
						sleepTime = (passTime >= 90000 ? 0 : 90000 - passTime);
						break;
					case 13:
						sleepTime = (passTime >= 100000 ? 0 : 100000 - passTime);
						break;
						
					case 14:
						sleepTime = (passTime >= 120000 ? 0 : 120000 - passTime);
						break;
					case 15:
						isFinished = true;
						break;
					}
					if(!isFinished) {
						TAGS.log("addQuery-->等待中: flag:" + flag);
						TAGS.log("addQuery-->等待中的时间: " + sleepTime);
						Thread.sleep(sleepTime);
						pTime = sleepTime + passTime;
						return pc_query(activity, pay_id, flag + 1, pTime, isCancel, finalListener);
					} else {
						if(listener != null) {
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									if(finalListener != null)
										finalListener.onQueryCancel();
								}
								
							});
						}
						
						return null;
					}
				}
			}
		} catch (Exception e1) {
			TAGS.log("addQuery--exception:" + e1);
		}
		
		
		return false;
	}


	static void addQuery(Activity activity, IQueryListener listener, String pay_id, int flag, long pTime, boolean isCancel[]) {
		TAGS.log("----------------addQuery---------------");
		TAGS.log("DianPayActivity.dianPayActivity: " + DianPayActivity.dianPayActivity);
		final String final_pay_id = pay_id;
		final int final_flag = flag;
		final long final_pTime = pTime;
		final Activity finalAct = activity;
		final IQueryListener final_listener = listener;
		final boolean finalIsCancel[] = isCancel;
		if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					final Boolean queryRes = pc_query(finalAct, final_pay_id, final_flag, final_pTime, finalIsCancel, final_listener);
					
					if(queryRes != null)
						new Handler(Looper.getMainLooper()).post(new Runnable() {
	
							@Override
							public void run() {
								if(final_listener != null) {
									if(queryRes) {
										final_listener.onQuerySuccess();
									} else {
										final_listener.onQueryFail();
									}
								}
							}
							
						});
				}
				
			}).start();
			
		} else {
			
			final boolean queryRes = pc_query(finalAct, final_pay_id, final_flag, final_pTime, finalIsCancel, final_listener);
			
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					if(final_listener != null) {
						if(queryRes) {
							final_listener.onQuerySuccess();
						} else {
							final_listener.onQueryFail();
						}
					}
				}
				
			});
		}
		
		
	}	
}
