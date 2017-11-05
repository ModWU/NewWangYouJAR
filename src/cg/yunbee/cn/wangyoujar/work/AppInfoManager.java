package cg.yunbee.cn.wangyoujar.work;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkConfig.AIBEIConfig;
import cg.yunbee.cn.wangyoujar.sdkConfig.SecretData;
import cg.yunbee.cn.wangyoujar.sdkConfig.WEIXINConfig;
import cg.yunbee.cn.wangyoujar.utils.LoadFileToString;
import cg.yunbee.cn.wangyoujar.utils.SharedPreferencesManager;

public class AppInfoManager {
	private static PackageInfo packageInfo = null;
	public static final String PREFERENCE_NAME = "wang_you_jar_p";

	public static SecretData initData(Context context) {
		if (packageInfo == null) {
			try {
				packageInfo = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0);
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				TAGS.log(e.getMessage());
			}
		}
		String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
		TAGS.log("jarVersion:"
				+ SecretData.getInstance().getJarVersion());
		SecretData secretData = SecretData.getInstance();
		if (packageInfo != null) {
			secretData.setVersionCode(String.valueOf(packageInfo.versionCode));
			TAGS.log("versionCode:" + secretData.getVersionCode());
		}
		secretData.setAppName(appName);
		TAGS.log("appName:" + secretData.getAppName());
		secretData.setVersionName(packageInfo.versionName);
		TAGS.log("versionName:" + secretData.getVersionName());
		secretData.setPackageName(packageInfo.packageName);
		TAGS.log("packageName:" + secretData.getPackageName());
		String plugVersionCode = "";
		// 用内部存储路径,android4.1系统以后,dex文件不能在外部存储空间被加载
		File filesDir = context.getFilesDir();
		secretData.setPlugVersion(plugVersionCode);
		TAGS.log("plugVersionCode:" + secretData.getPlugVersion());
		String appConfigString = LoadFileToString.loadFileFromAssets(context,
				secretData.getAppConfigFileName());
		JSONObject appConfigJson = null;
		try {
			if (appConfigString != null) {
				appConfigJson = new JSONObject(appConfigString);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			TAGS.log(e.getMessage());
		}
		SharedPreferencesManager SharedPreferencesManager = new SharedPreferencesManager(
				context, PREFERENCE_NAME);
		//String uid = SharedPreferencesManager.getValue("uid");
		String startGameTime = String
				.valueOf(System.currentTimeMillis() / 1000);
		SharedPreferencesManager.save("start_game_time", startGameTime);
		/*if ("".equals(uid)) {
			secretData.setUid("0");
		} else {
			secretData.setUid(uid);
		}*/
		
		
		if (appConfigJson != null) {
			secretData.setAppId(appConfigJson.optString("appId"));
			secretData.setChannelId(appConfigJson.optString("channelId"));
			secretData.setSubChannelId(appConfigJson.optString("subChannelId"));
			secretData.setPackageId(appConfigJson.optString("packageId"));
			secretData.setOrientation(appConfigJson.optString("orientation"));
			secretData.setGameName(appConfigJson.optString("gameName"));
			secretData.setDebug(appConfigJson.optString("debug"));
			TAGS.log("appId:" + secretData.getAppId());
			TAGS.log("channelId:" + secretData.getChannelId());
			TAGS.log("subChannelId:" + secretData.getSubChannelId());
			TAGS.log("packageId:" + secretData.getPackageId());
			TAGS.log("orientation:" + secretData.getOrientation());
			TAGS.log("gameName:" + secretData.getGameName());
			TAGS.log("debug:" + secretData.getDebug());
			
			addSDKConfigs(secretData, appConfigJson);
			
			/*String sdkType = appConfigJson.optString("sdkType");
			
			TAGS.log("sdkType:" + sdkType);
			
			if(sdkType != null && !"".equals(sdkType.trim())) {
				String[] sdks = sdkType.split(",");
				Set<String> paySdkSet = new HashSet<String>();
				//过滤特殊的支付方式
				for(String perSdk : sdks) {
					String sdk = "";
					int line_index = perSdk.indexOf("_");
					if(line_index <= 0) {
						sdk = perSdk;
					} else {
						sdk = perSdk.substring(0, line_index);
					}
					
					paySdkSet.add(sdk);
				}
				
				for(String sdk : paySdkSet) {
					addSDKConfig(secretData, sdk, appConfigJson);
				}
			}*/
			
		}
		return secretData;
	}
	
	
	private static void addSDKConfigs(SecretData secretData, JSONObject jsonObj) {
		TAGS.log("-----------------------getSDKConfig--------------------");
		//爱贝
		try {
			AIBEIConfig aibeiConfig = new AIBEIConfig();
			aibeiConfig.setAppId(jsonObj.getString("aibeiAppId"));
			aibeiConfig.setOrientation(jsonObj.getString("aibeiOrientation"));
			aibeiConfig.setPrivateKey(jsonObj.getString("aibeiPrivateKey"));
			aibeiConfig.setPublicKey(jsonObj.getString("aibeiPublicKey"));
			TAGS.log("aibeiAppId: " + aibeiConfig.getAppId());
			TAGS.log("abeiOrientation: " + aibeiConfig.getOrientation());
			TAGS.log("aibeiPrivateKey: " + aibeiConfig.getPrivateKey());
			
			/*Map<String, PropertyInfo> mapProInfo = getPropertyInfMap(sdkType, proInfoArr);
			aibeiConfig.addHashMap(mapProInfo);*/
			
			secretData.addSDKConfig(SecretData.TYPE_AIBEI, aibeiConfig);
			
			//微信
			WEIXINConfig weixinConfig = new WEIXINConfig();
			weixinConfig.setAppId(jsonObj.getString("weixinAppId"));
			weixinConfig.setPartnerId(jsonObj.getString("weixinPartnerId"));
			TAGS.log("weixinAppId: " + weixinConfig.getAppId());
			TAGS.log("weixinPartnerId: " + weixinConfig.getPartnerId());
			
			secretData.addSDKConfig(SecretData.TYPE_WEIXIN, weixinConfig);
		
		} catch(Exception e) {
			TAGS.log("addSDKConfig-->ex: " + e.toString());
		}
		
		/*TAGS.log("sdkType: " + sdkType);
		try {
			//JSONArray proInfoArr = jsonObj.getJSONArray("propertyInfo");
			if(SecretData.TYPE_AIBEI.equals(sdkType)) {
				AIBEIConfig aibeiConfig = new AIBEIConfig();
				aibeiConfig.setAppId(jsonObj.getString("aibeiAppId"));
				aibeiConfig.setOrientation(jsonObj.getString("aibeiOrientation"));
				aibeiConfig.setPrivateKey(jsonObj.getString("aibeiPrivateKey"));
				aibeiConfig.setPublicKey(jsonObj.getString("aibeiPublicKey"));
				TAGS.log("aibeiAppId: " + aibeiConfig.getAppId());
				TAGS.log("abeiOrientation: " + aibeiConfig.getOrientation());
				TAGS.log("aibeiPrivateKey: " + aibeiConfig.getPrivateKey());
				
				Map<String, PropertyInfo> mapProInfo = getPropertyInfMap(sdkType, proInfoArr);
				aibeiConfig.addHashMap(mapProInfo);
				
				secretData.addSDKConfig(SecretData.TYPE_AIBEI, aibeiConfig);
			} else if(SecretData.TYPE_ALIPAY.equals(sdkType)) {
				ALIPAYConfig alipayConfig = new ALIPAYConfig();
				secretData.addSDKConfig(SecretData.TYPE_ALIPAY, alipayConfig);
			} else if(SecretData.TYPE_WEIXIN.equals(sdkType)) {
				WEIXINConfig weixinConfig = new WEIXINConfig();
				weixinConfig.setAppId(jsonObj.getString("weixinAppId"));
				weixinConfig.setPartnerId(jsonObj.getString("weixinPartnerId"));
				TAGS.log("weixinAppId: " + weixinConfig.getAppId());
				TAGS.log("weixinPartnerId: " + weixinConfig.getPartnerId());
				secretData.addSDKConfig(SecretData.TYPE_WEIXIN, weixinConfig);
			} else if(SecretData.TYPE_YHXF.equals(sdkType)) {
				YHXFConfig yhxfConfig = new YHXFConfig();
				secretData.addSDKConfig(SecretData.TYPE_YHXF, yhxfConfig);
			} else if(SecretData.TYPE_YST.equals(sdkType)) {
				YSTConfig ystConfig = new YSTConfig();
				secretData.addSDKConfig(SecretData.TYPE_YST, ystConfig);
			} else if(SecretData.TYPE_WFT.equals(sdkType)) {
				WFTConfig wftConfig = new WFTConfig();
				secretData.addSDKConfig(SecretData.TYPE_WFT, wftConfig);
			} else if(SecretData.TYPE_QRCODE.equals(sdkType)) {
				QRCODEConfig qrcodeConfig = new QRCODEConfig();
				secretData.addSDKConfig(SecretData.TYPE_QRCODE, qrcodeConfig);
			} else if(SecretData.TYPE_DYH5.equals(sdkType)) {
				DYH5Config dyh5Config = new DYH5Config();
				secretData.addSDKConfig(SecretData.TYPE_DYH5, dyh5Config);
			}
			
			
		} catch(Exception e) {
			TAGS.log("getSDKConfig-->ex: " + e.toString());
		}*/
	}
	

	public static PackageInfo getApplicationInfo(Context context, Uri packageURI) {
		final String archiveFilePath = packageURI.getPath();
		PackageInfo info = null;

		try {
			PackageManager pm = context.getPackageManager();
			info = pm.getPackageArchiveInfo(archiveFilePath,
					PackageManager.GET_ACTIVITIES);
			if (info != null) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			TAGS.log(e.getMessage());
		}
		return info;
	}
}
