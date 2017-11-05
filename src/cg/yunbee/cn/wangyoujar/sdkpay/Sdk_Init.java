package cg.yunbee.cn.wangyoujar.sdkpay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import cg.yunbee.cn.wangyoujar.feeInfo.InitFeeInfo;
import cg.yunbee.cn.wangyoujar.pojo.PluginInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkConfig.AIBEIConfig;
import cg.yunbee.cn.wangyoujar.sdkConfig.SecretData;
import dy.compatibility.joggle.IAiBeiSDK;
import dy.compatibility.joggle.IAlipaySDK;
import dy.compatibility.joggle.IYHXFSDK;
import dy.compatibility.work.Contant;
import dy.compatibility.work.SDKOptProducer;
import dy.compatibility.work.Utils;

public class Sdk_Init {
	
	private static final String NAME_ALIPAY = "alipay";
	private static final String NAME_AIBEI = "aibei";
	private static final String NAME_YST = "yst";
	private static final String NAME_YHXF = "yhxf";
	private static final String NAME_WEIXIN = "weixin";
	private static final String NAME_WFT = "wft";
	
	private static Map<String, List<String>> addDefaultCmptSdk() {
		
		Map<String, List<String>> cmptSdk = new HashMap<String, List<String>>();
		
		//这是杰莘宏业sdk-------
		/*if(allLocalNames.contains("jxhy")) {
			ArrayList<String> jxhy_jarNames = new ArrayList<String>();
			jxhy_jarNames.add("JshyPay_dex.jar");
			jxhy_jarNames.add("dycomptSDKimpl_dex.jar");
			jxhy_jarNames.add("libammsdk_dex.jar");
			jxhy_jarNames.add("JshyPayApp_games_dex.jar");
				
			cmptSdk.put(Contant.COMPT_JXHY, jxhy_jarNames);
		}*/
		
		
		
		//这是支付宝sdk-------
		ArrayList<String> aplipay_jarNames = new ArrayList<String>();
		aplipay_jarNames.add("alipaySdk-20160825_dex.jar");
		aplipay_jarNames.add("dycomptSDKimpl_dex.jar");
		cmptSdk.put(Contant.COMPT_ALIPAY, aplipay_jarNames);
		
		
		
		//这是爱贝sdk
		ArrayList<String> aibei_jarNames = new ArrayList<String>();
		aibei_jarNames.add("iapppay_plugin_dex.jar");
		aibei_jarNames.add("dycomptSDKimpl_dex.jar");
		cmptSdk.put(Contant.COMPT_AIBEI, aibei_jarNames);
		
		
		//这是银视通
		ArrayList<String> yst_jarNames = new ArrayList<String>();
		yst_jarNames.add("yst_dex.jar");
		yst_jarNames.add("dycomptSDKimpl_dex.jar");
		cmptSdk.put(Contant.COMPT_YST, yst_jarNames);
		
		
		//盈华讯方
		ArrayList<String> yhxf_jarNames = new ArrayList<String>();
		yhxf_jarNames.add("VsofoWapPay_V1.0.2.0_dex.jar");
		yhxf_jarNames.add("dycomptSDKimpl_dex.jar");
			
		cmptSdk.put(Contant.COMPT_YHXF, yhxf_jarNames);
		
		
		
		
		/****************all****************/
		//这是微信,对于all,加个前缀，便于更新
		ArrayList<String> weixin_jarNames = new ArrayList<String>();
		weixin_jarNames.add("weixin_libammsdk_dex.jar");
		weixin_jarNames.add("weixin_dycomptSDKimpl_dex.jar");
		cmptSdk.put(Contant.COMPT_ALL_WEIXIN, weixin_jarNames);
		
		//威富通
		ArrayList<String> wft_jarNames = new ArrayList<String>();
		wft_jarNames.add("wft_wftsdk4.1_dex.jar");
		wft_jarNames.add("wft_dycomptSDKimpl_dex.jar");
		cmptSdk.put(Contant.COMPT_ALL_WFT, wft_jarNames);
		
		return cmptSdk;
	}
	
	private static ArrayList<String> getDefaultSdkNames() {
		ArrayList<String> sdkNameList = new ArrayList<String>();
		sdkNameList.addAll(Arrays.asList(NAME_ALIPAY, NAME_AIBEI, NAME_WEIXIN, NAME_WFT, NAME_YHXF, NAME_YST));
		return sdkNameList;
	}
	
	
	public static void initSdk(Activity activity, boolean isReinit) {
		/*aibei_init(activity);
		jxhy_init(activity);*/
		
		//采用本地appconfig配置了几个就初始化几个的策略
		InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
		//List<String> allLocalNames = getDefaultSdkNames();//initFeeInfo.getListSdkName();
		List<PluginInfo> pluginInfos = Utils.getPluginsFromPhone(activity);
		if(isReinit || pluginInfos == null || pluginInfos.size() <= 0) {
			Map<String, List<String>> cmptSdk = addDefaultCmptSdk();
			doInit(activity, cmptSdk, isReinit);
		} else {
			init(activity, pluginInfos, isReinit);
		}
		
	}
	private static void doInit(Activity activity, Map<String, List<String>> cmptSdk, boolean isReint) {
		//采用本地appconfig配置了几个就初始化几个的策略
		//InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
		final List<String> allLocalNames = getDefaultSdkNames();//initFeeInfo.getListSdkName();
		final Activity finalActivity = activity;
		
		/**
		 * 处理不兼容的sdk --start
		 */
		SDKOptProducer sdkOptProducer = SDKOptProducer.newInstance(activity);
		
		//初始化来构建各自的加载器
		sdkOptProducer.init(cmptSdk, isReint);
		
		TAGS.log("初始化第三方sdk中。。。");
		
		/*new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {*/
				//jar尝试着初始化
				if(allLocalNames.contains("zhifubao"))
					aplipay_init(finalActivity);
				
				if(allLocalNames.contains("aibei"))
					aibei_init(finalActivity);
				
				if(allLocalNames.contains("yhxf")) 
					yhxf_init(finalActivity);
			/*}
			
		});*/
		
		
		/**
		 * 处理不兼容的sdk --end
		 */
	}
	
	/**
	 * 这个初始化留给下载插件成功的时候
	 * @param activity
	 * @param allUpdatePlugins
	 */
	public static void init(Activity activity, List<PluginInfo> allUpdatePlugins) {
		init(activity, allUpdatePlugins, true);
	}
	
	public static void init(Activity activity, List<PluginInfo> allUpdatePlugins, boolean isResInit) {

		if(allUpdatePlugins == null || allUpdatePlugins.isEmpty()) return;
		
		Map<String, List<String>> cmptSdk = new HashMap<String, List<String>>();
		//InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
		List<String> allLocalNames = getDefaultSdkNames();//initFeeInfo.getListSdkName();
		for(PluginInfo p: allUpdatePlugins) {
			if(allLocalNames.contains(p.getName())) {
				List<String> jarUri = p.getJarUris();
				List<String> soUri = p.getSoUris();
				List<String> allUri = new ArrayList<String>();
				if(jarUri != null)
					allUri.addAll(jarUri);
				if(soUri != null) 
					allUri.addAll(soUri);
				List<String> allNames = new ArrayList<String>();
				for(String uri : allUri) {
					allNames.add(Utils.getNameFromUri(uri));
				}
				
				cmptSdk.put(p.getPlugin_type(), allNames);
			}
		}
		if(isResInit) {
			//每当插件成功更新的时候保存数据
			Utils.savePluginsToPhone(activity, allUpdatePlugins);
		}
		doInit(activity, cmptSdk, isResInit);
	}
	
	public static void init(Activity activity, boolean isResInit) {
		//InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
		//List<String> allLocalNames = getDefaultSdkNames();//initFeeInfo.getListSdkName();
		Map<String, List<String>> cmptSdk = addDefaultCmptSdk();
		doInit(activity, cmptSdk, isResInit);
	}
	
	
	private static void aplipay_init(Activity activity) {
		SDKOptProducer sdkOptProducer = SDKOptProducer.newInstance(activity);
		IAlipaySDK alipaySdk = sdkOptProducer.getAlipaySDK();
		alipaySdk.alipay_init(activity);
	}

	private static void aibei_init(Activity activity) {
		try {
			SecretData secretData = SecretData.getInstance();
			
			AIBEIConfig aibeiConfig = (AIBEIConfig) secretData.getSDKConfig(SecretData.TYPE_AIBEI);
			
			String appId = aibeiConfig.getAppId();
			String orientation = aibeiConfig.getOrientation();
			String privateKey = aibeiConfig.getPrivateKey();
			
			SDKOptProducer sdkOptProducer = SDKOptProducer.newInstance(activity);
			IAiBeiSDK aibeiSdk = sdkOptProducer.getAiBeiSDK();
			
			aibeiSdk.aibei_init(activity, orientation, appId);
			
			
			TAGS.log("appId:" + appId);
			TAGS.log("privateKey:" + privateKey);
			TAGS.log("爱贝初始化成功");
		} catch (Exception e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}
	
	private static void yhxf_init(Activity activity) {
		try {
			SDKOptProducer sdkOptProducer = SDKOptProducer.newInstance(activity);
			IYHXFSDK yhxfSDK = sdkOptProducer.getYHXFSDK();
			yhxfSDK.yhxf_init(activity);
			TAGS.log("盈华讯方初始化成功");
		} catch (Exception e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		
	}

}
