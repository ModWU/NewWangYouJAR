package dy.compatibility.work;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;
import dy.compatibility.joggle.IAiBeiSDK;
import dy.compatibility.joggle.IAlipaySDK;
import dy.compatibility.joggle.IJXHYSDK;
import dy.compatibility.joggle.IWFTSDK;
import dy.compatibility.joggle.IWeixinSDK;
import dy.compatibility.joggle.IYHXFSDK;
import dy.compatibility.joggle.IYstSDK;

public class SDKOptProducer {
	
	private CompatibilityClassLoader mClassLoader;
	
	//下载的路径，最好在sd下
	private String downloadJarDir;
	private String downloadSoDir;
	
	//
	private String realJarDir;
	
	private String realOutDexDir;
	
	private String realSoDir;
	
	private Context context;
	
	private boolean isInitFinished = false;
	
	private Map<String, DexClassLoader> diffSdkDexLoader;
	
	private static volatile SDKOptProducer INSTANCE;
	
	
	
	private SDKOptProducer(Context context) {
		this.context = context;
		
		String own_sd_inner_path = Utils.getCachePath(context).getAbsolutePath();
		
		realOutDexDir = context.getCacheDir().getAbsolutePath() + File.separator + Contant.REAL_CACHE_DIR + File.separator + Contant.FILE_DEX;
		realJarDir = own_sd_inner_path + File.separator + Contant.REAL_CACHE_DIR + File.separator + Contant.FILE_JAR;
		//外部不安全，放到内部吧，防止被修改
		realSoDir = context.getCacheDir().getAbsolutePath() + File.separator + Contant.REAL_CACHE_DIR + File.separator + Contant.FILE_SO;
		
		//初始化下载的路径,就缓存到外部吧
		downloadJarDir = own_sd_inner_path + File.separator + Contant.DOWNLOAD_CACHE_DIR + File.separator + Contant.FILE_JAR;
		downloadSoDir = own_sd_inner_path + File.separator + Contant.DOWNLOAD_CACHE_DIR + File.separator + Contant.FILE_SO;
		
		
		mClassLoader = CompatibilityClassLoader.getInstance();
		
		Log.i("INFO", "outJarDir:" + realJarDir);
		
	}
	
	public static SDKOptProducer newInstance(Context context) {
		if(INSTANCE == null) {
			synchronized (SDKOptProducer.class) {
				if(INSTANCE == null) {
					INSTANCE = new SDKOptProducer(context);
				}
			}
		}
		return INSTANCE;
	}
	
	private void rebuildClassLoader(Map<String, List<String>> nameSdksMap) {
		
		mClassLoader.clearSdk();
		
		Set<Entry<String, List<String>>> entrySet = nameSdksMap.entrySet();
		
		for(Entry<String, List<String>> e : entrySet) {
			
			String sdkName = e.getKey();
			List<String> sdkAllJarAndSo = e.getValue();
			
			if(sdkAllJarAndSo == null || sdkAllJarAndSo.isEmpty()) continue;
			
			for(String jarAndSoName : sdkAllJarAndSo) {
				//精确到两种jar,so
				if(jarAndSoName.endsWith(".jar") || jarAndSoName.endsWith(".apk") || jarAndSoName.endsWith(".dex") ) {
					if(sdkName.equals(Contant.COMPT_AIBEI)) {
						mClassLoader.addJarPath(Contant.COMPT_AIBEI, realJarDir + File.separator + Contant.FILE_AIBEI + File.separator + jarAndSoName);
					} else if(sdkName.equals(Contant.COMPT_ALIPAY)) {
						mClassLoader.addJarPath(Contant.COMPT_ALIPAY, realJarDir + File.separator + Contant.FILE_ALIPAY + File.separator + jarAndSoName);
					} else if(sdkName.equals(Contant.COMPT_JXHY)) {
						mClassLoader.addJarPath(Contant.COMPT_JXHY, realJarDir + File.separator + Contant.FILE_JXHY + File.separator + jarAndSoName);
					} else if(sdkName.equals(Contant.COMPT_YST)) {
						mClassLoader.addJarPath(Contant.COMPT_YST, realJarDir + File.separator + Contant.FILE_YST + File.separator + jarAndSoName);
					} else if(sdkName.equals(Contant.COMPT_YHXF)) {
						mClassLoader.addJarPath(Contant.COMPT_YHXF, realJarDir + File.separator + Contant.FILE_YHXF + File.separator + jarAndSoName);
					}
					
					//all目录
					String jar_root_all = realJarDir + File.separator + Contant.FILE_ALL;
					if(sdkName.equals(Contant.COMPT_ALL_WEIXIN)) {
						mClassLoader.addJarPath_all(Contant.COMPT_ALL_WEIXIN, jar_root_all + File.separator + Contant.FILE_WEIXIN + File.separator + jarAndSoName);
					} else if(sdkName.equals(Contant.COMPT_ALL_WFT)) {
						mClassLoader.addJarPath_all(Contant.COMPT_ALL_WFT, jar_root_all + File.separator + Contant.FILE_WFT + File.separator + jarAndSoName);
					}
				}
			}
			
			//现在只有aibei有so文件
			if(sdkName.equals(Contant.COMPT_AIBEI)) {
				mClassLoader.addSoDir(Contant.COMPT_AIBEI, realSoDir + File.separator + Contant.FILE_AIBEI);
			} else if(sdkName.equals(Contant.COMPT_ALIPAY)) {
				mClassLoader.addSoDir(Contant.COMPT_ALIPAY, realSoDir + File.separator + Contant.FILE_ALIPAY);
			} else if(sdkName.equals(Contant.COMPT_JXHY)) {
				mClassLoader.addSoDir(Contant.COMPT_JXHY, realSoDir + File.separator + Contant.FILE_JXHY);
			} else if(sdkName.equals(Contant.COMPT_YST)) {
				mClassLoader.addSoDir(Contant.COMPT_YST, realSoDir + File.separator + Contant.FILE_YST);
			} else if(sdkName.equals(Contant.COMPT_YHXF)) {
				mClassLoader.addSoDir(Contant.COMPT_YHXF, realSoDir + File.separator + Contant.FILE_YHXF);
			}
			
			//all目录,so也分别放
			String so_root_all = realSoDir + File.separator + Contant.FILE_ALL;
			if(sdkName.equals(Contant.COMPT_ALL_WEIXIN)) {
				mClassLoader.addSoDir(Contant.COMPT_ALL, so_root_all + File.separator + Contant.FILE_WEIXIN);
			} else if(sdkName.equals(Contant.COMPT_ALL_WFT)) {
				mClassLoader.addSoDir(Contant.COMPT_ALL, so_root_all + File.separator + Contant.FILE_WFT);
			}
			
		}
		
		ClassLoader cl = getClass().getClassLoader();
		Log.i("yunbee_processing", cl.toString());
		
		Log.i("chao", "---------------rubuildClassLoader----------------");
		Log.i("chao", "-----------parent:" + getClass().getClassLoader().hashCode());
		
		diffSdkDexLoader = mClassLoader.rebuildDuffClassLoader(realOutDexDir, getClass().getClassLoader());
	}
	
	public void init(Map<String, List<String>> nameSdksMap, boolean isReInit) {
		if(nameSdksMap == null || nameSdksMap.isEmpty()) return;
		
		if(!isInitFinished || isReInit) {
			
			//SharedPreferencesUtils spfUtils = SharedPreferencesUtils.getInstance(context, Contant.SHARE_FILENAME_CMPT_SDK);
			
			//copyAllAssetsFile();
			
			rebuildClassLoader(nameSdksMap);
			
			isInitFinished = true;
		}
		
	}
	
	/*public void copyAllAssetsFile() {
		
		Utils.createFileDir(realOutDexDir);
		Utils.createFileDir(realJarDir);
		Utils.createFileDir(realSoDir);
		Utils.createFileDir(downloadJarDir);
		Utils.createFileDir(downloadSoDir);
		
		SharedPreferences sp = context.getSharedPreferences(Contant.SHARE_FILENAME_CMPT_SDK, Context.MODE_APPEND);
		
		//只copy一遍，切记！
		//boolean isCoppySuccess = spfUtils.getBoolean("assets_copy_success", false);
		boolean isCoppySuccess = sp.getBoolean("assets_copy_success", false);
		
		Log.i("INFO", "isCoppySuccess:" + isCoppySuccess);
		
		if(!isCoppySuccess) {
			//so文件不可能都copy进去，根据设备cpu类型(Build.SUPPORTED_ABIS)选择copy，待处理。测试：先全部copy看看
			isCoppySuccess = SDKStateContext.copyAllAssetsFile(context, Contant.ASSETS_DIR, realJarDir, realSoDir, false);
			Log.i("INFO", "copy isCoppySuccess:" + isCoppySuccess);
			//spfUtils.saveBoolean("assets_copy_success", isCoppySuccess).commit();
			sp.edit().putBoolean("assets_copy_success", isCoppySuccess).commit();
		} else {
			Log.i("INFO", "assets已经不需要再copy了,除非用户能手动删除内部存储！");
			//但是 需要检查文件是否被意外删除
			SDKStateContext.checkCopyFiles(context, Contant.ASSETS_DIR, realJarDir, realSoDir);
		}
	}*/
	
	/******************************choose sdk********************************/
	public IJXHYSDK getJxhySDK() {
		try {
			Class<?> clazz = getComptClass(Contant.COMPT_JXHY, "dy.compatibility.ImplSdk.JXHYSDKImpl");
			return (IJXHYSDK) clazz.newInstance();
		} catch (Exception e) {
			Log.i("chao", "getJxhySDK->ex:" + e.toString());
		}
		return null;
	}
	
	public IAiBeiSDK getAiBeiSDK() {
		try {
			Class<?> clazz = getComptClass(Contant.COMPT_AIBEI, "dy.compatibility.ImplSdk.AiBeiSDKImpl");
			return (IAiBeiSDK) clazz.newInstance();
		} catch (Exception e) {
			Log.i("chao", "getAiBeiSDK->ex:" + e.toString());
		}
		return null;
	}
	
	public IAlipaySDK getAlipaySDK() {
		try {
			Class<?> clazz = getComptClass(Contant.COMPT_ALIPAY, "dy.compatibility.ImplSdk.AlipaySDKImpl");
			return (IAlipaySDK) clazz.newInstance();
		} catch (Exception e) {
			Log.i("chao", "getAlipaySDK->ex:" + e.toString());
		}
		return null;
	}
	
	public IYstSDK getYstSDK() {
		try {
			
			Class<?> clazz = getComptClass(Contant.COMPT_YST, "dy.compatibility.ImplSdk.YstSDKImpl");
			Log.i("baba2", "----getYstSDK----");
			Log.i("baba2", "clazz loader:" + clazz.getClassLoader().hashCode());
			Log.i("baba2", "clazz loader:" + clazz.getClassLoader());
			return (IYstSDK) clazz.newInstance();
		} catch (Exception e) {
			Log.i("chao", "getYstSDK->ex:" + e.toString());
		}
		return null;
	}
	
	
	
	private Class<?> getComptClass(String sdkType, String clazzName) {
		try {
			DexClassLoader hintSdkLoader = diffSdkDexLoader.get(sdkType);

			Class<?> clazz = Class.forName(clazzName, true, hintSdkLoader);
			
			return clazz;
		} catch (Exception e) {
			Log.i("chao", "getComptSDKClass->ex:" + e.toString());
		}
		return null;
	}
	
	
	
	public IWeixinSDK getWeixinSDK() {
		try {
			Class<?> clazz = getComptClass(Contant.COMPT_ALL, "dy.compatibility.ImplSdk.WeixinSDKImpl");
			return (IWeixinSDK) clazz.newInstance();
		} catch (Exception e) {
			Log.i("chao", "getWeixinSDK->ex:" + e.toString());
		}
		return null;
	}
	
	public IWFTSDK getWFTSDK() {
		try {
			Class<?> clazz = getComptClass(Contant.COMPT_ALL, "dy.compatibility.ImplSdk.WftSDKImpl");
			return (IWFTSDK) clazz.newInstance();
		} catch (Exception e) {
			Log.i("chao", "getWFTSDK->ex:" + e.toString());
		}
		return null;
	}
	
	public IYHXFSDK getYHXFSDK() {
		try {
			Class<?> clazz = getComptClass(Contant.COMPT_YHXF, "dy.compatibility.ImplSdk.YHXFSDKImpl");
			return (IYHXFSDK) clazz.newInstance();
		} catch (Exception e) {
			Log.i("chao", "getYHXFSDK->ex:" + e.toString());
		}
		return null;
	}
	/******************************choose sdk********************************/
	
	
	public void clearAndDelete() {
		mClassLoader.clearSdk();
		Utils.deleteAllFileInDir(realOutDexDir, true, true);
		Utils.deleteAllFileInDir(realJarDir, true, true);
		Utils.deleteAllFileInDir(realSoDir, true, true);
		Utils.deleteAllFileInDir(downloadJarDir, true, true);
		Utils.deleteAllFileInDir(downloadSoDir, true, true);
		if(diffSdkDexLoader != null) {
			diffSdkDexLoader.clear();
			diffSdkDexLoader = null;
		}
		isInitFinished = false;
	}
	
	public void clearNotDelete() {
		mClassLoader.clearSdk();
		if(diffSdkDexLoader != null) {
			diffSdkDexLoader.clear();
			diffSdkDexLoader = null;
		}
		isInitFinished = false;
	}
	
}
