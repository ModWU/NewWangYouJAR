package cn.yunbee.cn.wangyoujar.joggleUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

public class Utils {
	
	private static final String SHARE_FILENAME_LOCAL_JAR = "dy_preshare_local_jar";
	
	 static boolean private_copyAssets(AssetManager am, String assetsDir, String toDirPath, boolean isReplace) {
		
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
	
	
	private static void copyFile(InputStream fis, FileOutputStream fos) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = fis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
			fos.flush();
		}
	}
	
	
	
	
	public static boolean createFileDir(String dirPath) {
		try {
			File dir = new File(dirPath);
			if(!dir.exists()) {
				return dir.mkdirs();
			}
		} catch(Exception e) {
			Log.i("INFO", "createFileDir-->ex:" + e.toString());
		}
		return false;
	}
	
	
	
	public static boolean isSd() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		
	}
	
	public static File getCachePath(Context context) {
		File file = null;
		if(isSd()) {
			Log.i("chao", "2:" + 2);
			file = context.getExternalCacheDir();
		}
		
		if(file == null) {
			Log.i("chao", "1:" + 1);
			file = context.getCacheDir();
		}
			
		Log.i("chao", "context:" + context);
		Log.i("chao", "file:" + file);
		if(!file.exists())
			file.mkdirs();
		
		return file;
		
	}
	
	
	static void replaceClassLoader(ClassLoader loader, Activity activity) {  
	    try {  
	        Class clazz_Ath = Class.forName("android.app.ActivityThread");  
	        Class clazz_LApk = Class.forName("android.app.LoadedApk");  
	        Object currentActivityThread = clazz_Ath.getMethod("currentActivityThread").invoke(null);  
	        Field field1 = clazz_Ath.getDeclaredField("mPackages");  
	        field1.setAccessible(true);  
	        Map mPackages = (Map) field1.get(currentActivityThread);  
	        String packageName = activity.getPackageName();  
	        WeakReference ref = (WeakReference) mPackages.get(packageName);  
	        Field field2 = clazz_LApk.getDeclaredField("mClassLoader");  
	        field2.setAccessible(true);  
	        field2.set(ref.get(), loader); 
	        
	        Log.i("INFO", "replaceClassLoader successful!");
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        Log.i("INFO", "replaceClassLoader-->ex: " + e.toString());
	    }  
	} 
	
	
	static void clearClassLoader(Activity activity) {  
	    try {  
	        Class clazz_Ath = Class.forName("android.app.ActivityThread");  
	        Class clazz_LApk = Class.forName("android.app.LoadedApk");  
	        Object currentActivityThread = clazz_Ath.getMethod("currentActivityThread").invoke(null);  
	        Field field1 = clazz_Ath.getDeclaredField("mPackages");  
	        field1.setAccessible(true);  
	        Map mPackages = (Map) field1.get(currentActivityThread);  
	        String packageName = activity.getPackageName();  
	        WeakReference ref = (WeakReference) mPackages.get(packageName);  
	        Field field2 = clazz_LApk.getDeclaredField("mClassLoader");  
	        field2.setAccessible(true);  
	        field2.set(ref.get(), activity.getClassLoader()); 
	        
	        Log.i("INFO", "clearClassLoader successful!");
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        Log.i("INFO", "clearClassLoader-->ex: " + e.toString());
	    }  
	} 
	
	
	
	public static boolean checkJarExitsInPhone(Context context) {
		Log.i("INFO", "--------------------checkJarExitsInPhone--------------------------");
		SharedPreferencesUtils spfUtils = SharedPreferencesUtils.getInstance(context, SHARE_FILENAME_LOCAL_JAR);
		String flag = "flag_dywangyou_jarPaths";
		boolean isJarOutpath = checkJarOutPath(context);
		Log.i("INFO", "isJarOutpath: " + isJarOutpath);
		if(isJarOutpath) {
			String[] allJarFiles = spfUtils.getString(flag, "").split(File.pathSeparator);
			if(allJarFiles != null) {
				int count = allJarFiles.length;
				if(count > 0) {
					for(int i = 0; i < count; i++) {
						File f = new File(allJarFiles[i]);
						Log.i("INFO", "file: " + f);
						if(!f.exists()) {
							return false;
						}
					}
				}
			}
		}
				
		return true;
	}
	
	
	public static List<String> getPluginsFromPhone(Context context) {
		SharedPreferencesUtils spfu = SharedPreferencesUtils.getInstance(context, SHARE_FILENAME_LOCAL_JAR);
		
		String flag = "dy_phone_dywangyou_plugins";
		
		
		String p_allJars = spfu.getString(flag, null);
		if(p_allJars != null) {
			String[] allJars = p_allJars.split("\\|");
			if(allJars != null && allJars.length > 0) {
				List<String> allJarList = new ArrayList<String>();
				for(String jarName : allJars) {
					allJarList.add(jarName);
				}
				return allJarList;
			}
		}
		
		return null;
		
	}
	
	//此目的是防止外部jar被用户删除
	public static void saveLocalJarFlagsToPhone(Context context, String jarDir) {
		if(jarDir == null) return;
		SharedPreferencesUtils spfUtils = SharedPreferencesUtils.getInstance(context, SHARE_FILENAME_LOCAL_JAR);
		//先检查jar是保存在外部路径还是内部路径上
		boolean isJarOutpath = checkJarOutPath(context);
					
					
		String flag = "flag_dywangyou_jarPaths";
					
		//清除数据
		spfUtils.saveString(flag, "").commit();
		if(isJarOutpath) {
			File jarsFile = new File(jarDir);
			File allFiles[] = jarsFile.listFiles();
			int count = allFiles.length;
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
							
	}
	
	private static boolean checkJarOutPath(Context context) {
		File jarRealPath = getCachePath(context);
		File inPath = context.getCacheDir();
		if(jarRealPath.getAbsolutePath().equals(inPath.getAbsolutePath())) {
			return false;
		} else {
			return true;
		}
	}
	
	
	static String parsePath(String parentDir, List<String> allJarNames) {
		if(parentDir == null || allJarNames == null) return "";
		StringBuffer buffer = new StringBuffer();
		for(String name : allJarNames) {
			buffer.append(name).append(File.pathSeparator);
		}
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}
}
