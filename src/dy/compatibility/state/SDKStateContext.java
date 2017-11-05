package dy.compatibility.state;

import java.io.File;

import android.content.Context;
import android.util.Log;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.SharedPreferencesUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cn.yunbee.cn.wangyoujar.update.SdkContant;
import dy.compatibility.work.Contant;
import dy.compatibility.work.Utils;

/**
 * 写这套类集完全是为了便于修改
 * @author Administrator
 *
 */
public class SDKStateContext implements IState {
	
	private final static SDKStateContext CONTEXT = new SDKStateContext();
	
	public static final NULL NULL = new NULL();
	public static final SDKAibeiState AIBEI_STATE = new SDKAibeiState();
	public static final SDKAlipayState ALIPAY_STATE = new SDKAlipayState();
	public static final SDKJxhyState JXHY_STATE = new SDKJxhyState();
	public static final SDKWeiXinState WEIXIN_STATE = new SDKWeiXinState();
	public static final SDKDYWangyouState DYWANGYOU_STATE = new SDKDYWangyouState();
	public static final SDKYstState YST_STATE = new SDKYstState();
	public static final SDKWftState WFT_STATE = new SDKWftState();
	
	
	public static final SDKYhxfState YHXF_STATE = new SDKYhxfState();
	
	private IState currentState = NULL;
	
	private SDKStateContext() {}
	
	public static SDKStateContext getInstance() {
		return CONTEXT;
	}
	
	public synchronized SDKStateContext type(String sdkType) {
		if(sdkType.equals(Contant.COMPT_AIBEI)) {
			currentState = AIBEI_STATE;
		} else if(sdkType.equals(Contant.COMPT_ALIPAY)) {
			currentState = ALIPAY_STATE;
		} else if(sdkType.equals(Contant.COMPT_JXHY)) {
			currentState = JXHY_STATE;
		} else if(sdkType.equals(Contant.COMPT_YST)) {
			currentState = YST_STATE;
		} else if(sdkType.equals(Contant.COMPT_DYWANGYOU)) {
			currentState = DYWANGYOU_STATE;
		} else if(sdkType.equals(Contant.COMPT_YHXF)) {
			currentState = YHXF_STATE;
		} else if(sdkType.equals(Contant.COMPT_ALL_WEIXIN)) {
			currentState = WEIXIN_STATE;
		} else if(sdkType.equals(Contant.COMPT_ALL_WFT)) {
			currentState = WFT_STATE;
		} else {
			currentState = NULL;
		}
		return this;
	}
	
	public synchronized SDKStateContext name(String sdkName) {
		if(sdkName.equals("aibei")) {
			currentState = AIBEI_STATE;
		} else if(sdkName.equals("alipay")) {
			currentState = ALIPAY_STATE;
		} else if(sdkName.equals("weixin")) {
			currentState = WEIXIN_STATE;
		} else if(sdkName.equals("jxhy")) {
			currentState = JXHY_STATE;
		} else if(sdkName.equals("dywangyou")) {
			currentState = DYWANGYOU_STATE;
		} else if(sdkName.equals("yhxf")) { 
			currentState = YHXF_STATE;
		} else if(sdkName.equals("yst")) {
			currentState = YST_STATE;
		} else if(sdkName.equals("wft")) {
			currentState = WFT_STATE;
		} else {
			currentState = NULL;
		}
		return this;
	}
	
	public synchronized SDKStateContext state(IState state) {
		if(state != null) {
			currentState = state;
		}
		return this;
	}
	
	public static String getCmptType(SdkContant.SDK_TYPE sdk_type) {
		switch(sdk_type) {
		case AIBEI:
			return Contant.COMPT_AIBEI;
			
		case ALIPAY:
			return Contant.COMPT_ALIPAY;
			
		case JXHY:
			return Contant.COMPT_JXHY;
			
		case WANGYOU:
			return Contant.COMPT_DYWANGYOU;
			
		case WEIXIN:
			return Contant.COMPT_ALL_WEIXIN;
			
		case WFT:
			return Contant.COMPT_ALL_WFT;
			
		case YHXF:
			return Contant.COMPT_YHXF;
			
		case YST:
			return Contant.COMPT_YST;
			
		default:
			return "";
		}
	}
	
	//此目的是防止外部jar被用户删除
	public static void saveLocalJarFlagsToPhone(Context context, String sdkType, String jarDir) {
		if(jarDir == null || Util.isEmpty(sdkType)) return;
		SharedPreferencesUtils spfUtils = SharedPreferencesUtils.newInstance(context, Contant.SHARE_FILENAME_LOCAL_JAR);
		//先检查jar是保存在外部路径还是内部路径上
		boolean isJarOutpath = Utils.checkJarOutPath(context);
				
		String flag  = SDKStateContext.getInstance().type(sdkType).getJarPathsFlag();;
				
		//清除数据
		spfUtils.saveString(flag, "").commit();
		if(isJarOutpath) {
			File jarsFile = new File(jarDir);
			File allFiles[] = jarsFile.listFiles();
			int count = allFiles.length;
			Log.i("INFO", "save->count:" + count);
			if(allFiles != null && count > 0) {
				for(int i = 0; i < count; i++) {
					File f = allFiles[i];
					if(i == count - 1) {
						spfUtils.saveString(flag, spfUtils.getString(flag, "") + f.getAbsolutePath()).commit();
					} else {
						spfUtils.saveString(flag, spfUtils.getString(flag, "") + f.getAbsolutePath() + File.pathSeparator).commit();
					}
				}
			}
		}
		
		Log.i("INFO", "save->flag:" + flag);
		Log.i("INFO", "save->flag:" + spfUtils.getString(flag, ""));
						
	}
	
	@Override
	public synchronized String getJarPathsFlag() {
		return currentState.getJarPathsFlag();
	}

	@Override
	public synchronized boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir,
			boolean isReplace) {
		return currentState.copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, isReplace);
	}

	public static boolean copyAllAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir, boolean isReplace) {
		
		boolean isCoptySuccess = true;
		
		Utils.createFileDir(toJarDir);
		Utils.createFileDir(toSoDir);
		
		isCoptySuccess &= SDKStateContext.getInstance().state(SDKStateContext.AIBEI_STATE).copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, isReplace);
		isCoptySuccess &= SDKStateContext.getInstance().state(SDKStateContext.ALIPAY_STATE).copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, isReplace);
		isCoptySuccess &= SDKStateContext.getInstance().state(SDKStateContext.JXHY_STATE).copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, isReplace);
		isCoptySuccess &= SDKStateContext.getInstance().state(SDKStateContext.WEIXIN_STATE).copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, isReplace);
		isCoptySuccess &= SDKStateContext.getInstance().state(SDKStateContext.YHXF_STATE).copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, isReplace);
		isCoptySuccess &= SDKStateContext.getInstance().state(SDKStateContext.YST_STATE).copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, isReplace);
		isCoptySuccess &= SDKStateContext.getInstance().state(SDKStateContext.WFT_STATE).copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, isReplace);
		
		return isCoptySuccess;
		
	}

	public static boolean checkCopyFiles(Context context2, String assetsDir, String realJarDir, String realSoDir) {
		TAGS.log("-----------------------checkCopyFiles(防止用户删除外部文件)---------------------");
		Utils.createFileDir(realJarDir);
		Utils.createFileDir(realSoDir);
		boolean isCopy = false;
		isCopy |= SDKStateContext.getInstance().state(SDKStateContext.AIBEI_STATE).checkCopyFile(context2, assetsDir, realJarDir, realSoDir);
		isCopy |= SDKStateContext.getInstance().state(SDKStateContext.ALIPAY_STATE).checkCopyFile(context2, assetsDir, realJarDir, realSoDir);
		isCopy |= SDKStateContext.getInstance().state(SDKStateContext.JXHY_STATE).checkCopyFile(context2, assetsDir, realJarDir, realSoDir);
		isCopy |= SDKStateContext.getInstance().state(SDKStateContext.WEIXIN_STATE).checkCopyFile(context2, assetsDir, realJarDir, realSoDir);
		isCopy |= SDKStateContext.getInstance().state(SDKStateContext.YHXF_STATE).checkCopyFile(context2, assetsDir, realJarDir, realSoDir);
		isCopy |= SDKStateContext.getInstance().state(SDKStateContext.YST_STATE).checkCopyFile(context2, assetsDir, realJarDir, realSoDir);
		isCopy |= SDKStateContext.getInstance().state(SDKStateContext.WFT_STATE).checkCopyFile(context2, assetsDir, realJarDir, realSoDir);
		TAGS.log("isCopy: " + isCopy);
		return isCopy;
	}

	@Override
	public boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir) {
		return currentState.checkCopyFile(context, fromAssetsDir, toJarDir, toSoDir);
	}
	
	
	
	/*private static boolean downloadPerPlugin(Context context, String sdkTypeKey, PluginInfo pluginInfo, String sdkUri, String downloadCache_jar,
			String downloadCache_so) {
		HttpURLConnection urlConn = null;
		InputStream inputstream = null;
		FileOutputStream outputStream = null;
		long startPos = 0;
		long newLen = 0;
		long downloadSize = 0;
		
		
		try {
			URL url = new URL(sdkUri);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(20000);
			urlConn.setReadTimeout(120000);
			String fileName = Utils.getNameFromUri(sdkUri);
			
			String downloadCacheFilePath = SDKStateContext.getInstance().type(sdkTypeKey).getDownloadLocalFilePath(downloadCache_so, downloadCache_jar, fileName);
			
			SharedPreferencesUtils spfu = SharedPreferencesUtils.newInstance(context, Contant.SHARE_FILENAME_CMPT_SDK);
			String downloadPluginName = getSharePluginNameWithDownload_static(sdkTypeKey, fileName);
			
			if(downloadPluginName == null) return false;
			
			startPos = spfu.getLong(downloadPluginName, startPos);
			
			downloadSize = startPos;
			
			if(startPos < 0) {
				return false;
			}
			
			Log.i("INFO", "downloadPerPlugin-->正在下载文件:" + fileName);
			urlConn.setRequestProperty("Range", "bytes=" + startPos + "-");// 设置获取实体数据的范围
			inputstream = urlConn.getInputStream();
			outputStream = new FileOutputStream(downloadCacheFilePath, true);
			newLen = urlConn.getContentLength();
			
			long oldLen = spfu.getLong(downloadPluginName + "_len", newLen);
			
			boolean isReDownload = false;
			if(oldLen != newLen) {
				isReDownload = true;
			} else {
				long localFileLen = Utils.getLocalFileSize(downloadCacheFilePath);
				if(localFileLen != downloadSize) {
					isReDownload = true;
				}
			}
			
			
			if(isReDownload) {
				downloadSize = startPos = 0;
				urlConn.setRequestProperty("Range", "bytes=" + startPos + "-");// 设置获取实体数据的范围
				inputstream = urlConn.getInputStream();
				spfu.saveLong(downloadPluginName, 0).saveLong(downloadPluginName + "_len", newLen).commit();
				Log.i("INFO", "downloadPerPlugin-->需要重新下载文件:" + fileName);
			}
			
			byte[] buffer = new byte[1024];
			int curlen = -1;
			while ((curlen = inputstream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, curlen);
				outputStream.flush();
				downloadSize += curlen;
				spfu.saveLong(downloadPluginName, downloadSize).commit();
				if(downloadSize >= newLen) {
					break;
				}
			}
			
			outputStream.close();
			inputstream.close();
			urlConn.disconnect();
			
			//回归位置
			spfu.saveLong(downloadPluginName, 0).saveLong(downloadPluginName + "_len", 0).commit();
			//保存copy的文件长度
			
			String allCopyFileNames = spfu.getString(sdkTypeKey + "_copy_names", fileName);
			
			if(!allCopyFileNames.equals(fileName)) {
				allCopyFileNames = allCopyFileNames + "|" + fileName;
			}
			
			spfu.saveLong(sdkTypeKey + "_copy_" + downloadPluginName, newLen).saveString(sdkTypeKey + "_copy_names", allCopyFileNames).commit();
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("INFO", "downloadPerPlugin->ex:" + e.toString() + ", sdkType:" + sdkTypeKey + ", sdkUri:" + sdkUri);
		}
		
		return false;
		
	}*/
	
}
