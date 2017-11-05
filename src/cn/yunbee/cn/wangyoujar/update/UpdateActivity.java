package cn.yunbee.cn.wangyoujar.update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;
import cg.yunbee.cn.wangyoujar.utils.Zip;
import cn.yunbee.cn.wangyoujar.joggle.ICheckUpdate;
import cn.yunbee.cn.wangyoujar.update.SdkContant.SDK_TYPE;
import dy.compatibility.state.SDKStateContext;
import dy.compatibility.work.Utils;

public class UpdateActivity extends Activity implements IDownloadListener {
	
	private DownloadProgressBar pb;
	
	private SdkDBHelper dbHelper;
	
	private static ICheckUpdate mUpdateListener;
	
	private static List<SdkInfo> mAllUpdateSdkList = new ArrayList<SdkInfo>();
	
	public static void prepareSdksAndLibs(List<SdkInfo> allUpdateSdkList) {
		mAllUpdateSdkList.clear();
		mAllUpdateSdkList.addAll(allUpdateSdkList);
	}
	
	public static void setCheckWyListener(ICheckUpdate listener) {
		mUpdateListener = listener;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(ResourceUtils.getLayoutId(this, "dy_update_activity"));
		
		
		ViewGroup viewGroup = (ViewGroup) findViewById(ResourceUtils.getId(this, "dybgLay"));
		
		pb = new DownloadProgressBar(this);//(DownloadProgressBar)findViewById(ResourceUtils.getId(this, "pgBar"));	这种方式加载的类 所属加载器不一致
		//pb.setVisibility(View.GONE);
		viewGroup.addView(pb);
		pb.setProgressText("更新准备中，请稍后...");
		
		dbHelper = SdkDBHelper.getInstance(this);
		
		startUpdateTask();
	}

	private void startUpdateTask() {
		new UpdateTask(this, this, pb, mAllUpdateSdkList).execute();
		
	}

	/*private void computerLibUris() {
		allLibsPaths.clear();
		Collection<List<LibInfo>> allLibs = sdkMap.values();
		for(List<LibInfo> perLibs : allLibs)
			for(LibInfo lib : perLibs) {
				String uri = lib.getDownload_path();
				if(uri != null)
					allLibsPaths.add(uri);
			}
	}*/
	
	@Override
	public void onBackPressed() {
	}


	@Override
	public void downloadFinished() {
		
		final boolean isUpdate = checkLibs();
		
		if(isUpdate) {
		
			pb.setDoOtherOnFinished(true, "正在复制文件，请稍后...");
			
			new Thread(new Runnable() {
	
				@Override
				public void run() {
					synchronized (UpdateActivity.class) {
						copyLibs();
					}
					pb.post(new Runnable() {
	
						@Override
						public void run() {
							pb.setProgressText("复制完成");
							pb.finish();
							if(mUpdateListener != null)
								mUpdateListener.checkFinished(true);
							finish();
							
							awakeUpdateLock();
						}
						
					});
				}
				
			}).start();
		} else {
			pb.setProgressText("下载结束！");
			pb.finish();
			if(mUpdateListener != null)
				mUpdateListener.checkFinished(false);
			finish();
			
			awakeUpdateLock();
		}
		
		
	}
	
	private void awakeUpdateLock() {
		synchronized (YunbeeUpdate.UPDATE_LOCK) {
			YunbeeUpdate.isUpdateDoing = false;
			YunbeeUpdate.UPDATE_LOCK.notify();
		}
	}

	private boolean checkLibs() {
		for(SdkInfo perSdk : mAllUpdateSdkList) {
			SdkBuilder sdkBuilder = SdkBuilder.build(this, SDK_TYPE.getType(perSdk.getType_str()));
			String zipDir = sdkBuilder.getDownload_zipDir();
			File dirf = new File(zipDir);
			File fs[] = dirf.listFiles();
			int fileCount = fs == null ? 0 : fs.length;
			if(fileCount > 0) 
				return true;
		}
		return false;
	}

	private void copyLibs() {
		TAGS.log("------------------copyLibs-------------------------");
		for(SdkInfo perSdk : mAllUpdateSdkList) {
			TAGS.log("copyLibs->perSdk: " + perSdk);
			SdkBuilder sdkBuilder = SdkBuilder.build(this, SDK_TYPE.getType(perSdk.getType_str()));
			String zipFilePath = sdkBuilder.getDownload_zipDir() + File.separator + SdkBuilder.getNameFromUri(perSdk.getDownloadUri());
			String zipOutDir = sdkBuilder.getDownload_zipDir();
			
			TAGS.log("copyLibs->zipFilePath: " + zipFilePath);
			
			TAGS.log("copyLibs->zipOutDir: " + zipOutDir);
			
			Zip.unZip(zipFilePath, zipOutDir);
			
			String sdkDownloadJarPath = zipOutDir + File.separator + "jar";
			String sdkDownloadSoPath = zipOutDir + File.separator + "so";
			
			TAGS.log("copyLibs->sdkDownloadJarPath: " + sdkDownloadJarPath);
			TAGS.log("copyLibs->sdkDownloadSoPath: " + sdkDownloadSoPath);
			
			String sdkRealJarPath = sdkBuilder.getReal_jardir();
			String sdkRealSoPath = sdkBuilder.getReal_sodir();
			
			TAGS.log("copyLibs->sdkRealJarPath: " + sdkRealJarPath);
			TAGS.log("copyLibs->sdkRealSoPath: " + sdkRealSoPath);
			//-----
			//处理so文件
			String soCpuType = Utils.chooseBestLibraryFromAssets(sdkDownloadSoPath);
			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
			TAGS.log("SDKType:" + perSdk.getType_str() + ", copyLibs->最终获取到的Cpu架构类型为: " + soCpuType);
			
			Utils.deleteAllFileInDir(sdkRealJarPath, false, false);
			
			boolean isCopySuccess = true;
			
			if(soCpuType != null) {
				Utils.deleteAllFileInDir(sdkRealSoPath, false, false);
				String sdkDownloadSoCpuTypePath = sdkDownloadSoPath + File.separator + soCpuType;
				TAGS.log("sdkDownloadSoCpuTypePath: " + sdkDownloadSoCpuTypePath);
				isCopySuccess &= Utils.copyFile(sdkDownloadSoCpuTypePath, sdkRealSoPath);
				TAGS.log("copy so success: " + isCopySuccess);
			}
			
			isCopySuccess &= Utils.copyFile(sdkDownloadJarPath, sdkRealJarPath);
			
			TAGS.log("copyLibs->isCopySuccess: " + isCopySuccess);
			
			if(isCopySuccess) {
				//更新数据库信息
				TAGS.log("copyLibs->更新数据库信息: " + perSdk.getType_str());
				SDKStateContext.saveLocalJarFlagsToPhone(this, SDKStateContext.getCmptType(SDK_TYPE.getType(perSdk.getType_str())), sdkBuilder.getReal_jardir());
				dbHelper.update(perSdk);
			}
			
			Utils.deleteAllFileInDir(zipOutDir, false, false);
			
		}
	}


	@Override
	public void downloadStart() {
		pb.setVisibility(View.VISIBLE);
	}
}
