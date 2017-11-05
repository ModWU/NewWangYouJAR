package cn.yunbee.cn.wangyoujar.update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.SharedPreferencesUtils;
import cn.yunbee.cn.wangyoujar.update.SdkContant.SDK_TYPE;
import dy.compatibility.work.Utils;

public abstract class SdkBuilder {
	
	private Context mContext;
	
	private static final String REAL_CACHE_DIR = "real_cache_dir";
	
	private static final String DOWNLOAD_CACHE_DIR = "download_cache_dir";
	
	private static final String DOWNLOAD_ZIP_DIR = "download_zip";
	
	private static final String SPF_FILE_SDKINFO = "spf_sdkInfo";
	
	private static final String SPF_KEY_DATA = "spf_key_data";
	
	private static final int VERSION = 1;
	
	private static final int STATE = 1;
	
	protected int version = VERSION;
	
	protected String downloadUri;
	
	protected long size;
	
	protected int state = STATE;
	
	protected SdkBuilder(Context context) {
		mContext = context;
	}
	
	public static SdkBuilder build(Context context, SDK_TYPE sdkType) {
		switch(sdkType) {
		case WANGYOU:
			return new SdkWyBuilder(context);
			
		case AIBEI:
			return new SdkAibeiBuilder(context);
			
		case ALIPAY:
			return new SdkAlipayBuilder(context);
			
		case JXHY:
			return new SdkJxhyBuilder(context);
			
		case YST:
			return new SdkYstBuilder(context);
			
		case WEIXIN:
			return new SdkWeixinBuilder(context);
			
		case YHXF:
			return new SdkYhxfBuilder(context);
			
		case WFT:
			return new SdkWftBuilder(context);
			
		default:
		}
		
		return new SdkNULLBuilder(context);
	}
	
	private String getOutPath() {
		File outf = Utils.getCachePath(mContext);
		return outf == null ? "" : outf.getAbsolutePath();
	}
	
	private String getInnerPath() {
		File innerf = mContext.getCacheDir();
		return innerf == null ? "" : innerf.getAbsolutePath();
	}
	
	public static String getOutPath(Context context) {
		File outf = Utils.getCachePath(context);
		return outf == null ? "" : outf.getAbsolutePath();
	}
	
	public static String getInnerPath(Context context) {
		File innerf = context.getCacheDir();
		return innerf == null ? "" : innerf.getAbsolutePath();
	}
	
	
	public static String getDownload_jardir(Context context) {
		return getOutPath(context) + File.separator + DOWNLOAD_CACHE_DIR + File.separator + "jar";
	}
	
	public static String getDownload_sodir(Context context) {
		return getOutPath(context) + File.separator + DOWNLOAD_CACHE_DIR + File.separator + "so";
	}
			
	public static String getReal_jardir(Context context) {
		return getOutPath(context) + File.separator + REAL_CACHE_DIR + File.separator + "jar";
	}
			
	public static String getReal_outdexdir(Context context) {
		return getInnerPath(context) + File.separator + REAL_CACHE_DIR + File.separator + "dex";
	}
			
	public static String getReal_sodir(Context context) {
		return getInnerPath(context) + File.separator + REAL_CACHE_DIR + File.separator + "so";
	}
	
	public static String getDownload_zipDir(Context context) {
		return getOutPath(context) + File.separator + DOWNLOAD_CACHE_DIR + File.separator + DOWNLOAD_ZIP_DIR;
	}
	
	
	protected String getDownload_jardir() {
		return getOutPath() + File.separator + DOWNLOAD_CACHE_DIR + File.separator + "jar";
	}
	
	protected String getDownload_sodir() {
		return getOutPath() + File.separator + DOWNLOAD_CACHE_DIR + File.separator + "so";
	}
			
	protected String getReal_jardir() {
		return getOutPath() + File.separator + REAL_CACHE_DIR + File.separator + "jar";
	}
			
	protected String getReal_outdexdir() {
		return getInnerPath() + File.separator + REAL_CACHE_DIR + File.separator + "dex";
	}
			
	protected String getReal_sodir() {
		return getInnerPath() + File.separator + REAL_CACHE_DIR + File.separator + "so";
	}
	
	protected String getDownload_zipDir() {
		return getOutPath() + File.separator + DOWNLOAD_CACHE_DIR + File.separator + DOWNLOAD_ZIP_DIR;
	}
	
	public static String getDownload_zipRootDir(Context context) {
		File outf = Utils.getCachePath(context);
		if(outf == null) outf = new File("");
		return outf.getAbsolutePath() + File.separator + DOWNLOAD_CACHE_DIR + File.separator + DOWNLOAD_ZIP_DIR;
	}
	
	public static String getZipCachePath(Context context) {
		File outf = Utils.getCachePath(context);
		String outPath = outf == null ? "" : outf.getAbsolutePath();
		return outPath + File.separator + DOWNLOAD_CACHE_DIR + File.separator + DOWNLOAD_ZIP_DIR;
	}
	
	protected List<String> getFileNamesInSodir() {
		List<String> soFileNameList = new ArrayList<String>();
		File soDir = new File(getReal_sodir());
		if(soDir.exists()) {
			String[] soFileNames = soDir.list();
			int length = (soFileNames != null ? soFileNames.length : 0);
			if(length > 0) 
				for(String f : soFileNames)
					soFileNameList.add(f);
		}
		return soFileNameList;
	}
	
	public static long getFileSize(String filepath) {
		long size = 0;
		File file = new File(filepath);
		if(file.exists()) {
			size = file.length();
		}
		return size;
	}
	
	public static String getNameFromUri(String uri) {
		if(uri == null) return "";
        String b = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
        return b;
	}
	
	public static void saveSdkDataList(Context context, List<SdkInfo> allUpdateSdkList) {
		if(allUpdateSdkList != null) {
			SharedPreferencesUtils spfu = SharedPreferencesUtils.newInstance(context, SPF_FILE_SDKINFO);
			
			if(allUpdateSdkList.isEmpty()) {
				spfu.saveString(SPF_KEY_DATA, "").commit();;
			} else {
				StringBuffer buffer = new StringBuffer();
				buffer.append("[");
				for(int i = 0; i < allUpdateSdkList.size(); i++) {
					SdkInfo sdkInfo = allUpdateSdkList.get(i);
					buffer.append(sdkInfo.getJSONStr());
					buffer.append(",");
				}
				
				buffer.deleteCharAt(buffer.length() - 1);
				buffer.append("]");
				spfu.saveString(SPF_KEY_DATA, buffer.toString()).commit();;
			}
			
		}
	}
	
	public static List<SdkInfo> obtainSdkListFromPhone(Context context) {
		SharedPreferencesUtils spfu = SharedPreferencesUtils.newInstance(context, SPF_FILE_SDKINFO);
		List<SdkInfo> sdkInfoList = new ArrayList<SdkInfo>();
		
		String sdkInfoStr = spfu.getString(SPF_KEY_DATA, null);
		
		try {
			JSONArray jsonArr = new JSONArray(sdkInfoStr);
			int len = jsonArr.length();
			for(int i = 0; i < len; i++) {
				JSONObject sdkObj = jsonArr.getJSONObject(i);
				SdkInfo sdkInfo = SdkInfo.getSdkInfoByJsonStr(sdkObj);
				if(sdkInfo != null)
					sdkInfoList.add(sdkInfo);
			}
		} catch (Exception e) {
			TAGS.log("obtainSdkListFromPhone->ex:" + e.toString());
		}
		
		return sdkInfoList;
	}
	
	protected abstract SDK_TYPE getSdkType();
	
	public SdkBuilder buildVersion(int version) {
		this.version = version;
		return this;
	}
	
	public SdkBuilder buildDownloadUri(String downloadUri) {
		this.downloadUri = downloadUri;
		return this;
	}
	
	public SdkBuilder buildSize(long size) {
		this.size = size;
		return this;
	}
	
	public SdkBuilder buildState(int state) {
		this.state = (state == 0 ? 0 : 1);
		return this;
	}
	
	public SdkInfo buildSdkInfo() {
		SdkInfo sdkInfo = new SdkInfo();
		sdkInfo.setType_code(getSdkType().ordinal());
		sdkInfo.setType_str(SdkContant.SDK_TYPE.getStr(getSdkType()));
		sdkInfo.setVersion(version);
		sdkInfo.setDownloadUri(downloadUri);
		sdkInfo.setSize(size);
		sdkInfo.setState(state);
		return sdkInfo;
	}
	
	
	
}
