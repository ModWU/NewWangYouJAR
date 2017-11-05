package cn.yunbee.cn.wangyoujar.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cn.yunbee.cn.wangyoujar.update.SdkContant.SDK_TYPE;
import dy.compatibility.work.Utils;

public class UpdateTask extends AsyncTask<Integer, Integer, String> {
	
	private static final String UPDATE_FINISHED = "更新完成";
	
	private Activity mActivity;
	
	private IDownloadListener mListener;
	
	public static final int BAR_MAX = 1000;
	
	private long mProgress;
	
	private long mTotalSize;
	
	private DownloadProgressBar mPb;
	
	private List<SdkInfo> mAllUpdateSdkList = new ArrayList<SdkInfo>();
	
	
	private void measureTotalSize() {
		TAGS.log("---------------------UpdateTask-measureTotalSize--------------------");
		HttpURLConnection urlConn = null;
		try {

			for(int i = 0; i < mAllUpdateSdkList.size(); i++) {
				SdkInfo sdkInfo = mAllUpdateSdkList.get(i);
				URL url = new URL(sdkInfo.getDownloadUri());
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setConnectTimeout(20000);
				urlConn.setReadTimeout(120000);
				
				
				urlConn.setRequestProperty("Range", "bytes=" + 0 + "-");// 设置获取实体数据的范围
				
				sdkInfo.setSize(urlConn.getContentLength());
				
				mTotalSize += urlConn.getContentLength();
				
				urlConn.disconnect();
			}
		} catch (Exception e) {
			TAGS.log("UpdateTask->measureTotalSize->ex:" + e.toString());
		}
		
		TAGS.log("mTotalSize: " + mTotalSize);
		
	}
	
	private void downloadZipBags() {
		TAGS.log("---------------------UpdateTask-downloadZipBags--------------------");
		TAGS.log("download files size: " + mAllUpdateSdkList.size());
		TAGS.log("download files: " + mAllUpdateSdkList);
		
		TAGS.log("mTotalSize: " + mTotalSize);
		
		HttpURLConnection urlConn = null;
		InputStream inputstream = null;
		FileOutputStream outputStream = null;
		long startPos = 0;
		long newLen = mTotalSize;
		long downloadSize = 0;
		
		downloadStart();
		
		try {
			for(int i = 0; i < mAllUpdateSdkList.size(); i++) {
				SdkInfo sdkInfo = mAllUpdateSdkList.get(i);
				SdkBuilder sdkBuilder = SdkBuilder.build(mActivity, SDK_TYPE.getType(sdkInfo.getType_str()));
				Utils.createFileDir(sdkBuilder.getDownload_zipDir());
				
				URL url = new URL(sdkInfo.getDownloadUri());
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setConnectTimeout(20000);
				urlConn.setReadTimeout(120000);
				
				urlConn.setRequestProperty("Range", "bytes=" + startPos + "-");// 设置获取实体数据的范围
				inputstream = urlConn.getInputStream();
				String downZipPath = sdkBuilder.getDownload_zipDir() + File.separator + SdkBuilder.getNameFromUri(sdkInfo.getDownloadUri());
				TAGS.log("download Zip Path" + i + ": " + downZipPath);
				outputStream = new FileOutputStream((downZipPath), false);
				byte[] buffer = new byte[1024];
				int curlen = -1;
				while ((curlen = inputstream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, curlen);
					outputStream.flush();
					downloadSize += curlen;
					
					mProgress = downloadSize;
					TAGS.log("download Progress: " + mProgress);
					float percen = mProgress / (mTotalSize * 1.0f);
					int progress = Math.round(percen * BAR_MAX);
					
					publishProgress(progress);
				}
				
				outputStream.close();
				inputstream.close();
				urlConn.disconnect();
			}
			
			TAGS.log("---------------------UpdateTask-downloadZipBags-Success!-------------------");
			return;
		} catch (Exception e) {
			TAGS.log("UpdateTask->downloadZipBags->" + e.toString());
		}
		TAGS.log("---------------------UpdateTask-downloadZipBags-fail!-------------------");
		Utils.deleteAllFileInDir(SdkBuilder.getDownload_zipRootDir(mActivity), false, false);
	}
	
	
	private void downloadStart() {
		if(mPb != null)
			mPb.post(new Runnable(){
	
				@Override
				public void run() {
					if(mListener != null)
						mListener.downloadStart();
				}
				
			});
		
	}

	public UpdateTask(Activity activity, IDownloadListener listener, DownloadProgressBar pb, List<SdkInfo> allUpdateSdkList) {
		mActivity = activity;
		mListener = listener;
		mPb = pb;
		mAllUpdateSdkList.addAll(allUpdateSdkList);
		
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mPb.setMax(BAR_MAX);
		
		//播放一个小动画
		mPb.startRunBall();
	}

	@Override
	protected String doInBackground(Integer... params) {
		TAGS.log("----------------------UpdateTask->doInBackground-------------------");
		measureTotalSize();
		synchronized (UpdateTask.class) {
			downloadZipBags();
		}
		//假设隔几秒就跟新一次
		return UPDATE_FINISHED;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		int progress = Math.round(values[0] * 1.0f / BAR_MAX * 100);
		mPb.setProgressText("更新文件中" + progress + "%");
		mPb.setProgress(values[0]);
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(mListener != null)
			mListener.downloadFinished();
	}

}
