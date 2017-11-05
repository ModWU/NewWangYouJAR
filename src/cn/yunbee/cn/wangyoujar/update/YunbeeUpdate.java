package cn.yunbee.cn.wangyoujar.update;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cn.yunbee.cn.wangyoujar.joggle.ICheckUpdate;
import cn.yunbee.cn.wangyoujar.joggle.IUpdate;
import cn.yunbee.cn.wangyoujar.update.SdkContant.SDK_TYPE;
import dy.compatibility.state.SDKStateContext;
import dy.compatibility.work.Contant;
import dy.compatibility.work.Utils;

public class YunbeeUpdate implements IUpdate {
	
	private SdkDBHelper dbHelper;
	private Activity mActivity;
	
	//�Ƿ����ڸ���sdk
	static volatile boolean isUpdateDoing = false;
	static final Object UPDATE_LOCK = new Object();
	
	private static final String SPF_UPDATE_FILE = "dy_spf_update";
	private static final String FLAG_LIBS_SDK_ADD = "sdkLibsFlag";
	
	private YunbeeUpdate(Activity activity) {
		dbHelper = SdkDBHelper.getInstance(activity);
		mActivity = activity;
	}
	
	public static YunbeeUpdate newInstance(Activity activity) {
		return new YunbeeUpdate(activity);
	}
	
	
	private void doTask(final List<SdkInfo> allUpdateSdkList) {
		TAGS.log("------------------------YunbeeUpdate-doTask----------------------------");
		if(mActivity.isFinishing()) {
			TAGS.log("mActivity is finished! ��ֹ����!");
			return;
		}
		if(allUpdateSdkList == null || allUpdateSdkList.isEmpty()) {
			TAGS.log("allUpdateSdkList: " + allUpdateSdkList  + ", Ϊ������, ��ֹ���أ�");
			return;
		}
		
		
		isUpdateDoing = true;
		UpdateActivity.prepareSdksAndLibs(allUpdateSdkList);
		Util.replaceClassLoader_api17(getClass().getClassLoader(), mActivity);
		mActivity.startActivity(new Intent(mActivity, UpdateActivity.class));
	}
	
	
	private void callCheckUpdateListener(final ICheckUpdate listener, final boolean isChecked) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				listener.checkFinished(isChecked);
			}
		
		});
	}
	

	
	private void sendTaskUpdate(final List<SdkInfo> allUpdateSdkList, final ICheckUpdate listener) {
		TAGS.log("-------------------------sendTaskUpdate----------------------------");
		if(allUpdateSdkList == null || allUpdateSdkList.isEmpty()) {
			if(listener != null) {
				callCheckUpdateListener(listener, false);
			}
		} else {
			UpdateActivity.setCheckWyListener(listener);
			doTask(allUpdateSdkList);
		}
	}
	

	private void checkUpdateFromServer(final ICheckUpdate listener) {
		TAGS.log("-------------------------checkUpdateFromServer----------------------------");
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (UPDATE_LOCK) {
					if(isUpdateDoing) {
						try {
							UPDATE_LOCK.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					List<SdkInfo> allUpdateSdkList = new ArrayList<SdkInfo>();
					 
					requestInfo(allUpdateSdkList);
					
					sendTaskUpdate(allUpdateSdkList, listener);
				}
				
			}
			
		}).start();
		
	}

	
	//��������Ψһһ��
	private void requestInfo(List<SdkInfo> allUpdateSdkList) {
		TAGS.log("-------------------------requestInfo----------------------------");
		String update_url = SdkContant.AddressUrl.UPDATE_URL;
		String result = HttpUtils.sendGet(update_url);
		TAGS.log("requestInfoOnWy->result: " + result);
		List<SdkInfo> tmpUpdateSdkList = new ArrayList<SdkInfo>();
		try {
			JSONObject jsonObj = new JSONObject(result);
			int code = jsonObj.getInt("code");
			if(code == 0) {
				TAGS.log("requestInfo->��ȡ���ݳɹ���");
				JSONArray sdkObjs = jsonObj.getJSONArray("sdks");
				int sdkObjLen = sdkObjs.length();
				for(int i = 0; i < sdkObjLen; i++) {
					SdkInfo sdkInfo = new SdkInfo();
					
					
					JSONObject sdkObj = sdkObjs.getJSONObject(i);
					String sdkType = sdkObj.getString("sdkType");
					int version = sdkObj.getInt("version");
					String downloadUri = sdkObj.getString("downloadUri");
					
					TAGS.log("requestInfo->sdkType:" + sdkType);
					TAGS.log("requestInfo->version:" + version);
					TAGS.log("requestInfo->downloadUri:" + downloadUri);
					
					SDK_TYPE sdk_type = SDK_TYPE.getType(sdkType);
					
					sdkInfo.copy(SdkBuilder.build(mActivity, sdk_type).buildSdkInfo());
					TAGS.log("requestInfo->sdkInfo1:" + sdkInfo.toString());
					SdkInfo tmpQuerySdk = dbHelper.querySdk(sdk_type.ordinal());
					if(tmpQuerySdk != null)
						sdkInfo.copy(tmpQuerySdk);
					TAGS.log("requestInfo->sdkInfo2:" + sdkInfo.toString());
					
					if(version > sdkInfo.getVersion()) {
						sdkInfo.setVersion(version);
						sdkInfo.setDownloadUri(downloadUri);
						tmpUpdateSdkList.add(sdkInfo);
					}
					
				}
				
				TAGS.log("requestInfo->tmpUpdateSdkList: " + tmpUpdateSdkList.toString());
				
				SdkBuilder.saveSdkDataList(mActivity, tmpUpdateSdkList);
				
				allUpdateSdkList.addAll(tmpUpdateSdkList);
				
				TAGS.log("requestInfo->dataList: " + allUpdateSdkList.toString());
				
				
			}
		} catch (Exception e) {
			TAGS.log("requestInfo->ex: " + e.toString());
		}
		
	}
	

	
	
	private boolean addSdkLibsOnFirst(SDK_TYPE sdkType) {
		if(sdkType == null) return false;
		
		SdkInfo sdkInfo = new SdkInfo();
		
		SdkBuilder sdkBuilder = SdkBuilder.build(mActivity, sdkType);
		
		sdkBuilder.buildVersion(getSdkVersion(sdkType));
		
		sdkInfo.copy(sdkBuilder.buildSdkInfo());
		
		return dbHelper.insert(sdkInfo);
	}
	
	//����
	private int getSdkVersion(SDK_TYPE sdkType) {
		int version = 1;
		//���õ�ǰ�汾
		if(sdkType == SDK_TYPE.YST) {
			version = 12;
		} else if(sdkType == SDK_TYPE.WANGYOU) {
			version = 8;
		} else if(sdkType == SDK_TYPE.AIBEI) {
			version = 2;
		} else if(sdkType == SDK_TYPE.YHXF) {
			version = 2;
		} else if(sdkType == SDK_TYPE.WEIXIN) {
			version = 2;
		}
		return version;
	}
	
	
	//����
	/*
	private int getSdkVersion(SDK_TYPE sdkType) {
		int version = 1;
		//���õ�ǰ�汾
		if(sdkType == SDK_TYPE.YST) {
			version = 2;
		} else if(sdkType == SDK_TYPE.WANGYOU) {
			version = 1;
		} else if(sdkType == SDK_TYPE.AIBEI) {
			version = 1;
		} else if(sdkType == SDK_TYPE.YHXF) {
			version = 1;
		} else if(sdkType == SDK_TYPE.WEIXIN) {
			version = 1;
		}
		return version;
	}
	*/

	@Override
	public void checkUpdate(ICheckUpdate listener) {
		checkUpdateFromServer(listener);
	}
	
	
	public boolean copyAllAssetsFile() {
		TAGS.log("-------------------YunbeeUpdate-copyAllAssetsFile---------------------");
		SharedPreferences sp = mActivity.getSharedPreferences(Contant.SHARE_FILENAME_CMPT_SDK, Context.MODE_APPEND);
		
		//ֻcopyһ�飬�мǣ�
		//boolean isCoppySuccess = spfUtils.getBoolean("assets_copy_success", false);
		boolean isCoppySuccess = sp.getBoolean("assets_copy_success", false);
		
		TAGS.log("copyAllAssetsFile->isCoppySuccess:" + isCoppySuccess);
		String realJarDir = SdkBuilder.getReal_jardir(mActivity);
		String realSoDir = SdkBuilder.getReal_sodir(mActivity);
		boolean isCopy = false;
		if(!isCoppySuccess) {
			String realOutDexDir = SdkBuilder.getReal_outdexdir(mActivity);
			String downloadJarDir = SdkBuilder.getDownload_jardir(mActivity);
			String downloadSoDir = SdkBuilder.getDownload_sodir(mActivity);
			Utils.createFileDir(realOutDexDir);
			Utils.createFileDir(realJarDir);
			Utils.createFileDir(realSoDir);
			Utils.createFileDir(downloadJarDir);
			Utils.createFileDir(downloadSoDir);
			//so�ļ������ܶ�copy��ȥ�������豸cpu����(Build.SUPPORTED_ABIS)ѡ��copy�����������ԣ���ȫ��copy����
			isCoppySuccess = SDKStateContext.copyAllAssetsFile(mActivity, Contant.ASSETS_DIR, realJarDir, realSoDir, false);
			TAGS.log("copyAllAssetsFile->copy isCoppySuccess:" + isCoppySuccess);
			//spfUtils.saveBoolean("assets_copy_success", isCoppySuccess).commit();
			sp.edit().putBoolean("assets_copy_success", isCoppySuccess).commit();
			isCopy = true;
		} else {
			TAGS.log("copyAllAssetsFile->assets�Ѿ�����Ҫ��copy��,�����û����ֶ�ɾ���ڲ��洢��");
			//���� ��Ҫ����ļ��Ƿ�����ɾ��
			isCopy = SDKStateContext.checkCopyFiles(mActivity, Contant.ASSETS_DIR, realJarDir, realSoDir);
		}
		return isCopy;
	}


	@Override
	public void tryAddSdkLibs() {
		TAGS.log("--------------------------tryAddSdkLibs---------------------------");
		synchronized (UPDATE_LOCK) {
			boolean isHasSdkInfo = dbHelper.isHasSdkInfo();
			TAGS.log("isHasSdkInfo: " + isHasSdkInfo);
			boolean isReCopy = copyAllAssetsFile();
			if(!isHasSdkInfo || isReCopy) {
				
				isHasSdkInfo = true;
				
				dbHelper.deleteAllSdk();
				//����
				addSdkLibsOnFirst(SDK_TYPE.WANGYOU);
				//����
				addSdkLibsOnFirst(SDK_TYPE.AIBEI);
				//֧����
				addSdkLibsOnFirst(SDK_TYPE.ALIPAY);
				//��ݷ��ҵ
				addSdkLibsOnFirst(SDK_TYPE.JXHY);
				//����ͨ
				addSdkLibsOnFirst(SDK_TYPE.YST);
				//΢��
				addSdkLibsOnFirst(SDK_TYPE.WEIXIN);
				//����ͨ
				addSdkLibsOnFirst(SDK_TYPE.WFT);
				//ӯ��Ѷ��
				addSdkLibsOnFirst(SDK_TYPE.YHXF);
			}
		}
	}
	
	
	
	
}
