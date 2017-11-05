package dy.compatibility.work;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.util.Log;
import dalvik.system.DexClassLoader;

public class CompatibilityClassLoader {
	
	private Map<String, ArrayList<String>> jarDiffClassLoader = new HashMap<String, ArrayList<String>>();
	
	private Map<String, String> soDirMap = new HashMap<String, String>();
	
	private Map<String, ArrayList<String>> all_jarDiffClassLoader = new HashMap<String, ArrayList<String>>();
	
	
	private static volatile CompatibilityClassLoader INSTANCE;

	private CompatibilityClassLoader() {
	}
	
	public static CompatibilityClassLoader getInstance() {
		if(INSTANCE == null) {
			synchronized (CompatibilityClassLoader.class) {
				if(INSTANCE == null) {
					INSTANCE = new CompatibilityClassLoader();
				}
			}
		}
		return INSTANCE;
	}
	
	
	
	public void addJarPath(String name, String path) {
		
		if(!path.endsWith(".jar") && !path.endsWith(".apk") && !path.endsWith(".dex")) return;
		
		ArrayList<String> onePaths = jarDiffClassLoader.get(name);
		if(onePaths == null) {
			onePaths = new ArrayList<String>();
			jarDiffClassLoader.put(name, onePaths);
		}
		
		if(!onePaths.contains(path)) {
			onePaths.add(path);
		}
		
	}
	
	public void addSoDir(String name, String dirPath) {
		Utils.createFileDir(dirPath);
		String oldPath = soDirMap.get(name);
		if(oldPath == null) {
			soDirMap.put(name, dirPath);
		} else {
			soDirMap.put(name, oldPath + File.pathSeparator + dirPath);
		}
	}
	
	public void addJarPath_all(String name, String path) {
		if(!path.endsWith(".jar") && !path.endsWith(".apk") && !path.endsWith(".dex")) return;
		
		ArrayList<String> onePaths = all_jarDiffClassLoader.get(name);
		if(onePaths == null) {
			onePaths = new ArrayList<String>();
			all_jarDiffClassLoader.put(name, onePaths);
		}
		
		if(!onePaths.contains(path)) {
			onePaths.add(path);
		}
	}
	
	public void removeSdk(String name) {
		jarDiffClassLoader.remove(name);
		all_jarDiffClassLoader.remove(name);
		soDirMap.remove(name);
	}
	
	public void clearSdk() {
		jarDiffClassLoader.clear();
		all_jarDiffClassLoader.clear();
		soDirMap.clear();
	}
	
	public Map<String, DexClassLoader> rebuildDuffClassLoader(String dexOutputDir, ClassLoader parent) {
		Map<String, DexClassLoader> diffDexMap = new HashMap<String, DexClassLoader>();
		Set<Entry<String, ArrayList<String>>> jars = jarDiffClassLoader.entrySet();
		//Set<Entry<String, ArrayList<String>>> allSo = soDiffClassLoader.entrySet();
		
		//这里声明：有jar，必然有so。绝大部分jar里面调用so里面的本地方法，所以以jar为准.(除非直接调用so，但但第三方肯定会提供jar里的api)
		//非all
		for(Entry<String, ArrayList<String>> entry : jars) {
			List<String> jar_paths = entry.getValue();
			String soDir = soDirMap.get(entry.getKey());
			
			String load_jar_paths = parsePath(jar_paths);
			
			String sdk_type = entry.getKey();
			
			String tempSdkDexOutputDir = dexOutputDir;
			
			if(sdk_type.equals(Contant.COMPT_AIBEI)) {
				tempSdkDexOutputDir = tempSdkDexOutputDir + File.separator + Contant.FILE_AIBEI;
			} else if(sdk_type.equals(Contant.COMPT_ALIPAY)) {
				tempSdkDexOutputDir = tempSdkDexOutputDir + File.separator + Contant.FILE_ALIPAY;
			} else if(sdk_type.equals(Contant.COMPT_JXHY)) {
				tempSdkDexOutputDir = tempSdkDexOutputDir + File.separator + Contant.FILE_JXHY;
			} else if(sdk_type.equals(Contant.COMPT_YST)) {
				tempSdkDexOutputDir = tempSdkDexOutputDir + File.separator + Contant.FILE_YST;
			} else if(sdk_type.equals(Contant.COMPT_YHXF)) {
				tempSdkDexOutputDir = tempSdkDexOutputDir + File.separator + Contant.FILE_YHXF;
			}
			
			Utils.createFileDir(tempSdkDexOutputDir);
			Log.i("chao", entry.getKey() + ", soDir:" + soDir);
			Log.i("chao", entry.getKey() + ", load_jar_paths:" + load_jar_paths);
			
			DexClassLoader dexClassLoader = new DexClassLoader(load_jar_paths, tempSdkDexOutputDir, soDir, parent);
			
			diffDexMap.put(sdk_type, dexClassLoader);
		}
		
		//all
		Set<Entry<String, ArrayList<String>>> jars_all = all_jarDiffClassLoader.entrySet();
		String soDir = soDirMap.get(Contant.COMPT_ALL);
		StringBuffer load_jar_buffer = new StringBuffer();
		String tempSdkDexOutputDir = dexOutputDir + File.separator + Contant.FILE_ALL;
		for(Entry<String, ArrayList<String>> entry : jars_all) {
			List<String> jarPaths = entry.getValue();
			
			String load_jar_paths = parsePath(jarPaths);
			
			Log.i("chao", entry.getKey() + ", load_jar_paths all:" + load_jar_paths);
			load_jar_buffer.append(load_jar_paths + File.pathSeparator);
		}
		if(load_jar_buffer.length() > 0)
			load_jar_buffer.deleteCharAt(load_jar_buffer.length() - 1);
		Log.i("chao", "all_type, soDir all:" + soDir);
		if(jars_all != null && !jars_all.isEmpty()) {
			Utils.createFileDir(tempSdkDexOutputDir);
			DexClassLoader dexClassLoader = new DexClassLoader(load_jar_buffer.toString(), tempSdkDexOutputDir, soDir, parent);
			diffDexMap.put(Contant.COMPT_ALL, dexClassLoader);
		}
		
		return diffDexMap;
	}
	
	public Map<String, DexClassLoader> rebuildDuffClassLoader(String dexOutputDir) {
		return rebuildDuffClassLoader(dexOutputDir, Thread.currentThread().getContextClassLoader());
	}
	
	private String parsePath(List<String> pathList) {
		if(pathList == null || pathList.isEmpty()) return null;
		
		StringBuffer load_dex_paths = new StringBuffer();
		for(String path : pathList) {
			load_dex_paths.append(path);
			load_dex_paths.append(File.pathSeparator);
		}
		load_dex_paths.deleteCharAt(load_dex_paths.length() - 1);
		return load_dex_paths.toString();
	}
	

}
