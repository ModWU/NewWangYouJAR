package dy.compatibility.work;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import cg.yunbee.cn.wangyoujar.pojo.PluginInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.SharedPreferencesUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;

public class Utils {
	
	
	public static boolean private_copyAssets(AssetManager am, String assetsDir, String toDirPath, boolean isReplace) {
		
		String[] assetsFiles = null;
		try {
			assetsFiles = am.list(assetsDir);
		} catch (IOException e1) {
			Log.i("INFO", "private_copyAssets->ex:" + e1.toString());
		}
		
		if(assetsFiles == null || assetsFiles.length <= 0) {
			Log.i("INFO", "apk包的assets的目录\"" + assetsDir + "\"" + "下没有任何文件!");
			Log.i("INFO", "停止copy!!!!!!!");
			return true;
		}
		Log.i("INFO", "----------目录:" + assetsDir + "------------");
		Log.i("INFO", "保存到手机的目录:" + toDirPath);
		InputStream fis = null;
		FileOutputStream fos = null;
		
		File toDir = new File(toDirPath);
		if(!toDir.exists()) {
			toDir.mkdirs();
			Log.i("INFO", "手机的目录\"" + toDirPath + "\"不存在，已创建！");
		}
		Log.i("INFO", "toFile.listFiles():" + toDir.listFiles());
		File[] tempFiles = toDir.listFiles();
		if(tempFiles == null) {
			tempFiles = new File[0];
		} 
		List<File> allFiles = Arrays.asList(tempFiles);
		if(isReplace) {
			for(File delFile : allFiles) {
				delFile.delete();
			}
		}
		Log.i("INFO", "----------exit file name------------");
		List<String> allFileName = new ArrayList<String>();
		for(File fn : allFiles) {
			allFileName.add(fn.getName());
			Log.i("INFO", "exit name: " + fn.getName());
		}
		
		try {
			for(String perJar : assetsFiles) {
				String filename = perJar.substring(perJar.lastIndexOf("/") + 1);
				if(!isReplace && allFileName.contains(filename)) {
					continue;
				}
				
				Log.i("INFO", "copy name: " + filename);
				/*if(allFileName.contains(filename)) {
					File newFile_del = new File(toDirPath + "/" + filename);
					newFile_del.renameTo(new File(toDirPath + "/" + "dy" + System.currentTimeMillis() + filename));
					boolean isDelete = newFile_del.delete();
					Log.i("INFO", "newFile: " + newFile_del.getName());
					Log.i("INFO", "isDeleteSuccess: " + isDelete);
				}*/
				
				fos = new FileOutputStream(toDirPath + "/" + filename);
				fis = am.open(assetsDir + "/" + filename);
				copyFile(fis, fos);
				fis.close();
				fos.close();
				
			}
			
			return true;
			
		} catch (Exception e) {
			Log.i("INFO", "private_copyAssets-->ex: " + e.toString());
		}
		return false;
	}
	
	
	
	
	public static String chooseBestLibraryFromAssets(AssetManager am, String assetsSoDir) {
		String[] cpuTypeDirs = checkLibraryWidthCpu();
		
		String[] assetsFiles = null;
		try {
			assetsFiles = am.list(assetsSoDir);
		} catch (IOException e1) {
			Log.i("INFO", "chooseBestLibraryFromAssets->ex:" + e1.toString());
		}
		
		
		Log.i("INFO", "所有支持的so库:" + Arrays.toString(cpuTypeDirs));
		
		Log.i("INFO", "本地所有的so库:" + Arrays.toString(assetsFiles));
		
		if(assetsFiles != null && assetsFiles.length > 0) {
			List<String> allLocalLibDir = Arrays.asList(assetsFiles);
			for(String bestCpuTypeDir : cpuTypeDirs) {
				if(allLocalLibDir.contains(bestCpuTypeDir)) {
					Log.i("INFO", "找到这样的so库:" + bestCpuTypeDir);
					return bestCpuTypeDir;
				}
			}
		}
		
		if(assetsFiles != null && assetsFiles.length > 0) {
			List<String> allLocalLibDir = Arrays.asList(assetsFiles);
			if(allLocalLibDir.contains("armeabi")) {
				Log.i("INFO", "没有这样的so库:" + assetsSoDir + "依然还会选择一个:armeabi");
				return "armeabi";
			}
		}
		
		Log.i("INFO", "没有这样的so库:" + assetsSoDir);
		return "";
	}
	
	public static String chooseBestLibraryFromAssets(String soRootDir) {
		Log.i("INFO", "-------------------chooseBestLibraryFromAssets2---------------------------");
		Log.i("INFO", "soRootDir: " + soRootDir);
		if(soRootDir == null) return null;
		
		File soTypeRoot = new File(soRootDir);
		
		if(!soTypeRoot.exists()) return null;
		
		List<String> allLocalSoTypes = new ArrayList<String>();
		
		File soTypeDirs[] = soTypeRoot.listFiles();
		int len = soTypeDirs.length;
		for(int i = 0; i < len; i++) {
			String perSoType = soTypeDirs[i].getName();
			allLocalSoTypes.add(perSoType);
		}
		
		Log.i("INFO", "allLocalSoTypes: " + allLocalSoTypes);
		
		String[] cpuTypeDirs = checkLibraryWidthCpu();
		
		Log.i("INFO", "allSupportSoTypes: " + (cpuTypeDirs != null ? Arrays.toString(cpuTypeDirs) : "null"));
		
		for(String bestCpuTypeDir : cpuTypeDirs) {
			if(allLocalSoTypes.contains(bestCpuTypeDir)) {
				Log.i("INFO", "找到这样的so库:" + bestCpuTypeDir);
				return bestCpuTypeDir;
			}
		}
		
		Log.i("INFO", "没有找到任何匹配的so库");
		return null;
	}
	
	/**
	 *第三方没提供相应cpu类型库，应该会提供通用兼容类型库armeabi,你懂滴
	 * armeabi,armeabi-v7a,x86,mips,arm64-v8a,mips64,x86_64,这几种目录,如果还有可以自行添加
	 * @return 
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public static String[] checkLibraryWidthCpu() {
		String[] realCpuDirs = null;
		Log.i("INFO", "checkLibraryWidthCpu->Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
		
		if(Build.VERSION.SDK_INT < 21) {
			realCpuDirs = new String[2];
			realCpuDirs[0] = Build.CPU_ABI;
			realCpuDirs[1] = Build.CPU_ABI2;
		} else {
			realCpuDirs = Build.SUPPORTED_ABIS;
		}
		
		return realCpuDirs;
	}


	public static boolean isSd() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		
	}
	
	public static File getCachePath(Context context) {
		File file = null;
		
		Log.i("INFO", "getCachePath-->context..................." + context);
		if(isSd()) {
			file = context.getExternalCacheDir();
		}
		
		if(file == null) {
			file = context.getCacheDir();
		}
		
		if(!file.exists())
			file.mkdirs();
		
		return file;
		
	}
	
	public static void deleteAllFileInDir(String dirPath, boolean delRootDir, boolean isOnExit) {
		File dirFile = new File(dirPath);
		if(dirPath == null || !dirFile.exists()) return;
		
		if(dirFile.isDirectory()) {
			File allFile[] = dirFile.listFiles();
			int count = allFile.length;
			if(count > 0) {
				for(File f : allFile) {
					if(f.isDirectory()) {
						deleteAllFileInDir(f.getAbsolutePath(), true, isOnExit);
					} else {
						if(isOnExit)
							f.deleteOnExit();
						else 
							f.delete();
					}
				}
				
			}
			
			if(delRootDir) {
				if(isOnExit)
					dirFile.deleteOnExit();
				else 
					dirFile.delete();
			}
		}
	}
	
	public static boolean createFileDir(String dirPath) {
		try {
			if(dirPath != null) {
				File dir = new File(dirPath);
				if(!dir.exists()) {
					return dir.mkdirs();
				}
			}
		} catch(Exception e) {
			Log.i("INFO", "createFileDir-->ex:" + e.toString());
		}
		return false;
	}
	
	private static void copyFile(InputStream fis, FileOutputStream fos) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = fis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
			fos.flush();
		}
	}
	
	
	
	public static boolean copyFile(String fromDir, String toDir) {
		TAGS.log("------------------Utils-copyFile-----------------------");
		TAGS.log("Utils->copy->fromDir: " + fromDir);
		TAGS.log("Utils->copy->toDir: " + toDir);
		
		boolean isCopySuccessful = true;
		File fromFile = new File(fromDir);
		
		TAGS.log("Utils->copy->fromFile exists: " + fromFile.exists());
		
		if(fromDir == null || toDir == null || !fromFile.exists()) return false;
		
		createFileDir(toDir);
		
		TAGS.log("Utils->copy->start");
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			
			File[] fromFiles = fromFile.listFiles();
			for(File perF : fromFiles) {
				String toPath = toDir + File.separator + perF.getName();
				if(perF.isDirectory()) {
					isCopySuccessful &= copyFile(perF.getAbsolutePath(), toPath);
				} else {
					fos = new FileOutputStream(toPath);
					fis = new FileInputStream(perF);
					copyFile(fis, fos);
					fis.close();
					fos.close();
				}
				
			}
			
			return isCopySuccessful;
			
		} catch(Exception e) {
			TAGS.log("Utils->copy: ex->" + e.toString());
			Log.i("INFO", "copyFile(String , String) ex:" + e.toString());
		}
		return false;
	}


	public static long getLocalFileSize(String filePath) {
		File file = new File(filePath);
		if(file.exists()) {
			return file.length();
		}
		return 0;
	}




	public static String getNameFromUri(String uri) {
		if(uri == null) return null;
		return uri.substring(uri.lastIndexOf("/") + 1, uri.length());
	}

	public static void checkFilePath(String filePath) {
		File fDir = new File(filePath);
		if(!fDir.exists()) fDir.mkdirs();
	}

	
	public static void savePluginsToPhone(Context context, List<PluginInfo> pluginInfos) {
		if(context == null || pluginInfos == null) return;
		
		SharedPreferencesUtils spfu = SharedPreferencesUtils.newInstance(context, Contant.SHARE_FILENAME_CMPT_SDK);
		
		StringBuffer data = new StringBuffer();
		data.append("{");
		
		for(PluginInfo p : pluginInfos) {
			data.append("\"" + p.getPlugin_type() + "\":");
			data.append("[");
			
			if(p.getJarUris() != null && p.getJarUris().size() > 0) {
				for(String jarUri : p.getJarUris())
					data.append("\"" + jarUri + "\",");
			}
			
			if(p.getSoUris() != null && p.getSoUris().size() > 0) {
				for(String soUri : p.getSoUris()) {
					data.append("\"" + soUri + "\",");
				}
			}
			
			data.deleteCharAt(data.length() - 1);
			
			data.append("]");
			data.append(",");
		}
		data.deleteCharAt(data.length() - 1);
		data.append("}");
		spfu.saveString("dy_phone_plugins", data.toString()).commit();
		
	}
	
	
	public static void parseUriFromPhone(List<PluginInfo> pluginInfos, JSONArray jsonArr, String sdkType, String sdkName) throws JSONException {
		
		if(jsonArr != null && jsonArr.length() > 0) {
			PluginInfo pluginInfo = new PluginInfo();
			List<String> jarUris = new ArrayList<String>();
			List<String> soUris = new ArrayList<String>();
			for(int i = 0; i < jsonArr.length(); i++) {
				String uri = jsonArr.getString(i);
				if(uri.endsWith(".jar") || uri.endsWith(".apk") || uri.endsWith(".dex")) {
					jarUris.add(uri);
				} else if(uri.endsWith(".so")) {
					soUris.add(uri);
				}
			}
			if(jarUris != null) {
				pluginInfo.setJarUris(jarUris);
			}
				
			if(soUris != null) {
				pluginInfo.setSoUris(soUris);
			}
				
			pluginInfo.setPlugin_type(sdkType);
			pluginInfo.setName(sdkName);
			pluginInfos.add(pluginInfo);
		}
	}
	
	
	public static List<PluginInfo> getPluginsFromPhone(Context context) {
		SharedPreferencesUtils spfu = SharedPreferencesUtils.newInstance(context, Contant.SHARE_FILENAME_CMPT_SDK);
		String data = spfu.getString("dy_phone_plugins", null);
		List<PluginInfo> pluginInfos = null;
		try {
			if(data != null) {
				pluginInfos = new ArrayList<PluginInfo>();
				JSONObject jsonObj = new JSONObject(data);
				
				Utils.parseUriFromPhone(pluginInfos, jsonObj.optJSONArray(Contant.COMPT_AIBEI), Contant.COMPT_AIBEI, "aibei");
				
				Utils.parseUriFromPhone(pluginInfos, jsonObj.optJSONArray(Contant.COMPT_ALIPAY), Contant.COMPT_ALIPAY, "alipay");
				
				Utils.parseUriFromPhone(pluginInfos, jsonObj.optJSONArray(Contant.COMPT_JXHY), Contant.COMPT_JXHY, "jxhy");
				
				Utils.parseUriFromPhone(pluginInfos, jsonObj.optJSONArray(Contant.COMPT_YST), Contant.COMPT_YST, "yst");
				
				Utils.parseUriFromPhone(pluginInfos, jsonObj.optJSONArray(Contant.COMPT_YHXF), Contant.COMPT_YHXF, "yhxf");
				
				Utils.parseUriFromPhone(pluginInfos, jsonObj.optJSONArray(Contant.COMPT_ALL_WEIXIN), Contant.COMPT_ALL_WEIXIN, "weixin");
				
				Utils.parseUriFromPhone(pluginInfos, jsonObj.optJSONArray(Contant.COMPT_ALL_WFT), Contant.COMPT_ALL_WFT, "wft");
			}
		} catch(Exception e) {
			Log.i("INFO", "getPluginsFromPhone->ex:" + e.toString());
		}
		
		return pluginInfos;
	}
	
	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout",
				paramContext.getPackageName());
	}

	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id",
				paramContext.getPackageName());
	}
	
	public static int getResourceId(Context paramContext, String resourceType,
			String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				resourceType, paramContext.getPackageName());
	}


	public static boolean checkJarOutPath(Context context) {
		File jarRealPath = getCachePath(context);
		File inPath = context.getCacheDir();
		if(jarRealPath.getAbsolutePath().equals(inPath.getAbsolutePath())) {
			return false;
		} else {
			return true;
		}
	}
	

	public static boolean checkCopyFile(Context context, String jarPathsFlag, String fromAssetsDir, String toJarDir, String toSoDir) {
		SharedPreferencesUtils spfUtils = SharedPreferencesUtils.newInstance(context, Contant.SHARE_FILENAME_LOCAL_JAR);
		String pathsFlag = jarPathsFlag;
		String allPaths = spfUtils.getString(pathsFlag, "");
		Log.i("INFO", "allPaths: " + allPaths);
		if(!Util.isEmpty(allPaths)) {
			String paths[] = allPaths.split(File.pathSeparator);
			for(String path : paths) {
				File f = new File(path);
				if(!f.exists()) {
					return true;
				}
			}
				
		}
		return false;
	}
	
	
	
}
