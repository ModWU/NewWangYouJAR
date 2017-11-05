package cn.yunbee.cn.wangyoujar.update;

import org.json.JSONException;
import org.json.JSONObject;

import cg.yunbee.cn.wangyoujar.pojo.TAGS;

public class SdkInfo {
	private int sdkno;
	private int type_code;
	private String type_str;
	private int version;
	private long size;
	
	/*private String downloadZipDir;
	private String downloadJarDir;
	private String downloadSoDir;
	private String realJarDir;
	private String realSoDir;
	private String realOutDexDir;*/
	
	private String downloadUri;
	private String last_update_time;
	private int state;
	
	
	public SdkInfo() {
	}
	
	public String getJSONStr() {
		String downloadUri = this.downloadUri;
		if(downloadUri == null) downloadUri = "";
		
		String last_update_time = this.last_update_time;
		if(last_update_time == null) last_update_time = "";
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		buffer.append("\"sdkno\":" +  sdkno + ",");
		buffer.append("\"type_code\":" + type_code + ", ");
		buffer.append("\"type_str\":" + "\"" + type_str + "\", ");
		buffer.append("\"version\":" + version + ", ");
		buffer.append("\"size\":" + size + ", ");
		buffer.append("\"downloadUri\":" + "\"" + downloadUri + "\", ");
		buffer.append("\"last_update_time\":" + "\"" + last_update_time + "\", ");
		buffer.append("\"state\":" + state);
		buffer.append("}");
		return buffer.toString();
	}
	
	public static SdkInfo getSdkInfoByJsonStr(JSONObject jsonObj) {
		SdkInfo sdkInfo = null;
		try {
			int sdkno = jsonObj.getInt("sdkno");
			int type_code = jsonObj.getInt("type_code");
			String type_str = jsonObj.getString("type_str");
			int version = jsonObj.getInt("version");
			long size = jsonObj.getLong("size");
			String downloadUri = jsonObj.getString("downloadUri");
			String last_update_time = jsonObj.getString("last_update_time");
			int state = jsonObj.getInt("state");
			sdkInfo = new SdkInfo(sdkno, type_code, type_str, version, size, downloadUri, last_update_time, state);
		} catch (JSONException e) {
			TAGS.log("getSdkInfoByJsonStr->ex:" + e.toString());
		}
		
		return sdkInfo;
	}
	
	public void copy(SdkInfo sdkInfo) {
		if(sdkInfo == null) return;
		sdkno = sdkInfo.sdkno;
		type_code = sdkInfo.type_code;
		type_str = sdkInfo.type_str;
		version = sdkInfo.version;
		size = sdkInfo.size;
		downloadUri = sdkInfo.downloadUri;
		last_update_time = sdkInfo.last_update_time;
		state = sdkInfo.state;
	}

	public String getDownloadUri() {
		return downloadUri;
	}

	public void setDownloadUri(String downloadUri) {
		this.downloadUri = downloadUri;
	}

	public int getSdkno() {
		return sdkno;
	}
	public void setSdkno(int sdkno) {
		this.sdkno = sdkno;
	}
	public int getType_code() {
		return type_code;
	}
	public void setType_code(int type_code) {
		this.type_code = type_code;
	}
	public String getType_str() {
		return type_str;
	}
	public void setType_str(String type_str) {
		this.type_str = type_str;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getLast_update_time() {
		return last_update_time;
	}
	public void setLast_update_time(String last_update_time) {
		this.last_update_time = last_update_time;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public SdkInfo(int sdkno, int type_code, String type_str, int version, long size, String downloadUri,
			String last_update_time, int state) {
		super();
		this.sdkno = sdkno;
		this.type_code = type_code;
		this.type_str = type_str;
		this.version = version;
		this.size = size;
		this.downloadUri = downloadUri;
		this.last_update_time = last_update_time;
		this.state = state;
	}

	@Override
	public String toString() {
		return "SdkInfo [sdkno=" + sdkno + ", type_code=" + type_code + ", type_str=" + type_str + ", version="
				+ version + ", size=" + size + ", downloadUri=" + downloadUri + ", last_update_time=" + last_update_time
				+ ", state=" + state + "]";
	}

	


}
